package com.example.devAppMob;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.devAppMob.adapter.AnnonceListAdapter;
import com.example.devAppMob.adapter.ApiAnnonceListAdapter;
import com.example.devAppMob.adapter.OnAnnonceListener;
import com.example.devAppMob.model.Annonce;
import com.example.devAppMob.model.ApiConf;
import com.example.devAppMob.model.Profil;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnnonceListViewActivity extends AbstractApiConnectedActivity implements OnAnnonceListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private SwipeRefreshLayout swipeContainer;

    private List<Annonce> itemsList;
    private boolean listByPseudo;
    private String pseudoToFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annonce_recycler_layout);

        sharedPrefs = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);

        listByPseudo = false;

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(BundleKeys.FILTER_USERNAME)) {
            initToolbar();
        } else {
            initToolbarWithoutUpButton();
        }
        initView();

        if (isConnected(this)) {
            if (bundle != null) {
                if (bundle.containsKey(BundleKeys.DELETED_ANNONCE)
                        && (bundle.getInt(BundleKeys.DELETED_ANNONCE) == BundleVals.DELETED_ANNONCE)) {
                    Snackbar.make(findViewById(R.id.recyclerViewMain), R.string.ad_deleted, Snackbar.LENGTH_SHORT).show();
                }
                if (bundle.containsKey(BundleKeys.FILTER_USERNAME)) {
                    pseudoToFilter = bundle.getString(BundleKeys.FILTER_USERNAME);
                    if (pseudoToFilter != null && !pseudoToFilter.equals("")) {
                        listByPseudo = true;
                        apiCallGET(getCurrentFocus(), ApiConf.METHOD.GET.listbyPseudo,
                                ApiConf.PARAM.pseudo, pseudoToFilter);
                    } else {
                        apiCallGET(getCurrentFocus(), ApiConf.METHOD.GET.listAll);
                    }
                } else {
                    apiCallGET(getCurrentFocus(), ApiConf.METHOD.GET.listAll);
                }
            } else {
                apiCallGET(getCurrentFocus(), ApiConf.METHOD.GET.listAll);
            }
        }
    }


    protected void initToolbarWithoutUpButton() {
        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AnnonceListViewActivity.this, AnnonceListViewActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_all_annonces:
                intent = new Intent(AnnonceListViewActivity.this, AnnonceListViewActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_my_annonces:
                intent = new Intent(this, AnnonceListViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(BundleKeys.FILTER_USERNAME, sharedPrefs.getString(Profil.username, ""));
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            case R.id.action_refresh_list:
                swipeContainer.setRefreshing(true);
                refreshView();
                return true;

            case R.id.action_add_annonce:
                intent = new Intent(AnnonceListViewActivity.this, AnnonceCreatorActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_profil:
                intent = new Intent(AnnonceListViewActivity.this, ProfilViewActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_my_local_annonces:
                intent = new Intent(AnnonceListViewActivity.this, LocalStorageAnnonceListViewActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

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
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected HTTP code" + response);
                    }
                    assert responseBody != null;
                    final String body = responseBody.string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseResponseAsAnnonceList(body);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void parseResponseAsAnnonceList(String response) {
        // créer Moshi et lui ajouter l'adapteur ApiPersonneAdapter
        Moshi moshi = new Moshi.Builder().add(new ApiAnnonceListAdapter()).build();
        // créer l'adapteur pour Annonce
        Type type = Types.newParameterizedType(List.class, Annonce.class);
        JsonAdapter<List<Annonce>> jsonAdapter = moshi.adapter(type);

        try {
            // response est la String qui contient le JSON de la réponse
            List<Annonce> wrapper = jsonAdapter.fromJson(response);
            assert wrapper != null;
            if (!wrapper.isEmpty()) {
                itemsList = wrapper;
                fillRecyclerView(wrapper);
            }
        } catch (IOException e) {

        }
    }

    private void fillRecyclerView(List<Annonce> itemsList) {
        mAdapter = new AnnonceListAdapter(itemsList, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAnnonceClick(int position) {
        Intent intent = new Intent(this, AnnonceViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("HELLO", itemsList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });
    }

    private void refreshView() {
        if (isConnected(getApplicationContext())) {
            if (listByPseudo) {
                apiCallGET(getCurrentFocus(), ApiConf.METHOD.GET.listbyPseudo,
                        ApiConf.PARAM.pseudo, pseudoToFilter);
            } else {
                apiCallGET(getCurrentFocus(), ApiConf.METHOD.GET.listAll);
            }
        }
        swipeContainer.setRefreshing(false);
    }
}
