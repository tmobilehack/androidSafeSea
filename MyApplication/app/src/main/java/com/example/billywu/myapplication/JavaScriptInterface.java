package com.example.billywu.myapplication;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by billywu on 3/26/17.
 */

public class JavaScriptInterface {
    String json;
    Context context;


    JavaScriptInterface(String json) {
        this.json = json;
    }


    @JavascriptInterface
    public String getOurString() {
        return json;
    }
}
