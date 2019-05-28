package com.app2gether.tesztfeladat.singleton;


import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FtpController {

    private FTPClient ftpClient = null;
    private UploadListener listener;
    private boolean result = false;
    private ConnectionListener connectionListener;
    private int percent = 0;
    private long fileLength;


    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public interface UploadListener {
        void startUpload();

        void onProgressChanged(int lenght);

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


    private FtpController() {
    }

    private static FtpController ourInstance;

    public static FtpController getInstance() {
        return ourInstance;
    }

    public static void init() {
        if (ourInstance == null) {
            ourInstance = new FtpController();
        }
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

    private void disconnect() {
        if (ftpClient != null) {
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

    public void upload(final String name, final InputStream stream, long fileLength) {
        setUploadStateListener();
        percent=0;
        this.fileLength=fileLength;
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.startUpload();
                }
                try {
                    BufferedInputStream buffIn = new BufferedInputStream(stream);
                    ftpClient.enterLocalPassiveMode();
                    result = ftpClient.storeFile(name, buffIn);

                    if (listener != null) {
                            listener.onProgressChanged(percent);
                    }
                    if (listener != null) {
                        if (result) {
                            listener.onUploadFinished();
                        } else {
                            listener.onError();
                        }
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError();
                    }
                    e.printStackTrace();
                }
            }

        });
        thread.start();

    }

    private void setUploadStateListener() {
        if (ftpClient != null) {
            ftpClient.setCopyStreamListener(new CopyStreamListener() {
                @Override
                public void bytesTransferred(CopyStreamEvent event) {

                }

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                    if (listener != null){
                        long lenght = (fileLength > 1) ? fileLength : streamSize;
                        int percentFloat = (int) ((float) totalBytesTransferred / (float) lenght  * 100);
                        if (percent < percentFloat){
                            listener.onProgressChanged(percent);
                            percent = percentFloat;
                            Log.d("Inputsteam", "totalBytesTransferred: " + totalBytesTransferred);

                        }
                    }

                }
            });
        }
    }

}
