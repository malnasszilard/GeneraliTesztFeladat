package com.app2gether.tesztfeladat.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app2gether.tesztfeladat.R;
import com.app2gether.tesztfeladat.singleton.FtpController;

import java.io.File;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadActivity extends AppCompatActivity implements FtpController.UploadListener {

    private static final String TAG = UploadActivity.class.getSimpleName();
    FtpController ftpController = FtpController.getInstance();
    Uri selectedFileUri = null;
    String displayName;
    @BindView(R.id.fileChooser)
    TextView fileChooser;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
        FtpController.getInstance().setListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick({R.id.upload, R.id.imageView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            // todo check permisson - write, read
            case R.id.upload:
                if (selectedFileUri != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    try {
                        InputStream stream = getContentResolver().openInputStream(selectedFileUri);
                        ftpController.upload(displayName, stream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            this);
                    alertDialog.setMessage("Kérlek válassz ki egy torrent fájlt feltöltés előtt");
                    alertDialog.setPositiveButton("Rendben",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    fileChooser.setText("Kérlek válassz ki egy fájlt");
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    //  intent.setType("image/*");
                                    intent.setType("*/*");
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                break;
            case R.id.imageView:

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //  intent.setType("image/*");
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a File to Upload"),
                            1);

                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "Please install a File Manager.",
                            Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedFileUri = data.getData();
                    String uriString = selectedFileUri.toString();
                    File file = new File(uriString);
                    displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getApplicationContext().getContentResolver().query(selectedFileUri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                Log.d(TAG, "file:" + displayName);
                                if (!displayName.endsWith(".torrent")) {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                            this);
                                    alertDialog.setTitle("Kérlek válassz torrent fájlt!");
                                    alertDialog.setMessage("Ezt a fájlt sajnos nem tudjuk feltölteni, akarsz újat választani?");
                                    alertDialog.setPositiveButton("Igen",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    fileChooser.setText("Kérlek válassz ki egy fájlt");
                                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                    //  intent.setType("image/*");
                                                    intent.setType("*/*");
                                                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                                                    try {
                                                        startActivityForResult(
                                                                Intent.createChooser(intent, "Kérlek válasz egy torrent fájlt"),
                                                                1);

                                                    } catch (ActivityNotFoundException ex) {
                                                        Toast.makeText(getApplicationContext(), "Please install a File Manager.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                    alertDialog.setNegativeButton("Nem",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    fileChooser.setText("Kérlek válassz ki egy fájlt");
                                                    dialog.cancel();
                                                }
                                            });
                                    alertDialog.show();
                                } else {
                                    fileChooser.setText(displayName);
                                }
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = file.getName();
                        fileChooser.setText(displayName);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void startUpload() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Upload started",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onProgressChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"In progress",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onUploadFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Upload finished",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Error occured",Toast.LENGTH_LONG).show();
            }
        });
    }


}
