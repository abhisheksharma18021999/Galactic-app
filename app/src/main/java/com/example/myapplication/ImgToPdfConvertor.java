package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ImgToPdfConvertor extends Activity {

    private static ArrayList<String> fileList = new ArrayList<String>();
    Image image;
    Document document;

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 30,
            Font.BOLD, BaseColor.BLUE);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 20,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        storegePermission(this);

    }

    public static void storegePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, 0);
    }


    public void selectImage(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        startActivityForResult(intent, 1001);
    }

    // Get the result from this Overriden method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1001:
                    // Checking whether data is null or not
                    if (data != null) {
                        // Checking for selection multiple files or single.
                        if (data.getClipData() != null) {
                            // Getting the length of data and logging up the logs using index
                            ArrayList<File> imagesArrayList = new ArrayList<>();
                            for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                                // Getting the URIs of the selected files and logging them into logcat at debug level
                                Uri uri = data.getClipData().getItemAt(index).getUri();
                                Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));
                                File file = new File(getPath(uri));
                                imagesArrayList.add(file);
                            }
                            Log.d("LISTFILE: ", imagesArrayList.toString());
                            createPDFWithMultipleImage(imagesArrayList);
                        } else {
                            // Getting the URI of the selected file and logging into logcat at debug level
                            Uri uri = data.getData();
                            Log.d("fileUri: ", String.valueOf(uri));
                        }
                    }
                    break;
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }


    private void createPDFWithMultipleImage(List<File> images){
        Log.d("Entry","create pdf");
        File file = getOutputFile();
        if (file != null){
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PdfDocument pdfDocument = new PdfDocument();

                for (int i = 0; i < images.size(); i++){
                    Bitmap bitmap = BitmapFactory.decodeFile("");
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), (i + 1)).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.BLUE);
                    canvas.drawPaint(paint);
                    canvas.drawBitmap(bitmap, 0f, 0f, null);
                    pdfDocument.finishPage(page);
                    bitmap.recycle();
                }
                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getOutputFile(){
        Log.d("Entry","ROOT path");
        File root = null;
        try {
            root = new File(getDirectory());
        } catch (Exception e) {
            System.out.print("error    =    " +e);
        }

        boolean isFolderCreated = true;

        if (!root.exists()){
            Log.d("Entry","ROOT path create");
            isFolderCreated = root.mkdir();
        }

        if (isFolderCreated) {
            Log.d("Entry","ROOT path present");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "PDF_" + timeStamp;

            return new File(root, imageFileName + ".pdf");
        }
        else {
            Log.d("Entry","ROOT path not present");
            Toast.makeText(this, "Folder is not created", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    public static String getDirectory() throws Exception {
        File directory = new File(Environment.getExternalStorageDirectory()
                + File.separator
                + "almight");
        if (!directory.exists() && !directory.isDirectory())
            directory.mkdirs();
        return directory.getAbsolutePath();
    }

    public void webviewIntent(View v){
        Intent i = new Intent(v.getContext(),WebViewToPdf.class);
        startActivity(i);
    }


    public void createDynamicPdf(View v) throws Exception{
        System.out.println("called");
        File file = getOutputFile();
        System.out.println("file path"+file.getAbsolutePath());
        String FILE = file.getAbsolutePath();
        document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();
            addTitlePage(document);
            backgroundTask(document);
          //  addImage(document);
        } catch (Exception e) {
            System.out.print("ERRORR " + e);
        }
    }

    public void backgroundTask(final Document document){

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String imgPAth = "https://firebasestorage.googleapis.com/v0/b/localpay-14450.appspot.com/o/agentPhotographs%2F1594189219595.jpg?alt=media&token=903f066f-3491-4f28-b8a4-a4ef7dac91f1";
                     image = Image.getInstance(new URL(imgPAth));
                }catch (Exception e){
                    System.out.println("background task errorr "+ e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addImage(image,document);
                        addImage(image,document);
                        document.close();
                    }
                });
            }
        });
        thread.start();
    }



    private static void addImage(Image image,Document document){
        try {
            document.add(image);
        }catch (Exception e){
            Log.e("ERROR", "addImage: "+e);
        }
    }

    private static void addTitlePage(Document document)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Store  $Category Catalog", catFont));
        preface.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Product Name : ", subFont));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Product Image : ", subFont));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(
                "   ORDER NOW !!!!  ",
                redFont));
        document.add(preface);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}