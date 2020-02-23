package com.example.tp4;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

public class ProfilViewActivity extends AbstractBaseActivity {
    private EditText usernameView, phonenumberView, mailAddressView;
    private Button savedPrefsButton;
    private SharedPreferences sharedPrefs;
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_view);

        initToolbar();

        sharedPrefs  = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        initView();
        fillView();


    }

    private void initView(){
        usernameView = findViewById(R.id.usernameView);
        phonenumberView = findViewById(R.id.phonenumberView);
        mailAddressView = findViewById(R.id.mailAddressView);
        savedPrefsButton = findViewById(R.id.savePrefsButton);
        savedPrefsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedPrefs.edit();
                editor.putString(Profil.username, usernameView.getText().toString());
                editor.putString(Profil.phoneNumber, phonenumberView.getText().toString());
                editor.putString(Profil.emailAddress, mailAddressView.getText().toString());
                editor.commit();
                Snackbar.make(findViewById(R.id.profil_view), R.string.prefs_saved, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void fillView(){
        String username = sharedPrefs.getString(Profil.username, "");
        usernameView.setText(username);
        String phoneNumber = sharedPrefs.getString(Profil.phoneNumber, "");
        phonenumberView.setText(phoneNumber);
        String mailAddress = sharedPrefs.getString(Profil.emailAddress, "");
        mailAddressView.setText(mailAddress);
    }
}
