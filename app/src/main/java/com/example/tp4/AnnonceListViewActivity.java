package com.example.tp4;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnnonceListViewActivity extends AppCompatActivity implements OnAnnonceListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Annonce> itemsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annonce_recycler_layout);

/*        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);*/

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (isConnected(this)) {
            apiCall(getCurrentFocus());
        } else {
            itemsList = makeMockList();
            fillRecyclerView(itemsList);
        }
    }

    protected boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Snackbar.make(findViewById(R.id.recyclerView), "Connecté au Wifi", Snackbar.LENGTH_LONG).show();
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Snackbar.make(findViewById(R.id.recyclerView), "Connecté au data", Snackbar.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

    private static List<Annonce> makeMockList() {
        List<Annonce> mockList = new ArrayList<>();
        mockList.add(new Annonce("5a6a48033a66d996fc999c71",
                "Lorem aute elit aliqua veniam",
                "Exercitation elit nostrud dolore pariatur id enim enim excepteur elit pariatur. Cupidatat consectetur proident qui nisi quis aute sit incididunt nulla tempor aute ipsum sit. Sunt duis duis elit laboris dolore ex in voluptate id duis deserunt in consequat.",
                52,
                "Esperanza",
                "esperanzaclay@datagen.com",
                "38.98.64.70.49",
                "qui ipsum ea labore sunt",
                "95206",
                makeStringList("http://farm5.staticflickr.com/4609/38984233005_99ebb2a81a_q.jpg", "http://farm5.staticflickr.com/4760/39887610041_6f6820f60a_q.jpg", "http://farm5.staticflickr.com/4669/28115625339_d960cf228a_q.jpg"),
                1516502151
        ));

        mockList.add(new Annonce("5a6a4803e2c3b7b6cd7e5eba",
                "pariatur officia cupidatat sunt aute",
                "Incididunt est exercitation exercitation amet cupidatat qui labore eiusmod magna aute. Consectetur exercitation excepteur do qui commodo et velit laborum id adipisicing qui. Mollit commodo sint sint consectetur duis ipsum ipsum labore.",
                134,
                "Adriana",
                "adrianaclay@datagen.com",
                "64.39.81.97.73",
                "ad et qui sit eiusmod",
                "76942",
                makeStringList("http://farm5.staticflickr.com/4750/38928320495_aac4b0bc70_q.jpg", "http://farm5.staticflickr.com/4744/38960600945_fd78a28b4f_q.jpg", "http://farm5.staticflickr.com/4617/28117382549_64ee8dff4c_q.jpg"),
                1516902354
        ));

        mockList.add(new Annonce("5a6a4803a70b36aa0d0b4c7f",
                "dolore ad anim commodo aliqua",
                "Ad tempor elit eiusmod qui exercitation ea cillum Lorem irure tempor reprehenderit. Minim deserunt pariatur aute nostrud fugiat proident voluptate ea amet. Anim ipsum ea nostrud anim nisi excepteur ex amet non commodo aute fugiat.",
                275,
                "Cooley",
                "cooleyclay@datagen.com",
                "41.57.69.54.53",
                "pariatur excepteur mollit pariatur elit",
                "18828",
                makeStringList("http://farm5.staticflickr.com/4704/39825436801_d3242a903c_q.jpg", "http://farm5.staticflickr.com/4671/25002092047_3262fc12a4_q.jpg", "http://farm5.staticflickr.com/4657/38970125465_2f880c00dd_q.jpg"),
                1516807212
        ));

        mockList.add(new Annonce("5a6a4803dfb5ec01fc6e7d21",
                "tempor ullamco culpa anim nulla",
                "Duis sunt minim reprehenderit duis cillum culpa. Ullamco aliqua commodo nulla irure nisi cupidatat. Tempor qui cupidatat minim pariatur sunt reprehenderit culpa enim.",
                56,
                "Robin",
                "robinclay@datagen.com",
                "77.67.73.24.57",
                "velit dolor quis in aute",
                "13142",
                makeStringList("http://farm5.staticflickr.com/4654/39778867762_3737534605_q.jpg", "http://farm5.staticflickr.com/4763/39132150144_a8791871e0_q.jpg", "http://farm5.staticflickr.com/4671/39813441251_6ba5153b27_q.jpg"),
                1516519838
        ));

        //5e mockAnnonce
        mockList.add(new Annonce("5a6a4803562613a36c318508",
                "fugiat incididunt sunt nulla dolor",
                "Labore commodo do consequat do voluptate pariatur veniam aliquip. In officia ex excepteur velit commodo do in. Ea sit fugiat pariatur ullamco ex velit sit.",
                119,
                "Mcfarland",
                "mcfarlandclay@datagen.com",
                "93.77.34.27.69",
                "ipsum reprehenderit anim ut veniam",
                "63810",
                makeStringList("http://farm5.staticflickr.com/4712/39837547902_04f15943e7_q.jpg", "http://farm5.staticflickr.com/4676/39863866052_563c4eca6d_q.jpg", "http://farm5.staticflickr.com/4614/24981367097_c919fca391_q.jpg", "http://farm5.staticflickr.com/4718/28088913019_1ac6f44739_q.jpg"),
                1516158357
        ));

        return mockList;
    }

    private static List<String> makeStringList(String... uris) {
        List<String> stringList = new ArrayList();
        for (String uri : uris) {
            stringList.add(uri);
        }
        return stringList;
    }

    protected void apiCall(View view) {
        makeApiCall("https://ensweb.users.info.unicaen.fr/android-api/mock-api/liste.json");
        //makeApiCall("https://ensweb.users.info.unicaen.fr/android-api/mock-api/completeAdWithImages.json");
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
        Moshi moshi = new Moshi.Builder().add(new ApiAnnonceListAdapter()).build();
        // créer l'adapteur pour Annonce
        Type type = Types.newParameterizedType(List.class, Annonce.class);
        JsonAdapter<List<Annonce>> jsonAdapter = moshi.adapter(type);

        try {
            // response est la String qui contient le JSON de la réponse
            List<Annonce> wrapper = jsonAdapter.fromJson(response);

            if(!wrapper.isEmpty()){
                itemsList = wrapper;
                fillRecyclerView(wrapper);
            }
        } catch (IOException e) {
            Log.i("TP_DEBUG_WESH_WESH", "Erreur I/O");
        }
    }

    private void fillRecyclerView(List<Annonce> itemsList){
        mAdapter = new AnnonceListAdapter(itemsList, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAnnonceClick(int position) {
        //Log.d("hello", "onAnnonceClick : clicked " + itemsList.get(position).titre);

        Intent intent = new Intent(this, AnnonceViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("HELLO", itemsList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
