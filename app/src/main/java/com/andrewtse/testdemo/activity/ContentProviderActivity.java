package com.andrewtse.testdemo.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.andrewtse.testdemo.R;

public class ContentProviderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);

        /**
         * 对user表进行操作
         */
        Uri uriUser = Uri.parse("content://com.andrew.mycontentprovider/user");

        ContentValues values = new ContentValues();
        values.put("_id", 3);
        values.put("name", "Iverson");

        ContentResolver resolver = getContentResolver();
        resolver.insert(uriUser, values);

        Cursor cursor = resolver.query(uriUser, new String[]{"_id", "name"}, null, null, null);
        while (cursor.moveToNext()) {
            System.out.println("query: " + cursor.getInt(0) + " " + cursor.getString(1));
        }
        cursor.close();

        /**
         * 对job表进行操作
         */
        Uri uri_job = Uri.parse("content://com.andrew.mycontentprovider/job");

        ContentValues values2 = new ContentValues();
        values2.put("_id", 3);
        values2.put("job", "NBA Player");

        ContentResolver resolver2 =  getContentResolver();
        resolver2.insert(uri_job,values2);

        Cursor cursor2 = resolver2.query(uri_job, new String[]{"_id","job"}, null, null, null);
        while (cursor2.moveToNext()){
            System.out.println("query job:" + cursor2.getInt(0) +" "+ cursor2.getString(1));
        }
        cursor2.close();
    }
}
