package com.example.tp4;

import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class AbstractBaseActivity extends AppCompatActivity {
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
