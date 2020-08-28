//package com.example.myapplication;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//
//import com.itextpdf.text.Utilities;
//
//public class DialogBoxShare extends Activity {
//
//    Button button1 =null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pdf);
//
//        button1=(Button)findViewById(R.id.select_share);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getApplicationContext());
//                View mView = getLayoutInflater().inflate(R.layout.share_dialog, null);
//                mBuilder.setView(mView);
//                final AlertDialog dialog = mBuilder.create();
//                dialog.show();
//            }
//        });
//
//    }
//}