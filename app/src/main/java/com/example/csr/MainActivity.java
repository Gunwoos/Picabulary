package com.example.csr;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csr.utils.AudioWriterPCM;
import com.example.csr.utils.Translation;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionException;
import com.naver.speech.clientapi.SpeechRecognitionListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.naver.speech.clientapi.SpeechRecognizer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "udvt44fz7r";
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private TextView txtResult;
    private Button btnStart;
    private Button btnStart2;
    private Button btnMyword;
    private Button btnTest;
    private Button btnAdd;
    private String mResult;
    private AudioWriterPCM writer;

    /* 텍스트를 영어로 변환하기 위한 소스코드입니다. */
    private TextView translatedView;
    private TextView before_Translated;
    private String textForTranslated;
    private String translated;
    private String address = "https://openapi.naver.com/v1/papago/n2mt";
    private static boolean startKo = true;

    DBHelper dbHelper;
    SQLiteDatabase db = null;


    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {

        private String forTranslated;

        // forTranslated는 번역이 완료된 TextView에 띄우기 위한 용도, 이 번역 결과를 점자를 만드는 데 사용함
        BackgroundTask(String forTranslated) {
            this.forTranslated = forTranslated;
        }

        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            translated = request(address, forTranslated);
            return null;
        }

        protected void onPostExecute(Integer a) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(translated);
            if (element.getAsJsonObject().get("errorMessage") != null) {
                System.out.println("번역 오류가 발생했습니다. " +
                        "[오류 코드: " + element.getAsJsonObject().get("errorCode").getAsString() + "]");
            } else if (element.getAsJsonObject().get("message") != null) {
                // 번역 결과 출력

                String result = element.getAsJsonObject().get("message").getAsJsonObject().get("result")
                        .getAsJsonObject().get("translatedText").getAsString();

                translatedView.setText(result); //번역 후 문장
                before_Translated.setText(forTranslated); //번역 전 문장
            }

        }

    }

    private String request(String urlStr, String textForTranslated) {
        StringBuilder output = new StringBuilder();
        String clientId = "wVCRXR41BQ3xx0VDCW65"; // 애플리케이션 클라이언트 아이디 값";
        String clientSecret = "m7mEqSecUi"; // 애플리케이션 클라이언트 시크릿 값";
        try {
            String text = URLEncoder.encode(textForTranslated, "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            // 포스트 방식으로 파라미터를 전달합니다.

            // 시작 지점이 한국어이냐 아니냐에 따라 번역 시작과 끝 부분을 달리함
            String source = startKo ? "ko" : "en";
            String dest = !startKo ? "ko" : "en";
            String postParams = "source=" + source + "&target=" + dest + "&text=" + text;

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                output.append(inputLine);
            }
            br.close();
        } catch (Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }
        return output.toString();
    }
    /* 여기까지 텍스트를 영어로 변환 */

    // 음성 인식 메시지를 처리합니다.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            // 음성 인식을 시작할 준비가 완료된 경우
            case R.id.clientReady:
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;
            // 현재 음성 인식이 진행되고 있는 경우
            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;
            // 처리가 되고 있는 도중에 결과를 받은 경우
            case R.id.partialResult:
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;
            // 최종 인식이 완료되면 유사 결과를 모두 보여줍니다.
            case R.id.finalResult:
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();

                StringBuilder strBuf = new StringBuilder();
                // 전달 받은 모든 문자열을 차례대로 출력합니다.
                for (String result : results) {
                    strBuf.append(result);
//                    strBuf.append(" ");
//                    strBuf.append(WordClass.decompose(result, true));
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();
                txtResult.setText(mResult);
                textForTranslated = mResult;

                new BackgroundTask(textForTranslated).execute();
                break;
            // 인식 오류가 발생한 경우
            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
            // 음성 인식 비활성화 상태인 경우
            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtResult = (TextView) findViewById(R.id.txt_result);
        translatedView = (TextView) findViewById(R.id.translated);
        before_Translated = (TextView) findViewById(R.id.before_translated);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart2 = (Button) findViewById(R.id.btn_start2);
        btnMyword = (Button) findViewById(R.id.btn_myword);
        btnTest = (Button) findViewById(R.id.btn_test);
        btnAdd = (Button) findViewById(R.id.add);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        try {
            Intent intent = this.getIntent(); //intent 라는 것을 받아서 읽어드립니다.
            String result = intent.getStringExtra("result");
            Log.e(TAG, "결과값 받기" + result);
            if(!result.equals("") || result != null) {
                new BackgroundTask(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 사용자의 OS 버전이 마시멜로우 이상인지 체크합니다. */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /* 사용자 단말기의 권한 중 권한이 허용되어 있는지 체크합니다. */
                    int permissionResult = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
                    /* 권한이 없을 때 */
                    if (permissionResult == PackageManager.PERMISSION_DENIED) {
                        /* 사용자가 권한을 한번이라도 거부한 적이 있는 지 확인합니다. */
                        if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("권한이 필요합니다.")
                                    .setMessage("이 기능을 사용하기 위해서는 권한이 필요합니다. 계속하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                                            }
                                        }
                                    })
                                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(MainActivity.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                        // 최초로 권한을 요청하는 경우
                        else {
                            // 권한을 요청합니다.
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                        }
                    }
                    /* 권한이 있는 경우 */
                    else {
                        /* 음성 인식 기능을 처리합니다. */
                        startRecognize();
                    }
                }
                /* 사용자의 OS 버전이 마시멜로우 이하일 떄 */
                else {
                    /* 음성 인식 기능을 처리합니다. */
                    startRecognize();
//                    if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
//                        mResult = "";
//                        txtResult.setText("Connecting...");
//                        btnStart.setText(R.string.str_stop);
//                        naverRecognizer.recognize(startKo);
//                    } else {
//                        Log.d(TAG, "stop and wait Final Result");
//                        btnStart.setEnabled(false);
//                        naverRecognizer.getSpeechRecognizer().stop();
//                    }
                }
            }
        });

        //사진->텍스트변환
        btnStart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 언어를 고르라는 Dialog 보여주기
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("언어 선택").setMessage("언어를 고르세요").setPositiveButton("한국어->영어", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // Dialog에서 무언가를 골랐으면 시작지점을 한국어로 설정했는지 체크 후 녹음 분석 시작
                        startKo = true; // 분석할때 시작을 한국어로 했으면 true
                        Intent intent = new Intent(getApplicationContext(), photoRecognizer.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("영어->한국어", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // Dialog에서 무언가를 골랐으면 시작지점을 한국어로 설정했는지 체크 후 녹음 분석 시작
                        startKo = false;
                        Intent intent = new Intent(getApplicationContext(), photoRecognizer.class);
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        btnMyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Myword.class);
                startActivity(intent);
            }
        });
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 언어를 고르라는 Dialog 보여주기
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("테스트 선택").setMessage("종류를 고르세요").setPositiveButton("전체", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // Dialog에서 무언가를 골랐으면 시작지점을 한국어로 설정했는지 체크 후 녹음 분석 시작
                        Intent intent = new Intent(getApplicationContext(), Test.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("자주 틀린단어", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // Dialog에서 무언가를 골랐으면 시작지점을 한국어로 설정했는지 체크 후 녹음 분석 시작
                        Intent intent = new Intent(getApplicationContext(), Verytest.class);
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dbHelper.onUpgrade(db, 0, 1); //초기화
                String name = before_Translated.getText().toString();
                String info = translatedView.getText().toString();
                db.execSQL("INSERT INTO tableName VALUES ('" + name + "', '" + info + "');");
                Toast.makeText(getApplicationContext(), "추가 성공", Toast.LENGTH_SHORT).show();
            }
        });

//        번역된 글자가 있을 경우에 점자로 변환을 한다.
        if (Translation.text != null) {
            new BackgroundTask(Translation.text).execute();
            Translation.text = null;
            Log.d(TAG, "onCreate: Translate to language is korean? = " + startKo);
        }
    }

    private void startRecognize() {
        if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
            mResult = "";
            txtResult.setText("Connecting...");
            btnStart.setText(R.string.str_stop);


            // 언어를 고르라는 Dialog 보여주기
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("언어 선택").setMessage("언어를 고르세요").setPositiveButton("한국어->영어", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { // Dialog에서 무언가를 골랐으면 시작지점을 한국어로 설정했는지 체크 후 녹음 분석 시작
                    startKo = true; // 분석할때 시작을 한국어로 했으면 true
                    naverRecognizer.recognize(true);
                }
            }).setNegativeButton("영어->한국어", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { // Dialog에서 무언가를 골랐으면 시작지점을 한국어로 설정했는지 체크 후 녹음 분석 시작
                            startKo = false;
                            naverRecognizer.recognize(false);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            Log.d(TAG, "stop and wait Final Result");
            btnStart.setEnabled(false);
            naverRecognizer.getSpeechRecognizer().stop();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 음성인식 서버 초기화를 진행합니다.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResult = "";
        txtResult.setText("");
        btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 음성인식 서버를 종료합니다.
        naverRecognizer.getSpeechRecognizer().release();
    }

    // SpeechRecognizer 쓰레드의 메시지를 처리하는 핸들러를 정의합니다.
    static class RecognitionHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        RecognitionHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}


// 2. SpeechRecognitionListener를 상속한 클래스를 정의합니다.
class NaverRecognizer implements SpeechRecognitionListener {
    /* 텍스트를 영어로 변환하기 위한 소스코드입니다. */
    public TextView translatedView;
    public String textForTranslated;
    public String translated;
    public String address = "https://openapi.naver.com/v1/papago/n2mt";

    private final static String TAG = NaverRecognizer.class.getSimpleName();
    private Handler mHandler;
    private SpeechRecognizer mRecognizer;

    public NaverRecognizer(Context context, Handler handler, String clientId) {
        this.mHandler = handler;
        try {
            mRecognizer = new SpeechRecognizer(context, clientId);
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
        mRecognizer.setSpeechRecognitionListener(this);
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return mRecognizer;
    }

    public void recognize(boolean startKo) {
        try {
            // 시작을 한국어로 했으면 한국어, 영어로 했으면 언어 타입을 영어로 바꿔준다.
            mRecognizer.recognize(new SpeechConfig(startKo ? SpeechConfig.LanguageType.KOREAN : SpeechConfig.LanguageType.ENGLISH, SpeechConfig.EndPointDetectType.AUTO));
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
    }

    @Override
    @WorkerThread
    public void onInactive() {
        Message msg = Message.obtain(mHandler, R.id.clientInactive);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onReady() {
        Message msg = Message.obtain(mHandler, R.id.clientReady);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onRecord(short[] speech) {
        Message msg = Message.obtain(mHandler, R.id.audioRecording, speech);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onPartialResult(String result) {
        Message msg = Message.obtain(mHandler, R.id.partialResult, result);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onEndPointDetected() {
        Log.d(TAG, "Event occurred : EndPointDetected");
    }

    @Override
    @WorkerThread
    public void onResult(SpeechRecognitionResult result) {
        Message msg = Message.obtain(mHandler, R.id.finalResult, result);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onError(int errorCode) {
        Message msg = Message.obtain(mHandler, R.id.recognitionError, errorCode);
        msg.sendToTarget();
    }

    @Override
    public void onEndPointDetectTypeSelected(SpeechConfig.EndPointDetectType epdType) {

    }


}