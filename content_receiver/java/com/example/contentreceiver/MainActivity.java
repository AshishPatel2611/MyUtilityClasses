package com.example.contentreceiver;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    TextView resultView = null;
    CursorLoader cursorLoader;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = (TextView) findViewById(R.id.res);
    }

    public void onClickDisplayNames(View view) {
        getSupportLoaderManager().initLoader(1, null, this);
    }

    static final String PROVIDER_NAME = "com.example.contentproviders";
    static final String URL = "content://" + PROVIDER_NAME + "/ashish/*";

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        cursorLoader = new CursorLoader(this, Uri.parse(URL), null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (cursor != null) {
            cursor = null;
        }
        cursor = data;
        cursor.moveToFirst();
        StringBuilder res = new StringBuilder();
        while (!cursor.isAfterLast()) {
            res.append("\n" + cursor.getString(cursor.getColumnIndex("id")) + "-" + cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }
        cursor.close();
        resultView.setText(res);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
