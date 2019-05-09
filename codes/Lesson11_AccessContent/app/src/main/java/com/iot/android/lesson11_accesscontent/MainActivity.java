package com.iot.android.lesson11_accesscontent;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText nameEt = null;
    private EditText numberEt = null;
    private Button addBtn = null;
    private Button queryBtn = null;
    private LinearLayout titleLL = null;
    private ListView resultLv = null;
    private ContentResolver resolver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO 私有方法
        init();

        titleLL.setVisibility(View.INVISIBLE);
        resolver = getContentResolver();

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        addBtn.setOnClickListener(myOnClickListener);
        queryBtn.setOnClickListener(myOnClickListener);
    }

    private class MyOnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addBtn:
                    addPerson();
                    break;
                case R.id.queryBtn:
                    titleLL.setVisibility(View.VISIBLE);
                    ArrayList<Map<String, String>> persons = queryPerson();

                    SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
                            persons, R.layout.result,
                            new String[] { "id", "name", "num" },
                            new int[] { R.id.personid, R.id.personname, R.id.personnum });

                    resultLv.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    }

    public void addPerson() {
        String nameStr = nameEt.getText().toString();
        String numStr = numberEt.getText().toString();
        ContentValues values = new ContentValues();
        Uri rawContactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);

        long contactId = ContentUris.parseId(rawContactUri);
        System.out.println(contactId);
        values.clear();
        values.put(Data.RAW_CONTACT_ID, contactId);
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        values.put(StructuredName.GIVEN_NAME, nameStr);
        resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        values.clear();
        values.put(Data.RAW_CONTACT_ID, contactId);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER, numStr);
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        Toast.makeText(MainActivity.this, "Hello", 1000).show();
    }

    public ArrayList<Map<String, String>> queryPerson() {
        ArrayList<Map<String, String>> detail = new ArrayList<Map<String, String>>();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            Map<String, String> person = new HashMap<String, String>();
            String personId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            person.put("id", personId);
            person.put("name", name);
            Cursor nums = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + personId, null, null);
            if (nums.moveToNext()) {
                String num = nums.getString(nums.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                person.put("num", num);
            }
            nums.close();
            detail.add(person);
        }
        cursor.close();
        System.out.println(detail);
        return detail;
    }

    private void init() {
        nameEt = findViewById(R.id.nameEt);
        numberEt = findViewById(R.id.numEt);
        addBtn = findViewById(R.id.addBtn);
        queryBtn = findViewById(R.id.queryBtn);
        titleLL = findViewById(R.id.titleLL);
        resultLv = findViewById(R.id.resultLv);
    }
}
