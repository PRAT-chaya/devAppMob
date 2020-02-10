package com.example.tp4;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.Console;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class AnnonceViewActivity extends AppCompatActivity {
    private TextView adTitleTextView, priceTextView, locationTextView, descTextView,
            dateTextView, contactTextView, emailTextView, phoneTextView;
    private ImageView imageView;
    private Annonce fedAnnonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annonce_view);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null) {
            fedAnnonce = null;
        } else {
            fedAnnonce = (Annonce) bundle.getSerializable("HELLO");
        }

        initViews();
        if (fedAnnonce != null){
            fillView(fedAnnonce);
        }
        else if (isConnected(this)) {
            apiCall(getCurrentFocus());
        } else {
            MockAnnonce mock = new MockAnnonce();
            fillView(mock);
        }
      
        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                sendPhoneCall();
            }
        });

        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.putExtra(Intent.EXTRA_EMAIL, contactTextView.getText());
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact : " + adTitleTextView.getText());

        emailIntent.setType("message/rfc822");

        startActivity(Intent.createChooser(emailIntent, "Envoi de l'email..."));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendPhoneCall() {

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneTextView.getText()));
        startActivity(intent);
    }

    protected boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Snackbar.make(findViewById(R.id.main), "Connecté au Wifi", Snackbar.LENGTH_LONG).show();
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Snackbar.make(findViewById(R.id.main), "Connecté au data", Snackbar.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

    protected void apiCall(View view) {
        makeApiCall("https://ensweb.users.info.unicaen.fr/android-api/mock-api/completeAdWithImages.json");
        //makeApiCall("https://ensweb.users.info.unicaen.fr/android-api/mock-api/completeAd.json");
        //makeApiCall("https://ensweb.users.info.unicaen.fr/android-api/mock-api/erreur.json");
    }

    private void makeApiCall(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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
        //Snackbar.make(findViewById(R.id.main), "On parse la réponse", Snackbar.LENGTH_LONG).show();

        // créer Moshi et lui ajouter l'adapteur ApiPersonneAdapter
        Moshi moshi = new Moshi.Builder().add(new ApiAnnonceAdapter()).build();
        // créer l'adapteur pour Annonce
        JsonAdapter<Annonce> jsonAdapter = moshi.adapter(Annonce.class);

        try {
            // response est la String qui contient le JSON de la réponse
            Annonce annonce = jsonAdapter.fromJson(response);
            Log.i("TP4", "Désérialisation de la réponse");
            Log.i("TP4", annonce.toString());
            fillView(annonce);
        } catch (IOException e) {
            Log.i("TP4", "Erreur I/O");
        }
    }

    private void initViews() {
        adTitleTextView = findViewById(R.id.adTitleTextView);
        priceTextView = findViewById(R.id.priceTextView);
        locationTextView = findViewById(R.id.locationTextView);
        descTextView = findViewById(R.id.descTextView);
        dateTextView = findViewById(R.id.dateTextView);
        contactTextView = findViewById(R.id.contactTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        imageView = findViewById(R.id.imageView);
    }

    private void fillView(Annonce annonce) {
        adTitleTextView.setText(annonce.getTitre());
        priceTextView.setText(String.valueOf(annonce.getPrix() + "€"));
        locationTextView.setText(annonce.getCp() + " " + annonce.getVille());
        descTextView.setText(annonce.getDescription());
        Date date = new Date(annonce.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy à HH:mm", Locale.FRENCH);
        dateTextView.setText(getString(R.string.published) + " " + simpleDateFormat.format(date));
        contactTextView.setText(getString(R.string.contact) + " " + annonce.getPseudo());
        emailTextView.setText(annonce.getEmailContact());
        phoneTextView.setText(annonce.getTelContact());
        if (annonce.getImages().isEmpty()) {
            Glide.with(this).load(R.drawable.placeholder).into(imageView);
        } else {
            Random r = new Random();
            Glide.with(this)
                    .load(annonce.getImageUrl(r.nextInt(annonce.getImages().size())))
                    .into(imageView);
        }
    }
}
