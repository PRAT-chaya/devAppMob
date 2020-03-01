package com.example.devAppMob;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.devAppMob.model.ApiConf;
import com.example.devAppMob.model.Annonce;
import com.example.devAppMob.model.Profil;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnnonceCreatorActivity extends AnnonceEditorActivity {

    private String annonceId = "";
    private Annonce annonce = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_creator);

        initToolbar();
        initView();

        sharedPrefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePictureSourceDialog();
            }
        });

        btnEnvoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    if (isConnected(getApplicationContext())) {

                        apiCallPOST(v, ApiConf.METHOD.POST.save);

                    } else {
                        Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur de connexion", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Formulaire invalide", Snackbar.LENGTH_LONG).show();

                }
            }
        });
    }

    @Override
    protected boolean isValid() {

        String strTitre = title.getText().toString();
        String strPrix = price.getText().toString();
        String strDesc = description.getText().toString();
        String strVille = ville.getText().toString();
        String strCp = cp.getText().toString();
        boolean hasError = false;

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

        RequestBody body = null;
        boolean isLastRequest = false;

        if (method.equals(ApiConf.METHOD.POST.save)) {
            body = new FormBody.Builder()
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
        }

        if (body != null) {
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Échec du POST, réponse négative", Snackbar.LENGTH_LONG).show();
                            throw new IOException("Unexpected HTTP code" + response);
                        } else {
                            assert responseBody != null;
                            final String body = responseBody.string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parseFromResponse(body);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void apiCallPOSTaddImage(View v) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = null;
        File temp;

        try {
            temp = File.createTempFile("temp", ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] myByteArray = baos.toByteArray();

            try (FileOutputStream fos = new FileOutputStream(temp)) {
                fos.write(myByteArray);
            }

            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(ApiConf.PARAM.apikey, ApiConf.API_KEY)
                    .addFormDataPart(ApiConf.PARAM.method, ApiConf.METHOD.POST.addImage)
                    .addFormDataPart(ApiConf.PARAM.id, annonceId)
                    .addFormDataPart(ApiConf.PARAM.photo, targetUri.getPath(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), temp))
                    .build();

        } catch (IOException e) {
            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur: impossible de créer temp", Snackbar.LENGTH_LONG).show();
        }

        if (body != null) {
            Request request = new Request.Builder()
                    .url(ApiConf.API_URL)
                    .method("POST", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Échec du POST, réponse négative", Snackbar.LENGTH_LONG).show();
                            throw new IOException("Unexpected HTTP code" + response);
                        } else {
                            assert responseBody != null;
                            final String body = responseBody.string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parseFromResponseAndStartActivity(body);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void parseFromResponse(String body) {
        annonce = parseResponseAsAnnonce(body);
        if (annonce != null) {
            if (annonce.getId() != null)
            annonceId = annonce.getId();
            if (imageIsLoaded()) {
                apiCallPOSTaddImage(getCurrentFocus());
            }
        }
    }

    private void parseFromResponseAndStartActivity(String body) {
        annonce = parseResponseAsAnnonce(body);
        if (annonce != null) {
            startActivityFromAnnonce(annonce);
        }
    }
}
