package com.example.billywu.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class DataActivity extends AppCompatActivity {

    Intent receivedIntent;
    Double targetLatitude, targetLongitude;

    int duration = Toast.LENGTH_SHORT;
    Context context;
    WebView myWebView;
    final int ZOOM_LEVEL = 145;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);


        context = getApplicationContext();
        receivedIntent = getIntent();
        Bundle bundle = getIntent().getExtras();
        String jsonResult = bundle.getString("json_result");

        Log.d("DATA ON DATAACTIVITY", jsonResult);


        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.addJavascriptInterface(new JavaScriptInterface(jsonResult), "JSInterface");
        myWebView.loadUrl("file:///android_asset/index.html");

        myWebView.setInitialScale(ZOOM_LEVEL);
        myWebView.getSettings().setBuiltInZoomControls(true);
        setContentView(myWebView);


        targetLatitude = Double.parseDouble(bundle.getString("target_latitude"));
        targetLongitude = Double.parseDouble(bundle.getString("target_longitude"));
/*
        Toast toast = Toast.makeText(context, jsonResult, duration);
            toast.show();*/







        //myWebView.loadUrl("http://www.google.com");
    }

}
