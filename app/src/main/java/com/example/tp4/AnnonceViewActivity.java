package com.example.tp4;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.tp4.adapter.ApiAnnonceAdapter;
import com.example.tp4.adapter.SlidingImageAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnnonceViewActivity extends AbstractBaseActivity {
    private TextView adTitleTextView, priceTextView, locationTextView, descTextView,
            dateTextView, contactTextView, emailTextView, phoneTextView;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private List<String> imageUrlList;

    private Annonce fedAnnonce;
    private boolean isEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annonce_view);
        sharedPrefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        initToolbar();
        initView();

        isEditable = false;
        imageUrlList = new ArrayList();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("HELLO")) {
            fedAnnonce = (Annonce) bundle.getSerializable("HELLO");
            if (fedAnnonce.getImages() != null) {
                imageUrlList = fedAnnonce.getImages();
            }
            if (fedAnnonce.getPseudo().equals(sharedPrefs.getString(Profil.username, ""))) {
                isEditable = true;
            }
        } else {
            fedAnnonce = new MockAnnonce();
        }


        if (fedAnnonce != null) {
            fillView(fedAnnonce);
        } else if (isConnected(this)) {
            apiCall(getCurrentFocus());
        } else {
            fillView(fedAnnonce);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (isEditable) {
            menuInflater.inflate(R.menu.editable_annonce_menu, menu);
        } else {
            menuInflater.inflate(R.menu.base_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_all_annonces:
                Intent intent = new Intent(AnnonceViewActivity.this, AnnonceListViewActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_edit_annonce:
                intent = new Intent(AnnonceViewActivity.this, AnnonceEditorActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("HELLO", fedAnnonce);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            case R.id.action_show_my_annonces:
                intent = new Intent(this, AnnonceListViewActivity.class);
                bundle = new Bundle();
                bundle.putString("PSEUDO_TO_FILTER", sharedPrefs.getString(Profil.username, ""));
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            case R.id.action_add_pic:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_add_annonce:
                intent = new Intent(AnnonceViewActivity.this, AnnonceCreatorActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_profil:
                intent = new Intent(AnnonceViewActivity.this, ProfilViewActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
        // créer Moshi et lui ajouter l'adapteur ApiPersonneAdapter
        Moshi moshi = new Moshi.Builder().add(new ApiAnnonceAdapter()).build();
        // créer l'adapteur pour Annonce
        JsonAdapter<Annonce> jsonAdapter = moshi.adapter(Annonce.class);

        try {
            // response est la String qui contient le JSON de la réponse
            Annonce annonce = jsonAdapter.fromJson(response);
            fillView(annonce);
        } catch (IOException e) {
            Log.i("TP4", "Erreur I/O");
        }
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


        if(imageUrlList.isEmpty()) {
            ImageView defaultImageView = findViewById(R.id.defaultImageView);
            Glide.with(this)
                    .load(R.drawable.placeholder)
                    .into(defaultImageView);

        } else {
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(new SlidingImageAdapter(this, imageUrlList));
            CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(mPager);
            final float density = getResources().getDisplayMetrics().density;

            //Set circle indicator radius
            indicator.setRadius(5 * density);
            NUM_PAGES = imageUrlList.size();

            // Pager listener over indicator
            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrolled(int pos, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int pos) {

                }
            });
        }
    }

    @Override
    protected void initView() {
        adTitleTextView = findViewById(R.id.adTitleTextView);
        priceTextView = findViewById(R.id.priceTextView);
        locationTextView = findViewById(R.id.locationTextView);
        descTextView = findViewById(R.id.descTextView);
        dateTextView = findViewById(R.id.dateTextView);
        contactTextView = findViewById(R.id.contactTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
    }
}
