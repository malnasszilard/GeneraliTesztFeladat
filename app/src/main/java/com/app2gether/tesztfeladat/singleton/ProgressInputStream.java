package com.app2gether.tesztfeladat.singleton;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends InputStream {

    private ProgressListener progressListener;


    public interface ProgressListener {
        void onProgressChanged(long progress);
    }


    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }


    private static final int ChangeRate = 1024;//TEN_KILOBYTES

    private InputStream inputStream;
    private long progress;
    private long lastUpdate;
    private boolean closed;

    public ProgressInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        this.progress = 0;
        this.lastUpdate = 0;

        this.closed = false;
    }

    @Override
    public int read() throws IOException {
        int count = inputStream.read();
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = inputStream.read(b, off, len);
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (closed)
            throw new IOException("already closed");
        closed = true;
    }

    private int incrementCounterAndUpdateDisplay(int count) {
        if (count < 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
        return count;
    }

    private long maybeUpdateDisplay(long progress, long lastUpdate) {
        if (progress - lastUpdate < ChangeRate) {
            lastUpdate = progress;
            sendLong(progress);
        }
        return lastUpdate;
    }

    private void sendLong(long value) {
        Log.d("Inputsteam", "value: " + value);
        if (progressListener != null) {
            progressListener.onProgressChanged(value);
        }
    }
}
