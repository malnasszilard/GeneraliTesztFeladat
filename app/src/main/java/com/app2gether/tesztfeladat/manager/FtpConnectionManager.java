package com.app2gether.tesztfeladat.manager;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpConnectionManager {


    public interface Listener {
        void onConnectionFinished(boolean success);
        void onUploadStateChanged(int status);
    }


    private static FtpConnectionManager instance = new FtpConnectionManager();
    public static FtpConnectionManager getInstance() {
        return instance;
    }

    private FTPClient ftpClient = null;
    private Listener listener;

    public void connect(String host, String username, String password, int port) {
         new FTPAsyncConnection(host, username, password, port, new FTPAsyncConnection.Listener() {
             @Override
             public void onConnectionFinished(FTPClient client) {
                 ftpClient = client;
             //    listener.onConnectionFinished(isConnected());
             }
         }).execute();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public boolean isConnected(){
        return ftpClient != null;
    }

    public void upload(String file) {

        try {
            File newFile = new File(file);
            FileInputStream ifile = new FileInputStream(newFile);
            ftpClient.storeFile("filetotranfer", ifile);
          //  ftpClient.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
