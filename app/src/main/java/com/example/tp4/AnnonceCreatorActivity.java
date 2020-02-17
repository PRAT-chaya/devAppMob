package com.example.tp4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    EditText titre;
    EditText prix;
    EditText description;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_creator);
        Button btnEnvoi = findViewById(R.id.buttonEnvoi);
        titre = findViewById(R.id.editTitle);
        prix   = findViewById(R.id.editPrix);
        description   = findViewById(R.id.editDescription);
        btnEnvoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                create();

            }
        });
    }

    protected void create(){

        String strTitre = titre.getText().toString();
        String strPrix = prix.getText().toString();
        String strDesc = description.getText().toString();
        Boolean hasError = false;

        if(TextUtils.isEmpty(strTitre)) {
            titre.setError("Le titre ne peux être vide");
            hasError = true;
        }

        if(TextUtils.isEmpty(strPrix)) {
            prix.setError("Le prix ne peux être vide");
            hasError = true;
        }

        if(TextUtils.isEmpty(strDesc)) {
            description.setError("La description ne peux être vide");
            hasError = true;
        }

        if(!hasError){
            Annonce annonce = new Annonce();
            annonce.setTitre(strTitre);
            annonce.setPrix(Integer.parseInt(strPrix));
            annonce.setDescription(strDesc);
            post("https://ensweb.users.info.unicaen.fr/android-api/",annonce);
        }


    }



    private void post(String url, Annonce annonce){
        Moshi moshi = new Moshi.Builder().add(new ApiAnnonceAdapter()).build();
        JsonAdapter<Annonce> jsonAdapter = moshi.adapter(Annonce.class);
        String json = "{\"apikey\": 21600639,\"method\": save, \"titre\": Jeans}";
        //jsonAdapter.toJson(annonce);
        Log.i("TP4", json.toString());
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected HTTP code" + response);
                    }
                    final String body = responseBody.string();
                    Log.i("TP4", body);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseResponse(body);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void parseResponse(String response) {
        Log.i("TP4", response);
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
