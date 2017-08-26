package com.abc.mylibrary;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by hexagon on 31/5/17.
 */

public class LoadURL_in_WebView {

    private WebView WEB_VIEW;
    private String DOCUMENT_LINK;
    private Context CONTEXT;
    private Boolean isJavaScriptEnable = false;

    public LoadURL_in_WebView(WebView webView, String url, Boolean isJavaScriptEnable, Context context) {
        this.WEB_VIEW = webView;
        this.DOCUMENT_LINK = url;
        this.CONTEXT = context;
        this.isJavaScriptEnable = isJavaScriptEnable;
        webview_settings();
        try {
            loadURL();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void webview_settings() {

        WEB_VIEW.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        WEB_VIEW.getSettings().setLoadWithOverviewMode(true);
        WEB_VIEW.getSettings().setUseWideViewPort(true);
        WEB_VIEW.setHorizontalScrollBarEnabled(true);
        WEB_VIEW.setScrollbarFadingEnabled(false);

        if (isJavaScriptEnable)
            WEB_VIEW.getSettings().setJavaScriptEnabled(true); // enable only if necessory
    }

    private void loadURL() {

        if (DOCUMENT_LINK != null && !DOCUMENT_LINK.equals("")) {
            if (!DOCUMENT_LINK.contains("http://")) {
                DOCUMENT_LINK = "http://" + DOCUMENT_LINK;

            } else if (DOCUMENT_LINK.contains(".pdf")) { // for opening pdfs to webview
                String url = "http://docs.google.com/gview?embedded=true&url=" + DOCUMENT_LINK;
                Log.i(TAG, "Opening PDF: " + url);
                WEB_VIEW.loadUrl(url);
            } else {
                WEB_VIEW.loadUrl(DOCUMENT_LINK);
            }
        } else {
            Toast.makeText(CONTEXT, "Error in opening Link!!!", Toast.LENGTH_SHORT).show();
        }
    }

}
