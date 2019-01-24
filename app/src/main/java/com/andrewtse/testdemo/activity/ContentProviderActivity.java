package com.andrewtse.testdemo.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andrewtse.testdemo.R;
import java.util.ArrayList;

public class ContentProviderActivity extends AppCompatActivity {

    @BindView(R.id.lv_result)
    ListView mLvResult;

    private ArrayList<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);
        ButterKnife.bind(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        mLvResult.setAdapter(adapter);

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
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mList.add(cursor.getInt(0) + " --- " + cursor.getString(1));
                System.out.println(cursor.getInt(0) + " --- " + cursor.getString(1));
            }
            cursor.close();
        }

        /**
         * 对job表进行操作
         */
        Uri uriJob = Uri.parse("content://com.andrew.mycontentprovider/job");

        ContentValues values2 = new ContentValues();
        values2.put("_id", 3);
        values2.put("job", "NBA Player");

        ContentResolver resolver2 = getContentResolver();
        resolver2.insert(uriJob, values2);

        Cursor cursor2 = resolver2.query(uriJob, new String[]{"_id", "job"}, null, null, null);
        if (cursor2 != null) {
            while (cursor2.moveToNext()) {
                mList.add(cursor2.getInt(0) + " --- " + cursor2.getString(1));
            }
            cursor2.close();
        }
        adapter.notifyDataSetChanged();
    }
}
