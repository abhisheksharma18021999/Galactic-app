package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import java.util.List;

public class DetailActivity extends Activity {

    Button button=null;
    Button actionBtn=null;
    Spinner spinner=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //

//        Bundle Stuff = getIntent().getExtras();
//        if(Stuff!=null){
//            String getValueStuff = Stuff.getString("key");
//            if(getValueStuff!=null){
//                Toast.makeText(this,getValueStuff,Toast.LENGTH_SHORT).show();
//            }
//        }

        button=(Button)findViewById(R.id.returnToSecondActivity);
        actionBtn=(Button)findViewById(R.id.performImplicit);
        spinner=(Spinner)findViewById(R.id.spinnerSelection);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent =new Intent();
                String selectedItem = spinner.getSelectedItem().toString();
                returnIntent.putExtra("key",selectedItem);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position =spinner.getSelectedItemPosition();
                Intent actionIntend = null;
                switch (position){
                    case 0:

                        final String myPackage = getPackageName();
                            actionIntend = new   Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+myPackage));
                        break;

                    case 1:
                        // visit web
                        actionIntend = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/"));
                        break;

                    case 2:
                        //
                        actionIntend = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:(+91)9569636530"));
                        break;

                    case 3:
                        //
                        actionIntend = new Intent(Intent.ACTION_VIEW,Uri.parse("geo:23.805450, 19.391070"));
                        break;

                    case 4:
                        actionIntend =new Intent("android.media.action.IMAGE_CAPTURE");
                        break;

                    case 5:
                        actionIntend = new Intent(Intent.ACTION_EDIT,Uri.parse("content://contacts//people/1"));
                        break;
                }

                if(actionIntend!=null){
                    if(IsIntendAvailable(actionIntend)) {
                        startActivity(actionIntend);
                    }else {

                    }
                }
            }
        });
    }
    public boolean IsIntendAvailable(Intent intent){
        PackageManager packageManager =getPackageManager();
        List<ResolveInfo> list =packageManager.queryIntentActivities(intent,0);
        boolean isSafe = list.size()>0;
        return isSafe;

    }
}
