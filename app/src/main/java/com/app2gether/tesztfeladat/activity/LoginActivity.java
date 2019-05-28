package com.app2gether.tesztfeladat.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app2gether.tesztfeladat.R;
import com.app2gether.tesztfeladat.singleton.FtpController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements FtpController.ConnectionListener {

    @BindView(R.id.username)
    TextInputLayout username;
    @BindView(R.id.password)
    TextInputLayout password;
    @BindView(R.id.host)
    TextInputLayout host;
    @BindView(R.id.port)
    TextInputLayout port;
    Uri selectedFileUri = null;
    String displayName;
    boolean connected = false;
    private static final String TAG = LoginActivity.class.getSimpleName();
    FtpController ftpController = FtpController.getInstance();
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.login)
    Button login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        FtpController.getInstance().setConnectionListener(this);
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        };
        int PERMISSION_ALL = 1;
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        username.getEditText().setText("dlpuser@dlptest.com");
        password.getEditText().setText("5p2tvn92R0di8FdiLCfzeeT0b");
        port.getEditText().setText("21");
        host.getEditText().setText("ftp.dlptest.com");   // ftp.dlptest.com
    }

    @OnClick(R.id.login)
    public void onViewClicked() {
        progressBar.setVisibility(View.VISIBLE);
        String userName = String.valueOf(username.getEditText().getText());
        String actPassword = String.valueOf(password.getEditText().getText());
        String actport = String.valueOf(port.getEditText().getText());
        String acthost = String.valueOf(host.getEditText().getText());
        username.setClickable(false);
        password.setClickable(false);
        port.setClickable(false);
        host.setClickable(false);
        login.setClickable(false);
        ftpController.connect(acthost, userName, actPassword, Integer.valueOf(actport));

    }


    @Override
    public void success() {
        Intent myIntent = new Intent(LoginActivity.this, UploadActivity.class);
        startActivity(myIntent);
        progressBar.setVisibility(View.GONE);
        finish();
    }

    @Override
    public void unsuccess(String error) {
        Toast.makeText(getApplicationContext(), "A megadott adatok helytelenek kérem próbálja újra!", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        username.setClickable(true);
        password.setClickable(true);
        port.setClickable(true);
        host.setClickable(true);
        login.setClickable(true);
    }

}
