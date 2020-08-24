package com.example.csr;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Myword extends AppCompatActivity {


    EditText editText, editText2;
    ListView listView, listView2, listView3;

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;
    ArrayAdapter adapter, adapter2, adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myword);

        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        listView = findViewById(R.id.listView);
        listView2 = findViewById(R.id.listView2);
        listView3 = findViewById(R.id.listView3);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        registerForContextMenu(listView);
    }


    public void listUpdate(View v) {
        cursor = db.rawQuery("SELECT * FROM tableName", null);
        startManagingCursor(cursor);    //엑티비티의 생명주기와 커서의 생명주기를 같게 한다.

        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1);
        adapter2 = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1);
        adapter3 = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1);

        while (cursor.moveToNext()) {
            adapter.add(cursor.getString(0));
            adapter2.add(cursor.getString(1));
            adapter3.add(cursor.getInt(2));
        }

        /*
        cursor.moveToFirst();
        cursor.moveToPrevious();
        cursor.moveToPosition(2);
        */

        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);
        listView3.setAdapter(adapter3);
    }


    public void insert(View v) {
        String name = editText.getText().toString();
        String info = editText2.getText().toString();
        db.execSQL("INSERT INTO tableName VALUES ('" + name + "', '" + info + "', 0);");


        Toast.makeText(getApplicationContext(), "추가 성공", Toast.LENGTH_SHORT).show();

        editText.setText("");
        editText2.setText("");
    }

    public void delete(View v) {
        String info = editText2.getText().toString();
        db.execSQL("DELETE FROM tableName WHERE info = '" + info + "';");

    }



}