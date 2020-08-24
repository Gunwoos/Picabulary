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

public class Test extends AppCompatActivity { //전체 테스트


    EditText editText;

    private TextView test1;
    private TextView test2;
    private Button btn;

    static int count; // 총 튜플 갯수
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

        Cursor mCount = db.rawQuery("SELECT count(*) FROM tableName", null); // 전체 튜플 갯수
        mCount.moveToFirst();
        count= mCount.getInt(0); // count = 갯수
        mCount.close();

        if(count == 0) {
            Toast.makeText(getApplicationContext(), "저장된 단어가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();

        }
        else {
            cursor = db.rawQuery("SELECT * FROM tableName ORDER BY RANDOM()", null); //랜덤으로 생성
            startManagingCursor(cursor);    //엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
            String name = "";
            String name2 = "";


            cursor.moveToFirst();
            name = cursor.getString(0);
            name2 = cursor.getString(1);
            test1.setText(name);
        }
    //    test2.setText("갯수 : "+ count);
    //    test2.setText("비~밀");

//        registerForContextMenu(listView);
    }


    public void listUpdate(View v) {
        //   int count = cursor.getCount();
        String name = "";
        String name2 = "";
        name2 = cursor.getString(1);
        String answer = editText.getText().toString();
        i++;

        if (i == count) { //마지막 문제일 경우
            if (name2.contains(answer)) { //마지막문제 맞았을경우
                if(cursor.getInt(2)>0) {
                    int Itemp = cursor.getInt(2);
                    db.execSQL("UPDATE tableName SET num = " + (Itemp - 1) + " WHERE name='" + cursor.getString(0) + "';");
                }
                o++;
            }
            else{ //마지막문제 틀렸을경우
                if(cursor.getInt(2)<6) { //틀린 횟수는 최대 6번
                    int Itemp = cursor.getInt(2);
                    db.execSQL("UPDATE tableName SET num = " + (Itemp + 1) + " WHERE name='" + cursor.getString(0) + "';");
                }
                x++;
            }
            test1.setText("맞은갯수 : "+ o);
            test2.setText("틀린갯수 : "+ x);
            editText.setVisibility(View.INVISIBLE);
            btn.setText("종료하기");
            Toast.makeText(getApplicationContext(), "수고하셨습니다", Toast.LENGTH_SHORT).show();
        }
        else if (i == count+1){ //종료하기 누르면 종료
            o = 0;
            x = 0;
            count = 0;
            i = 0;
            finish();
        }
        else{ //마지막 문제 전까지
            if (name2.contains(answer)) { //맞았을 경우
                if(cursor.getInt(2)>0) {
                    int Itemp = cursor.getInt(2);
                    db.execSQL("UPDATE tableName SET num = " + (Itemp - 1) + " WHERE name='" + cursor.getString(0) + "';");
                }

                cursor.moveToNext();
                name = cursor.getString(0);
                name2 = cursor.getString(1);
                test1.setText(name);

                //Toast.makeText(getApplicationContext(), "맞았습니다", Toast.LENGTH_SHORT).show();
                o++;
            }
            else { //틀렸을 경우
                if(cursor.getInt(2)<6) { //틀린 횟수는 최대 6번
                    int Itemp = cursor.getInt(2);
                    db.execSQL("UPDATE tableName SET num = " + (Itemp + 1) + " WHERE name='" + cursor.getString(0) + "';");
                }
                cursor.moveToNext();
                name = cursor.getString(0);
                name2 = cursor.getString(1);
                test1.setText(name);
                //Toast.makeText(getApplicationContext(), "틀렸습니다", Toast.LENGTH_SHORT).show();
                x++;
            }

        }
    }
}