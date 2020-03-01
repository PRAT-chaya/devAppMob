package com.example.devAppMob;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devAppMob.adapter.AnnonceListAdapter;
import com.example.devAppMob.adapter.OnAnnonceListener;
import com.example.devAppMob.model.Annonce;
import com.example.devAppMob.model.db.AnnonceContract.AnnonceEntry;
import com.example.devAppMob.model.db.AnnonceDbHelper;
import com.example.devAppMob.model.db.AnnonceDbManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocalStorageAnnonceListViewActivity extends AbstractBaseActivity implements OnAnnonceListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private List<Annonce> itemsList;

    private AnnonceDbManager dbManager;
    private Cursor mCursor;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_annonce_recycler_layout);
        initView();
        initToolbar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(BundleKeys.DELETED_ANNONCE)
                    && (bundle.getInt(BundleKeys.DELETED_ANNONCE) == BundleVals.DELETED_ANNONCE)) {
                Snackbar.make(findViewById(R.id.localRecyclerViewMain), R.string.ad_deleted, Snackbar.LENGTH_SHORT).show();
            }
        }

        dbManager = new AnnonceDbManager(new AnnonceDbHelper(getApplicationContext()));
        mCursor = dbManager.readAll();

        itemsList = new ArrayList<Annonce>();
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            String id = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_ID));
            String titre = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_TITRE));
            String description = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_DESCRIPTION));
            int prix = mCursor.getInt(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_PRIX));
            String pseudo = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_PSEUDO));
            String emailContact = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_EMAIL_CONTACT));
            String telContact = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_TEL_CONTACT));
            String ville = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_VILLE));
            String cp = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_CP));

            long date = 0;
            String dateStr = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_DATE));
            DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date dateObj = null;
            try {
                dateObj = df.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (dateObj != null) {
                date = dateObj.getTime();
            }

            String imagesJson = mCursor.getString(mCursor.getColumnIndex(AnnonceEntry.COLUMN_NAME_IMAGES));
            Moshi moshi = new Moshi.Builder().build();
            Type types = Types.newParameterizedType(List.class, String.class);
            JsonAdapter<List<String>> jsonAdapter = moshi.adapter(types);
            List<String> images = new ArrayList<>();
            try {
                images = jsonAdapter.fromJson(imagesJson);
            } catch (IOException e) {
            }

            Annonce annonce = new Annonce(id, titre, description, prix,
                    pseudo, emailContact, telContact, ville, cp, images, date);
            itemsList.add(annonce);
        }
        mCursor.close();
        fillRecyclerView(itemsList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.localRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        bundle.putInt(BundleKeys.IS_LOCAL, BundleVals.IS_LOCAL);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
