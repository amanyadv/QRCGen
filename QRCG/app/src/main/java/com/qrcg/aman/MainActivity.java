package com.qrcg.aman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private static final int FILE_SELECT_CODE = 101;

    private EditText folderNameTxt;
    private Button selectBt, openFileBt, shareFileBt;

    private String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        folderNameTxt = (EditText) findViewById(R.id.folder_name_et);
        selectBt = (Button) findViewById(R.id.file_select_bt);
        openFileBt = (Button) findViewById(R.id.open_file_bt);
        shareFileBt = (Button) findViewById(R.id.share_file_bt);

        selectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result  = RuntimePermissions.checkPermission(MainActivity.this);
                if (result){
                    showFileChooser();
                }
            }
        });

        openFileBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolder(folderName);
            }
        });

        shareFileBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        showOpenAndShareBt();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResult");
        switch (requestCode) {
            case FILE_SELECT_CODE:
                Log.d(LOG_TAG, "onActivityResult - "+requestCode);
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Log.d(LOG_TAG, "onActivityResult - "+Activity.RESULT_OK);
                    generateQrCode(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RuntimePermissions.REQUEST_EXTERNAL_STORAGE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                }
                break;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain"); //only text files   //intent.setType("*/*");   //all files
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select txt file"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this , "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void generateQrCode(Intent data) {
        if (folderNameTxt == null || folderNameTxt.getText().length() <= 0) {
            Toast.makeText(this, "Please write folder name", Toast.LENGTH_LONG).show();
            return;
        }
        Uri uri = data.getData();
        Log.d(LOG_TAG, "onActivityResult uri - "+uri);
        folderName = folderNameTxt.getText().toString();
        File folder = FileCreator.getOrCreateFolder(folderName);
        if (uri != null && uri.toString().length() > 0 && folder != null) {
           new GenerateQrCodeAsync(MainActivity.this, uri, folder.getAbsolutePath()).execute();
        }
    }

    private void generateQrCode(Uri uri, String folderPath){
        FileInputStream is;
        BufferedReader reader;
        if (uri != null && uri.toString().length() > 0){
            try {
                //is = new FileInputStream(file);
                InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream != null){
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    do {
                        Log.d(LOG_TAG, "line - "+line);
                        line = reader.readLine();
                        Log.d(LOG_TAG, "line 1 -"+line);
                        if(line != null && line.length() > 1){
                            String imageFileName = folderPath+"/"+line+".png";
                            Log.d(LOG_TAG, "imageFileName  -"+imageFileName);
                            QRGenerator.generateQrCode(line, imageFileName, 300);
                            FileCreator.scan_media(getApplicationContext(), imageFileName);
                        }

                    }while (line != null);
                    reader.close();
                    inputStream.close();
                    if (folderPath.length() > 0) {
                        AppUtil.createAlertDialog(MainActivity.this, "", "Your file is ready you can check from file manager.",  "ok", false, null);
                    }
                }
            }catch (Exception e){
                Log.e(LOG_TAG, "ERROR QR CODE -", e);
            }
        }
    }

    private class GenerateQrCodeAsync extends AsyncTask<Void,Void,Boolean> {
        private FileInputStream is;
        private BufferedReader reader;
        private Uri uri;
        private String folderName;
        private ProgressDialog progressDialog;

        public GenerateQrCodeAsync(Context context, Uri uri, String folderName){
            this.uri = uri;
            this.folderName = folderName;
            progressDialog =   AppUtil.createProgressDialog(context, "", "Generating qr codes .. ", false);
        }

        @Override
        protected void onPreExecute() {
            AppUtil.showProgressDialog(progressDialog);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (uri != null && uri.toString().length() > 0){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    if (inputStream != null){
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = "";
                        do {
                            Log.d(LOG_TAG, "line - "+line);
                            line = reader.readLine();
                            Log.d(LOG_TAG, "line 1 -"+line);
                            if(line != null && line.length() > 1){
                                String imageFileName = folderName+"/"+line+".png";
                                Log.d(LOG_TAG, "imageFileName  -"+imageFileName);
                                QRGenerator.generateQrCode(line, imageFileName, 300);
                                FileCreator.scan_media(getApplicationContext(), folderName);
                            }
                        }while (line != null);
                        reader.close();
                        inputStream.close();
                        return true;
                    }
                }catch (Exception e){
                    Log.e(LOG_TAG, "ERROR QR CODE -", e);
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            AppUtil.dismissProgressDialog(progressDialog);
            if (aBoolean){
                AppUtil.createAlertDialog(MainActivity.this, "", "Your QR codes file is ready, You can check by click on open file button.",  "ok", false, null);
                showOpenAndShareBt();
            }else {
                AppUtil.createAlertDialog(MainActivity.this, "", "Error creating qr code, Please try again.",  "ok", false, null);
            }
        }
    }

    private void openFolder(String folderName) {
        Uri uri =  Uri.parse(FileCreator.getOrCreateFolder(folderName) + File.separator) ;;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(uri, "image/png");
        try {
            startActivity(Intent.createChooser(intent, "Open folder"));
        }catch (ActivityNotFoundException e) {
            Log.e(LOG_TAG, "Error opening folder", e);
        }
    }

    private void showOpenAndShareBt() {
        if (folderName != null && folderName.length() > 0) {
            File appFolder = FileCreator.getOrCreateFolder(folderName);
            if (appFolder != null && appFolder.listFiles() != null && appFolder.listFiles().length > 0) {
                openFileBt.setVisibility(View.VISIBLE);
                //shareFileBt.setVisibility(View.VISIBLE);
            } else {
                openFileBt.setVisibility(View.GONE);
                //shareFileBt.setVisibility(View.GONE);
            }
        }
    }

}
