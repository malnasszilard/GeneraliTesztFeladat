package com.app2gether.tesztfeladat.singleton;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceController {

    private static SharedPreferenceController instace;

    private SharedPreferences sharedPreferences;

    private SharedPreferenceController(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instace == null){
            instace = new SharedPreferenceController(context);
        }
    }
    public static SharedPreferenceController getInstance() {
        return instace;
    }

    public String read(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }


    public Integer read(String key, Integer defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }


    public void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }


    public void remove(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove(key);
        prefsEditor.commit();
    }

    public void remove(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove(key);
        prefsEditor.commit();
    }

}
