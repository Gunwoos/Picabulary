package com.example.csr;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Verytest extends AppCompatActivity { //자주 틀린 단어 테스트


    EditText editText;

    private TextView test1;
    private TextView test2;
    private Button btn;

    static int count = 0; // 총 튜플 갯수
    static int i = 0; // 채점결과를 위한 카운트 변수
    static int o = 0; // 맞은갯수
    static int x = 0; // 틀린갯수

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;
    ArrayAdapter adapter, adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        test1 = (TextView) findViewById(R.id.test1);
        test2 = (TextView) findViewById(R.id.test2);
        editText = findViewById(R.id.answer);
        btn = (Button)findViewById(R.id.button3);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        //Cursor mCount = db.rawQuery("SELECT count(*) FROM tableName", null); // 전체 튜플 갯수
        //mCount.moveToFirst();
        //count= mCount.getInt(0); // count = 갯수
        //mCount.close();
        cursor = db.rawQuery("SELECT * FROM tableName ORDER BY RANDOM()", null); //랜덤으로 생성
        startManagingCursor(cursor);    //엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
        cursor.moveToFirst();

        //2개 이상 틀린것의 개수 count로 넣기
        if(cursor.getInt(2) > 1) { //처음 나온 단어가 2개 이상 틀린것일 경우
            count++;
            while (cursor.moveToNext()) { //다음 단어 검사 ~ 끝까지 검사
                if (cursor.getInt(2) > 1) {
                    count++;
                }
            }
        }
        else { //처음 나온 단어가 2개 이상 틀린것이 아닐 경우
            while (cursor.moveToNext()) { //다음 단어 검사 ~ 끝까지 검사
                if (cursor.getInt(2) > 1) {
                    count++;
                }
            }
        }

        if(count == 0) {
            Toast.makeText(getApplicationContext(), "자주 틀린 단어가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            String name = "";
            String name2 = "";


            cursor.moveToFirst();
            if(cursor.getInt(2) > 1) { //2번 이상 틀린 단어의 처음인 경우
                name = cursor.getString(0);
                name2 = cursor.getString(1);
                test1.setText(name);
            }
            else { //아닐 경우
                while (cursor.moveToNext()) { //2번 이상 틀린 단어의 처음 찾기
                    if(cursor.getInt(2) > 1) { //2번 이상 틀린 단어인 경우
                        name = cursor.getString(0);
                        name2 = cursor.getString(1);
                        test1.setText(name);
                        break;
                    }
                }
            }
        }
        //Toast.makeText(getApplicationContext(),String.valueOf(count) , Toast.LENGTH_SHORT).show();
        //    test2.setText("갯수 : "+ count);
        //    test2.setText("비~밀");

//        registerForContextMenu(listView);
    }


    public void listUpdate(View v) {
        if (i == count +1) { //2번 이상 틀린 단어의 시험이 끝났을 때
            o = 0;
            x = 0;
            count = 0;
            i = 0;
            finish();

        }
        else if(cursor.getInt(2) > 1){ // 2번 이상 틀린 단어인 경우
            String name = "";
            String name2 = "";
            name2 = cursor.getString(1);
            String answer = editText.getText().toString();
            i++;
            //Toast.makeText(getApplicationContext(),String.valueOf(i) , Toast.LENGTH_SHORT).show();

            if (i == count) { //마지막 문제일 경우
                if (name2.contains(answer)) { //마지막문제 맞았을경우
                    if (cursor.getInt(2) > 0) {
                        int Itemp = cursor.getInt(2);
                        db.execSQL("UPDATE tableName SET num = " + (Itemp - 1) + " WHERE name='" + cursor.getString(0) + "';");
                    }
                    o++;
                } else { //마지막문제 틀렸을경우
                    if (cursor.getInt(2) < 6) { //틀린 횟수는 최대 6번
                        int Itemp = cursor.getInt(2);
                        db.execSQL("UPDATE tableName SET num = " + (Itemp + 1) + " WHERE name='" + cursor.getString(0) + "';");
                    }
                    x++;
                }
                test1.setText("맞은갯수 : " + o);
                test2.setText("틀린갯수 : " + x);
                editText.setVisibility(View.INVISIBLE);
                btn.setText("종료하기");
                Toast.makeText(getApplicationContext(), "수고하셨습니다", Toast.LENGTH_SHORT).show();
                i++;
            }
            else { //마지막 문제 전까지
                if (name2.contains(answer)) { //맞았을 경우
                    if (cursor.getInt(2) > 0) {
                        int Itemp = cursor.getInt(2);
                        db.execSQL("UPDATE tableName SET num = " + (Itemp - 1) + " WHERE name='" + cursor.getString(0) + "';");
                    }

                    while(cursor.moveToNext()){
                        if (cursor.getInt(2) > 1) {
                            break;
                        }
                    }
                    name = cursor.getString(0);
                    name2 = cursor.getString(1);
                    test1.setText(name);

                    //Toast.makeText(getApplicationContext(), "맞았습니다", Toast.LENGTH_SHORT).show();
                    o++;
                } else { //틀렸을 경우
                    if (cursor.getInt(2) < 6) { //틀린 횟수는 최대 6번
                        int Itemp = cursor.getInt(2);
                        db.execSQL("UPDATE tableName SET num = " + (Itemp + 1) + " WHERE name='" + cursor.getString(0) + "';");
                    }
                    while(cursor.moveToNext()){
                        if (cursor.getInt(2) > 1) {
                            break;
                        }
                    }
                    name = cursor.getString(0);
                    name2 = cursor.getString(1);
                    test1.setText(name);
                    //Toast.makeText(getApplicationContext(), "틀렸습니다", Toast.LENGTH_SHORT).show();
                    x++;
                }

            }
           // Toast.makeText(getApplicationContext(),String.valueOf(i) , Toast.LENGTH_SHORT).show();

        }
        else{ //틀린 횟수가 2번 미만일 경우
            while (cursor.moveToNext()) {
                if(cursor.getInt(2) <2){
                    break;
                }
            }
        }
    }
}