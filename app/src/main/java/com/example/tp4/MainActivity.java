package com.example.tp4;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button showAdButton;
    private Button addAdButton;
    private Button adsListButton;
    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);
        initButtons();
        setButtonListeners();
    }

    private void initButtons() {
        showAdButton = findViewById(R.id.showAdButton);
        addAdButton = findViewById(R.id.addAdButton);
        adsListButton = findViewById(R.id.adsListButton);
        profileButton = findViewById(R.id.profileButton);
    }

    private void setButtonListeners() {
        showAdButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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

        addAdButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnnonceCreatorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_all_annonces:
                Intent intent = new Intent(MainActivity.this, AnnonceListViewActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_my_annonces:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_add_pic:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_add_annonce:
                intent = new Intent(MainActivity.this, AnnonceCreatorActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_profil:
                intent = new Intent(MainActivity.this, ProfilViewActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
