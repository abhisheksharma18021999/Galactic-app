package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

public class Testpdf extends Activity {

    //create object of webView
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdflayout);

        myWebView = findViewById(R.id.myWebView);

        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                //if page loaded successfully then show print button
//                findViewById(R.id.fab).setVisibility(View.VISIBLE);
            }
        });

        //prepare your html content which will be show in webview
        String htmlDocument = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h1 style=\"color:blue;\">A Blue Heading</h1>\n" +
                "\n" +
                "<p style=\"color:red;\">A red paragraph.</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";

        //load your html to webview
        myWebView.loadData(htmlDocument, "text/HTML", "UTF-8");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void CreatePdf(View view){
        Context context=getApplicationContext();
        PrintManager printManager=(PrintManager)getApplicationContext().getSystemService(context.PRINT_SERVICE);
        PrintDocumentAdapter adapter=null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            adapter=myWebView.createPrintDocumentAdapter();
        }
        String JobName=getString(R.string.app_name) +"Document";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            PrintJob printJob=printManager.print(JobName,adapter,new PrintAttributes.Builder().build());
        }

    }







}
