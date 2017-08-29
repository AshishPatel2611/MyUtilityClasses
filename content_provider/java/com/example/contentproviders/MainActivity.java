package com.example.contentproviders;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(myContentProvider.name, ((EditText) findViewById(R.id.txtName))
                        .getText().toString());
                Uri uri = getContentResolver().insert(myContentProvider.CONTENT_URI, values);
                Log.i(TAG, "onClick: uri:: " + uri.toString());
                Toast.makeText(getBaseContext(), "New record inserted", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
