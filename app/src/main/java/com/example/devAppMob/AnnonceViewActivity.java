package com.example.devAppMob;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.devAppMob.adapter.SlidingImageAdapter;
import com.example.devAppMob.dialogs.DeleteAnnonceDialog;
import com.example.devAppMob.model.ApiConf;
import com.example.devAppMob.model.Profil;
import com.example.devAppMob.model.db.AnnonceDbHelper;
import com.example.devAppMob.model.db.AnnonceDbManager;
import com.example.devAppMob.model.Annonce;
import com.google.android.material.snackbar.Snackbar;
import com.viewpagerindicator.CirclePageIndicator;

import org.jetbrains.annotations.NotNull;

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

public class AnnonceViewActivity extends AbstractApiConnectedActivity implements DeleteAnnonceDialog.DeleteAnnonceDialogListener {
    private TextView adTitleTextView, priceTextView, locationTextView, descTextView,
            dateTextView, contactTextView, emailTextView, phoneTextView;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private List<String> imageUrlList;

    private Annonce fedAnnonce;
    private boolean isEditable;
    private boolean isLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annonce_view);
        sharedPrefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        initToolbar();
        initView();

        isEditable = false;
        isLocal = false;
        imageUrlList = new ArrayList();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("HELLO")) {
                fedAnnonce = (Annonce) bundle.getSerializable("HELLO");
                assert fedAnnonce != null;
                if (fedAnnonce.getImages() != null) {
                    imageUrlList = fedAnnonce.getImages();
                }
                if (fedAnnonce.getPseudo().equals(sharedPrefs.getString(Profil.username, ""))) {
                    isEditable = true;
                }
            }
            if (bundle.containsKey(BundleKeys.IS_LOCAL)
                    && bundle.getInt(BundleKeys.IS_LOCAL) == BundleVals.IS_LOCAL) {
                isLocal = true;
            }
        }

        if (fedAnnonce != null) {
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
        } else if(isLocal) {
            menuInflater.inflate(R.menu.local_annonce_menu, menu);

        } else {
            menuInflater.inflate(R.menu.annonce_view_menu, menu);
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

            case R.id.action_delete_annonce:
                showDeleteAnnonceDialog();
                return true;

            case R.id.action_show_my_annonces:
                intent = new Intent(this, AnnonceListViewActivity.class);
                bundle = new Bundle();
                bundle.putString("PSEUDO_TO_FILTER", sharedPrefs.getString(Profil.username, ""));
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            case R.id.action_add_annonce:
                intent = new Intent(AnnonceViewActivity.this, AnnonceCreatorActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_profil:
                intent = new Intent(AnnonceViewActivity.this, ProfilViewActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_save_annonce_local:
                if (fedAnnonce != null) {
                    AnnonceDbManager dbManager = new AnnonceDbManager(new AnnonceDbHelper(this));
                    long newRowId = dbManager.add(fedAnnonce);
                    if (newRowId != -1) {
                        Snackbar.make(findViewById(R.id.main), R.string.ad_saved, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(findViewById(R.id.main), R.string.ad_save_failure, Snackbar.LENGTH_SHORT).show();
                    }
                }
                return true;

            case R.id.action_show_my_local_annonces:
                intent = new Intent(AnnonceViewActivity.this, LocalStorageAnnonceListViewActivity.class);
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
        assert cm != null;
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


        if (imageUrlList.isEmpty()) {
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
    protected void makeApiCall(String url, String method) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected HTTP code" + response);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), AnnonceListViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(AnnonceListViewActivity.BundleKeys.FILTER_USERNAME, sharedPrefs.getString(Profil.username, ""));
                        bundle.putInt(AnnonceListViewActivity.BundleKeys.DELETED_ANNONCE, 1);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
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

    protected void showDeleteAnnonceDialog() {
        DialogFragment dialog = new DeleteAnnonceDialog();
        dialog.show(getSupportFragmentManager(), "delete_annonce_dialog");
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        if (fedAnnonce != null) {
            if(isLocal){
                AnnonceDbManager dbManager = new AnnonceDbManager(new AnnonceDbHelper(this));
                if (dbManager.delete(fedAnnonce.getId()) > 0){
                    Intent intent = new Intent(getApplicationContext(), LocalStorageAnnonceListViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(AnnonceListViewActivity.BundleKeys.DELETED_ANNONCE, 1);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Snackbar.make(findViewById(R.id.main), "Échec de la suppression de l'annonce", Snackbar.LENGTH_SHORT).show();

                }
            } else {
                apiCallGET(getCurrentFocus(), ApiConf.METHOD.GET.delete, ApiConf.PARAM.id, fedAnnonce.getId());
            }
        } else {
            Snackbar.make(findViewById(R.id.main), "Échec de la suppression de l'annonce", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {

    }
}
