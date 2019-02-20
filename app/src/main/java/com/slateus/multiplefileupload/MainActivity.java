package com.slateus.multiplefileupload;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.slateus.multiplefileupload.API.FileUploader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> files = new ArrayList<>();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pDialog=new ProgressDialog(this);
        findViewById(R.id.btnSelectFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            2);
                }else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && null != data){
            if(data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for(int i = 0; i < count; i++){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    getImageFilePath(imageUri);
                }
            }
            if(files.size()>0){
                uploadFiles();
            }
        }
    }

    public void getImageFilePath(Uri uri) {

        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];
        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            files.add(imagePath);
            cursor.close();
        }
    }

    public void uploadFiles(){
        File[] filesToUpload = new File[files.size()];
        for(int i=0; i< files.size(); i++){
            filesToUpload[i] = new File(files.get(i));
        }
        showProgress("Uploading media ...");
        FileUploader fileUploader = new FileUploader();
        fileUploader.uploadFiles("/", "file", filesToUpload, new FileUploader.FileUploaderCallback() {
            @Override
            public void onError() {
                hideProgress();
            }

            @Override
            public void onFinish(String[] responses) {
                hideProgress();
                for(int i=0; i< responses.length; i++){
                    String str = responses[i];
                    Log.e("RESPONSE "+i, responses[i]);
                }
            }

            @Override
            public void onProgressUpdate(int currentpercent, int totalpercent, int filenumber) {
                updateProgress(totalpercent,"Uploading file "+filenumber,"");
                Log.e("Progress Status", currentpercent+" "+totalpercent+" "+filenumber);
            }
        });
    }

    public void updateProgress(int val, String title, String msg){
        pDialog.setTitle(title);
        pDialog.setMessage(msg);
        pDialog.setProgress(val);
    }

    public void showProgress(String str){
        try{
            pDialog.setCancelable(false);
            pDialog.setTitle("Please wait");
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setMax(100); // Progress Dialog Max Value
            pDialog.setMessage(str);
            if (pDialog.isShowing())
                pDialog.dismiss();
            pDialog.show();
        }catch (Exception e){

        }
    }

    public void hideProgress(){
        try{
            if (pDialog.isShowing())
                pDialog.dismiss();
        }catch (Exception e){

        }

    }

}
