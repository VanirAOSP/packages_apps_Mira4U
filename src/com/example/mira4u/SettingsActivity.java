package com.example.mira4u;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

    private final String TAG = "SettingsActivity";

    /**
     * App exit on back key pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    @Deprecated
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        // always shows scrollbar
        getListView().setScrollbarFadingEnabled(false);

        ListPreference list = (ListPreference)findPreference("persist.sys.wfd.framerate"); 
        setSummaryOnAppBoot(list, "persist.sys.wfd.framerate", R.string.framerate_summary_1st, R.string.framerate_summary);

        list.setOnPreferenceChangeListener( new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue){
                if (newValue == null) {
                    return false; 
                }

                ListPreference listpref = (ListPreference) preference;
                listpref.setSummary(getString(R.string.framerate_summary, (String)newValue));

                SharedPreferences pref = getSharedPreferences("prefs", Context.MODE_WORLD_READABLE);
                Editor edit = pref.edit();
                edit.putString("persist.sys.wfd.framerate", (String)newValue);
                edit.commit();

                return true;
            }
        } );

        list = (ListPreference)findPreference("persist.sys.wfd.bitrate"); 
        setSummaryOnAppBoot(list, "persist.sys.wfd.bitrate", R.string.bitrate_summary_1st, R.string.bitrate_summary);

        list.setOnPreferenceChangeListener( new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue){
                if (newValue == null) {
                    return false; 
                }

                ListPreference listpref = (ListPreference) preference;
                listpref.setSummary(getString(R.string.bitrate_summary, (String)newValue));

                SharedPreferences pref = getSharedPreferences("prefs", Context.MODE_WORLD_READABLE);
                Editor edit = pref.edit();
                edit.putString("persist.sys.wfd.bitrate", (String)newValue);
                edit.commit();

                return true;
            }
        } );
    }

    private void setSummaryOnAppBoot(ListPreference list, String prefkey, int initialKey, int setKey) {
        SharedPreferences pref = getSharedPreferences("prefs", Context.MODE_WORLD_READABLE);
        String f = pref.getString(prefkey, "0");
        String s = f.equals("0") ? getString(initialKey) : getString(setKey, f);
        list.setSummary(s);
    }

    @Override
    @Deprecated
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();

        // button
        if (key.startsWith("button_")) {
            if (key.equals("button_wfd")) {
                gotoWfd();
            } else if (key.equals("button_sink")) {
                gotoP2pSink();
            } else if (key.equals("button_wfddev")) {
                gotoWfdDev();
            } else {
                String msg = "onPreferenceTreeClick() Unknown Key["+key+"]";
                Log.e(TAG, msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        // list
        if (key.equals("persist.sys.wfd.framerate")) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        } else if (key.equals("persist.sys.wfd.bitrate")) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        CheckBoxPreference cbp = (CheckBoxPreference)preference;
        boolean chk = cbp.isChecked();

        // for OTA-package
        SystemProperties.set(key, chk ? "1" : "0");
        Log.d("SettingsActivity", "onPreferenceTreeClick() key["+key+"]["+SystemProperties.get(key, "0")+"]");

        // for update.zip
        SharedPreferences pref = getSharedPreferences("prefs", Context.MODE_WORLD_READABLE);
        Editor edit = pref.edit();
        edit.putString(key, chk ? "1" : "0");
        edit.commit();

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    /**
     * invoke Wifi Display Settings Activity
     */
    private void gotoWfd() {
        //String pac = "com.android.settings.wfd";

        //Intent i = new Intent();
        //i.setClassName(pac, pac + ".WifiDisplaySettings");
        //startActivity(i);        startActivity(i);

        Intent i = new Intent();
        i.setAction("android.settings.WIFI_DISPLAY_SETTINGS");
        i.addCategory(Intent.CATEGORY_DEFAULT);
        gotoXXX(i);
    }

    /**
     * invoke p2p Sink Activity
     */
    private void gotoP2pSink() {
        String pac = "com.example.mira4u";

        Intent i = new Intent();
        i.setClassName(pac, pac + ".P2pSinkActivity");
        gotoXXX(i);
    }

    /**
     * invoke Wfd Activity
     */
    private void gotoWfdDev() {
        String pac = "com.example.mira4u";

        Intent i = new Intent();
        i.setClassName(pac, pac + ".WfdActivity");
        gotoXXX(i);
    }

    /**
     * invoke XXX Activity
     */
    private boolean gotoXXX(Intent i) {
        try {
            startActivity(i);
        } catch (Exception e) {
            String msg = "gotoXXX() " + e.getMessage();
            Log.e(TAG, msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
