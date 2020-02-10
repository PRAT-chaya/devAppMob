package com.example.tp4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AnnonceCreatorActivity extends AppCompatActivity {

    EditText title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_creator);
        Button btnEnvoi = (Button) findViewById(R.id.buttonEnvoi);
        title   = (EditText)findViewById(R.id.editTitle);
        btnEnvoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Toast myToast = Toast.makeText(getApplicationContext(), title.getText(), Toast.LENGTH_LONG);
                myToast.show();

            }
        });
    }

    protected void createAdd(){

    }



}
