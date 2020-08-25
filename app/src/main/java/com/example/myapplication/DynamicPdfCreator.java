package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

public class DynamicPdfCreator extends Activity {


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pdf);
//    }
//
//    public void pdfConvertor(View view){
//        storegePermission(this);
//        try {
//            createDynamicPdf();
//        } catch (Exception e) {
//            System.out.print("ERRORR  CREATE" + e);
//        }
//    }
//
//    public static void storegePermission(Activity activity) {
//        ActivityCompat.requestPermissions(activity, new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//        }, 0);
//    }
//
//    public void createDynamicPdf() throws Exception{
//        String FILE_NAME = getDirectory();
//        Document document = new Document();
//        try {
//            PdfWriter.getInstance(document, new FileOutputStream(new File(FILE_NAME)));
//            document.open();
//            Paragraph p = new Paragraph();
//            p.add("Text 1");
//            p.setAlignment(Element.ALIGN_CENTER);
//            document.add(p);
//            Paragraph p2 = new Paragraph();
//            p2.add("Text 2"); //no alignment
//            document.add(p2);
//            Font f = new Font();
//            f.setStyle(Font.BOLD);
//            f.setSize(8);
//            document.add(new Paragraph("This is my paragraph 3", f));
//            String imageUrl = "https://images4.alphacoders.com/925/925020.png";
//            Image image = Image.getInstance(new URL(imageUrl));
//            document.add(image);
//            document.close();
//            System.out.println("Done");
//        } catch (Exception e) {
//            System.out.print("ERRORR " + e);
//        }
//    }
//
//    public static String getDirectory() throws Exception {
//        System.out.print("Get Path");
//        File directory = new File(Environment.getExternalStorageDirectory()
//                + File.separator
//                + "almight");
//        if (!directory.exists() && !directory.isDirectory())
//            directory.mkdirs();
//        return directory.getAbsolutePath();
//    }
}
