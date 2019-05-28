package com.app2gether.tesztfeladat.singleton;


import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.app2gether.ProgressInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class FtpController {

    private SharedPreferenceController sharedPreferenceController;
    private FTPClient ftpClient = null;
    private UploadListener listener;
    private boolean result = false;
    private ConnectionListener connectionListener;


    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public interface UploadListener {
        void startUpload();

        void onProgressChanged();

        void onUploadFinished();

        void onError();

    }

    public interface ConnectionListener {
        void success();

        void unsuccess(String error);
    }


    public void setListener(UploadListener listener) {
        this.listener = listener;
    }


    public FtpController(SharedPreferenceController sharedPreferenceController) {
        this.sharedPreferenceController = sharedPreferenceController;
    }

    private static FtpController ourInstance;

    public static FtpController getInstance() {
        return ourInstance;
    }

    public static void init() {
        ourInstance = new FtpController(SharedPreferenceController.getInstance());
    }


    public void connect(String host, String username, String password, int port) {
        try {
            disconnect();
            new AsyncConnexion(host, username, password, port).execute();

        } catch (Exception e) {
            e.printStackTrace();
            if (connectionListener != null) {
                connectionListener.unsuccess(e.getLocalizedMessage());

            }
        }
    }

    private void disconnect()  {
        if(ftpClient!=null){
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class AsyncConnexion extends AsyncTask<Void, Void, Boolean> {

        private String host;
        private String username;
        private String password;
        private int port;

        AsyncConnexion(String host, String username, String password, int port) {
            this.host = host;
            this.password = password;
            this.port = port;
            this.username = username;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ftpClient = new FTPClient();
                ftpClient.connect(host, port);
                boolean status = ftpClient.login(username, password);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                return status;
            } catch (Exception e) {
                Log.i("testConnection", "Error: could not connect to host " + host);
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (connectionListener != null) {
                if (result) {
                    connectionListener.success();
                } else {
                    connectionListener.unsuccess("Connection Error.");
                }
            }
        }
    }

    public void upload(final String name, final InputStream stream) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               if(listener!=null){
                   listener.startUpload();
               }
                try {
                    BufferedInputStream buffIn = new BufferedInputStream(stream);
                    ftpClient.enterLocalPassiveMode();
                    ProgressInputStream progressInput = new ProgressInputStream(
                            buffIn);
                    if(listener!=null){
                        listener.onProgressChanged();
                    }
                    result = ftpClient.storeFile(name, progressInput);
                    if (listener != null) {
                        if(result) {
                            listener.onUploadFinished();
                        }else{
                            listener.onError();
                        }
                    }
                } catch (IOException e) {
                    if(listener!=null){
                        listener.onError();
                    }
                    e.printStackTrace();
                }
            }

        });

        /*switch(thread.getState()) {
            case NEW:
                //Play voicefile
               String s="start";
                break;
            case RUNNABLE:
                //Stop MediaPlayer
                s="stop";
                break;
        }*/


        thread.start();

    }

}
