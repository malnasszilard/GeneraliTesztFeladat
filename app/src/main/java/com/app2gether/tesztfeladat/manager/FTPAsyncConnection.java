package com.app2gether.tesztfeladat.manager;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FTPAsyncConnection extends AsyncTask<Void, Void, FTPClient> {

    public interface Listener {
        void onConnectionFinished(FTPClient client);

    }

    private String host;
    private String username;
    private String password;
    private int port;
    private Listener listener;

    FTPAsyncConnection(String host, String username, String password, int port, Listener listener) {
        this.host = host;
        this.password = password;
        this.port = port;
        this.username = username;
        this.listener = listener;
        execute();
    }


    @Override
    protected FTPClient doInBackground(Void... voids) {
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(host, port);
            boolean status = ftpClient.login(username, password);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            return ftpClient;

        } catch (Exception e) {
            Log.i("testConnection", "Error: could not connect to host " + host);
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(FTPClient ftpClient) {
        super.onPostExecute(ftpClient);
        listener.onConnectionFinished(ftpClient);
    }
}
