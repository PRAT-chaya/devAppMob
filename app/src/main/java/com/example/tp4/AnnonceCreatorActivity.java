package com.example.tp4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AnnonceCreatorActivity extends AppCompatActivity {

    EditText title;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_creator);
        Button btnEnvoi = (Button) findViewById(R.id.buttonEnvoi);
        Button btnAddPicture = (Button) findViewById(R.id.btnAddPicture);



        title   = (EditText)findViewById(R.id.editTitle);

        btnEnvoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Toast myToast = Toast.makeText(getApplicationContext(), title.getText(), Toast.LENGTH_LONG);
                myToast.show();

            }
        });

        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                dispatchTakePictureIntent();
            }
        });
    }

    protected void createAdd(){

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }



}
