package com.example.tp4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnnonceCreatorActivity extends AppCompatActivity {

    protected EditText title, price, description, ville, cp;
    protected MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_creator);

        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        Button btnEnvoi = (Button) findViewById(R.id.buttonEnvoi);
        title = (EditText) findViewById(R.id.editTitle);
        price = (EditText) findViewById(R.id.editPrix);
        description = (EditText) findViewById(R.id.editDescription);
        ville = (EditText) findViewById(R.id.editVille);
        cp = (EditText) findViewById(R.id.editCP);
        sharedPrefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        btnEnvoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getApplicationContext()) && isValid()) {
                    apiCall(v);
                } else {
                    Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur de connexion", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    protected Boolean isValid(){

        String strTitre = title.getText().toString();
        String strPrix = price.getText().toString();
        String strDesc = description.getText().toString();
        String strVille = ville.getText().toString();
        String strCp = cp.getText().toString();
        Boolean hasError = false;

        if(TextUtils.isEmpty(strTitre)) {
            title.setError("Le titre ne peux être vide");
            hasError = true;
        }

        if(TextUtils.isEmpty(strPrix)) {
            price.setError("Le prix ne peux être vide");
            hasError = true;
        }

        if(TextUtils.isEmpty(strDesc)) {
            description.setError("La description ne peux être vide");
            hasError = true;
        }

        if(TextUtils.isEmpty(strVille)) {
            description.setError("Le nom de ville ne peux être vide");
            hasError = true;
        }

        if(TextUtils.isEmpty(strCp)) {
            description.setError("Le Code Postal ne peux être vide");
            hasError = true;
        }

        return !hasError;



    }

    protected boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Connecté au Wifi", Snackbar.LENGTH_LONG).show();
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Connecté au data", Snackbar.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

    protected void apiCall(View view) {
        makeApiCall("https://ensweb.users.info.unicaen.fr/android-api/");
    }

    private void makeApiCall(String url) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("apikey", "21907858")
                .add("method", "save")
                .add("titre", title.getText().toString())
                .add("description", description.getText().toString())
                .add("prix", price.getText().toString())
                .add("pseudo", sharedPrefs.getString(Profil.username, ""))
                .add("emailContact", sharedPrefs.getString(Profil.emailAddress, ""))
                .add("telContact", sharedPrefs.getString(Profil.phoneNumber, ""))
                .add("ville", ville.getText().toString())
                .add("cp", cp.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Échec du POST, réponse négative", Snackbar.LENGTH_LONG).show();
                        throw new IOException("Unexpected HTTP code" + response);
                    } else {
                        final String body = responseBody.string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseResponse(body);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void parseResponse(String response) {
        // créer Moshi et lui ajouter l'adapteur ApiPersonneAdapter
        Moshi moshi = new Moshi.Builder().add(new ApiAnnonceAdapter()).build();
        // créer l'adapteur pour Annonce
        JsonAdapter<Annonce> jsonAdapter = moshi.adapter(Annonce.class);

        try {
            // response est la String qui contient le JSON de la réponse
            Annonce annonce = jsonAdapter.fromJson(response);
            Intent intent = new Intent(this, AnnonceViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("HELLO", annonce);
            intent.putExtras(bundle);
            startActivity(intent);
        } catch (IOException e) {
            Log.i("TP4", "Erreur I/O");
        }
    }
}
