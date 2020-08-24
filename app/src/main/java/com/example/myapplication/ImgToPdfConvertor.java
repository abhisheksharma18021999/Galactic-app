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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ImgToPdfConvertor extends Activity {

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

//    public void imgtopdf(List<File> files){
//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String dirPath = path.getPath();
//        try {
//            for (File f : files)
//            {
//                String fileName=f.getName();
//                String outputFile = fileName.replace(".jpg", ".pdf");
//                outputFile = "zebra.pdf";
//                Document document = new Document();
//                PdfWriter.getInstance(document, new FileOutputStream(new File(dirPath, outputFile)));
//                document.open();
//                document.newPage();
//                Image image = Image.getInstance(new File(dirPath, fileName).getAbsolutePath());
//                image.setAbsolutePosition(0, 550);
//                image.setBorderWidth(0);
//                image.scaleToFit(595, 842);
//                document.add(image);
//                document.close();
//            }
//            System.out.println("Done");
//        }catch (Exception e){
//            Log.d("ERRRORRR",e+"");
//        }
//    }


    private void createPDFWithMultipleImage(List<File> images){
        Log.d("Entry","create pdf");
        File file = getOutputFile();
        if (file != null){
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PdfDocument pdfDocument = new PdfDocument();

                for (int i = 0; i < images.size(); i++){
                    Bitmap bitmap = BitmapFactory.decodeFile(images.get(i).getPath());
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
}