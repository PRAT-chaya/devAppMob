package com.example.tp4;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

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

public class AnnonceCreatorActivity extends AbstractApiConnectedActivity {

    protected EditText title, price, description, ville, cp;
    protected Button btnEnvoi;
    protected MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_creator);

        initToolbar();
        initView();

        sharedPrefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        btnEnvoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getApplicationContext()) && isValid()) {
                    apiCallPOST(v, ApiConf.METHOD.POST.save);
                } else {
                    Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur de connexion", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    protected Boolean isValid() {

        String strTitre = title.getText().toString();
        String strPrix = price.getText().toString();
        String strDesc = description.getText().toString();
        String strVille = ville.getText().toString();
        String strCp = cp.getText().toString();
        Boolean hasError = false;

        if (TextUtils.isEmpty(strTitre)) {
            title.setError("Le titre ne peux être vide");
            hasError = true;
        }

        if (TextUtils.isEmpty(strPrix)) {
            price.setError("Le prix ne peux être vide");
            hasError = true;
        }

        if (TextUtils.isEmpty(strDesc)) {
            description.setError("La description ne peux être vide");
            hasError = true;
        }

        if (TextUtils.isEmpty(strVille)) {
            description.setError("Le nom de ville ne peux être vide");
            hasError = true;
        }

        if (TextUtils.isEmpty(strCp)) {
            description.setError("Le Code Postal ne peux être vide");
            hasError = true;
        }

        return !hasError;


    }

    @Override
    protected void makeApiCall(String url, String method) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add(ApiConf.PARAM.apikey, ApiConf.API_KEY)
                .add(ApiConf.PARAM.method, method)
                .add(ApiConf.PARAM.titre, title.getText().toString())
                .add(ApiConf.PARAM.description, description.getText().toString())
                .add(ApiConf.PARAM.prix, price.getText().toString())
                .add(ApiConf.PARAM.pseudo, sharedPrefs.getString(Profil.username, ""))
                .add(ApiConf.PARAM.emailContact, sharedPrefs.getString(Profil.emailAddress, ""))
                .add(ApiConf.PARAM.telContact, sharedPrefs.getString(Profil.phoneNumber, ""))
                .add(ApiConf.PARAM.ville, ville.getText().toString())
                .add(ApiConf.PARAM.cp, cp.getText().toString())
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

    @Override
    protected void initView() {
        btnEnvoi = (Button) findViewById(R.id.buttonEnvoi);
        title = (EditText) findViewById(R.id.editTitle);
        price = (EditText) findViewById(R.id.editPrix);
        description = (EditText) findViewById(R.id.editDescription);
        ville = (EditText) findViewById(R.id.editVille);
        cp = (EditText) findViewById(R.id.editCP);
    }
}
