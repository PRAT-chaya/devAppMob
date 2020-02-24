package com.example.tp4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tp4.model.Profil;
import com.example.tp4.model.db.AnnonceDbHelper;
import com.example.tp4.model.db.AnnonceDbManager;
import com.google.android.material.snackbar.Snackbar;

public class ProfilViewActivity extends AbstractBaseActivity {
    private EditText usernameView, phonenumberView, mailAddressView;
    private Button savedPrefsButton;
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_view);

        initToolbar();
        initView();

        sharedPrefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        fillView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_all_annonces:
                Intent intent = new Intent(ProfilViewActivity.this, AnnonceListViewActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_my_annonces:
                intent = new Intent(this, AnnonceListViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("PSEUDO_TO_FILTER", sharedPrefs.getString(Profil.username, ""));
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            case R.id.action_add_annonce:
                intent = new Intent(ProfilViewActivity.this, AnnonceCreatorActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_profil:
                intent = new Intent(ProfilViewActivity.this, ProfilViewActivity.class);
                startActivity(intent);
                return true;


            case R.id.action_show_my_local_annonces:
                intent = new Intent(ProfilViewActivity.this, LocalStorageAnnonceListViewActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void initView() {
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

    private void fillView() {
        String username = sharedPrefs.getString(Profil.username, "");
        usernameView.setText(username);
        String phoneNumber = sharedPrefs.getString(Profil.phoneNumber, "");
        phonenumberView.setText(phoneNumber);
        String mailAddress = sharedPrefs.getString(Profil.emailAddress, "");
        mailAddressView.setText(mailAddress);
    }
}
