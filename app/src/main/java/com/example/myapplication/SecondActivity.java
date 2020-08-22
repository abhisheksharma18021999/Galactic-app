package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SecondActivity extends Activity {

    Button button2 =null;
    Button buttonP=null;
    final public static int DETAIL_REQ = 1;
    TextView textView=null;
    Button voice = null;
    Button pdf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        button2 =(Button)findViewById(R.id.returnhome);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),MainActivity.class);
                startActivity(i);
            }
        });
        buttonP =(Button)findViewById(R.id.power);
        textView =(TextView) findViewById(R.id.gotpower);
        buttonP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),DetailActivity.class);
//                i.putExtra("key","value");
                startActivityForResult(i,DETAIL_REQ);
            }
        });


        pdf =(Button)findViewById(R.id.pdfconvertor);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),ImgToPdfConvertor.class);
                startActivity(i);
            }
        });



        voice =(Button)findViewById(R.id.voice);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), TextToVoiceActivity.class);
                startActivity(i);
            }
        });

        // HTTP intent reciever
        Intent http = getIntent();
        String action = http.getAction();
        if(action==null ||action.equals(Intent.ACTION_VIEW)){
            Uri data =http.getData();
            if(data!=null){
                textView.setText(data.toString());
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==DETAIL_REQ){
            String value =data.getExtras().getString("key").toString();
            textView.setText(value);

        }
    }
}
