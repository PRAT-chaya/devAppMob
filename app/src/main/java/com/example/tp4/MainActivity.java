package com.example.tp4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private Button showAdButton;
    private Button addAdButton;
    private Button adsListButton;
    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtons();
        setButtonListeners();
    }

    private void initButtons() {
        showAdButton = findViewById(R.id.showAdButton);
        addAdButton = findViewById(R.id.addAdButton);
        adsListButton = findViewById(R.id.adsListButton);
        profileButton = findViewById(R.id.profileButton);
    }

    private void setButtonListeners(){
        showAdButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, AnnonceViewActivity.class);
                startActivity(intent);
            }
        });

        adsListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnnonceListViewActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfilViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
