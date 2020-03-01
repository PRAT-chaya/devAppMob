package com.example.devAppMob;

import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class AbstractBaseActivity extends AppCompatActivity {

    static final class BundleKeys {
        static String DELETED_ANNONCE = "DELETED_ANNONCE";
        static String FILTER_USERNAME = "FILTER_USERNAME";
        static String IS_LOCAL = "IS_LOCAL";
    }

    static final class BundleVals {
        static int DELETED_ANNONCE = 1;
        static int IS_LOCAL = 1;
    }

    protected SharedPreferences sharedPrefs;

    protected void initToolbar() {
        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    protected abstract void initView();

}
