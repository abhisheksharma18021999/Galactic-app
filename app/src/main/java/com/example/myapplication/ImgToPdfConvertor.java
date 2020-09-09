package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.content.FileProvider;

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
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


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

    public void webviewIntent(View v){
        Intent i = new Intent(v.getContext(),WebViewToPdf.class);
        startActivity(i);
    }


    public void createDynamicPdf(View v) throws Exception{
        System.out.print("Entered  func()");
        try {
            File file = getOutputFile();
            System.out.println("file path"+file.getAbsolutePath());
            String FILE = file.getAbsolutePath();
            Uri fileUri =  Uri.fromFile(file);
            System.out.print("Doc  created()");
            document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();

        Map<String,List<PdfDetails>> map = getDetails();
        int i = 0;
        for (String key : map.keySet()) {

            // category title
            System.out.println("Key  == "+key);
            addTitlePage(document,key);

            List<PdfDetails> details = map.get(key);
            System.out.println("details == list");
            if(details==null){
                System.out.println("Null");
            }else{
                System.out.println("Not Null");
            }
            System.out.println("loop == size"+details.size());

            for (int j=0;j<details.size();j++)
            {
                System.out.println("loop1 == start");
                System.out.println("{ price } "+details.get(j).getProductPrice());
                System.out.println("{ url } "+details.get(j).getProductURl());
                System.out.println("{ name } "+details.get(j).getProductName());

                // add details
                addProductdetails(document,details.get(j).getProductName(),details.get(j).getProductPrice());
                // add image url in pdf
                imageAddToPdf(document,details.get(j).getProductURl());
                // add space
                Paragraph preface = new Paragraph();
                addEmptyLine(preface, 2);
                document.add(preface);
                System.out.println("loop2 == End");
            }
            document.newPage();
            i++;
        }
       document.close();
       // shareCategoryAsText(map);
            Log.e("QQWW",fileUri+"");
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            PackageManager pm=getPackageManager();
            try {
                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("*/*");
                PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                waIntent.setPackage("com.whatsapp");
                waIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                waIntent.putExtra(Intent.EXTRA_STREAM , fileUri);
                startActivity(Intent.createChooser(waIntent, "Share with"));
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp not Installed" + "  "+e, Toast.LENGTH_SHORT)
                        .show();
                Log.e("error",""+e);
            }
        } catch (Exception e) {
            System.out.println("ERRORR " + e);
        }
    }

    public void imageAddToPdf(final Document document, final String url){
        System.out.println("addimage == start");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
                try {
                    System.out.println("addimage URL == "+url);
                    image = Image.getInstance(new URL(url));
                }catch (Exception e){
                    System.out.println("background task errorr "+ e);
                }
                        addImage(image,document);
    }

    public void addTitlePage(Document document,String categoryName) throws DocumentException {
        System.out.println("title == start");
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(categoryName+" Catalog", catFont));
        preface.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(preface, 1);
        document.add(preface);
        System.out.println("title == end");
    }

    private void addProductdetails(Document document, String productName, String productPrice) throws DocumentException {
        System.out.println("details == start");
        Paragraph preface = new Paragraph();
        preface.add(new Paragraph("Product Name : "+productName, subFont));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Product price : "+productPrice, subFont));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Product Image : ", subFont));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("!!!! ORDER NOW !!!!  ", redFont));
        addEmptyLine(preface, 1);
        document.add(preface);
        System.out.println("details == End");
    }


    private  void addImage(Image image,Document document){
        try {
            System.out.println("addimage () Start == ");
            document.add(image);
            System.out.println("addimage () end == ");
        }catch (Exception e){
            Log.e("ERROR", "addImage: "+e);
        }
    }

//    private static void addTitlePage(Document document)
//            throws DocumentException {
//        Paragraph preface = new Paragraph();
//        addEmptyLine(preface, 1);
//        preface.add(new Paragraph("Store  $Category Catalog", catFont));
//        preface.setAlignment(Element.ALIGN_CENTER);
//        addEmptyLine(preface, 1);
//        preface.add(new Paragraph("Product Name : ", subFont));
//        addEmptyLine(preface, 1);
//        preface.add(new Paragraph("Product Image : ", subFont));
//        addEmptyLine(preface, 1);
//        preface.add(new Paragraph(
//                "   ORDER NOW !!!!  ",
//                redFont));
//        document.add(preface);
//    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    class PdfDetails{
        private  String productName ;
        private String productURl ;
        private String productPrice ;


        public  PdfDetails(String productName,String productURl,String productPrice){
            this.productName = productName;
            this.productURl = productURl;
            this.productPrice = productPrice;
        }

        public String getProductName() {
            return productName;
        }

        public String getProductURl() {
            return productURl;
        }

        public String getProductPrice() {
            return productPrice;
        }
    }

    public Map<String,List<PdfDetails>> getDetails(){
        int countOfCategories = 2; // fetch total selected categories

        // fetch Details
        Map<String,List<PdfDetails>> map = new HashMap<>();
        for(int i=0 ; i < countOfCategories ; i++) {
            String categoryName = "shoe";   // fetch category name
            if(i==1){
                categoryName = "footwear";
            }
            int productCount = 2;       // fetch count of products in that category
            List<PdfDetails> details = new ArrayList<>();
            for(int j=0 ; j < productCount ; j++){
                details.add(new PdfDetails("adiddas","https://www.tutorialspoint.com/images/tp-logo-diamond.png","1000"));
            }
            map.put(categoryName, details);
        }
        return map;
    }

    public void   shareCategoryAsText( Map<String,List<PdfDetails>> map){
        String sms = "\n"+"List of categories available at our store".toUpperCase()+"\n";
        String msgC = "";
        String msgP = "";
        int i = 0;
        for (String key : map.keySet()) {
            msgC =  msgC ;
            msgC = "Category :- " + msgC + key.toUpperCase() + "\n";
            List<PdfDetails> details = map.get(key);
            for (int j=0;j<details.size();j++)
            {
                msgP = "Product :-" + msgP + details.get(j).getProductName() + " @ " +" Rs."+ details.get(j).getProductPrice() + "\n";
                msgP = msgP + "click the link to view product "+"\n";
                msgP = msgP + details.get(j).getProductURl() +"\n";
                msgC = msgC + "\n" + msgP + "\n";
                msgP = "";
            }
            i++;
            sms = sms +"\n"+ msgC;
            msgC = "";
        }
        sms = sms + "\n";
        sms = sms + "Order Now !!";
        System.out.println(" == TEXT ==>"+sms);


        PackageManager pm=getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, sms);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }

    }

}