package com.example.tp4;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tp4.adapter.ApiAnnonceAdapter;
import com.example.tp4.model.Annonce;
import com.example.tp4.model.ApiConf;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public abstract class AbstractApiConnectedActivity extends AbstractBaseActivity {
    protected boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Connecté au Wifi", Snackbar.LENGTH_LONG).show();
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Connecté au data", Snackbar.LENGTH_LONG).show();
                return true;
            }
        }
        Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Pas de connection", Snackbar.LENGTH_LONG).show();
        return false;
    }

    protected void apiCallPOST(View view, String apiMethod) {
        makeApiCall(ApiConf.API_URL, apiMethod);
    }

    protected void apiCallGET(View view, String apiMethod) {
        makeApiCall(ApiConf.API_URL + "?" + "apikey=" + ApiConf.API_KEY + "&method=" + apiMethod, apiMethod);
    }

    protected void apiCallGET(View view, String apiMethod, String paramName, String paramVal) {
        makeApiCall(
                ApiConf.API_URL + "?" +
                        "apikey=" + ApiConf.API_KEY +
                        "&method=" + apiMethod +
                        "&" + paramName + "=" + paramVal,
                apiMethod
        );
    }

    protected abstract void makeApiCall(String url, String method);

    protected Annonce parseResponseAsAnnonce(String response) {
        // créer Moshi et lui ajouter l'adapteur ApiPersonneAdapter
        Moshi moshi = new Moshi.Builder().add(new ApiAnnonceAdapter()).build();
        // créer l'adapteur pour Annonce
        JsonAdapter<Annonce> jsonAdapter = moshi.adapter(Annonce.class);

        try {
            // response est la String qui contient le JSON de la réponse
            return jsonAdapter.fromJson(response);

        } catch (IOException e) {
            Log.i("TP4", "Erreur I/O");
        }
        return null;
    }

    protected void startActivityFromAnnonce(Annonce annonce){
        Intent intent = new Intent(this, AnnonceViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("HELLO", annonce);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
