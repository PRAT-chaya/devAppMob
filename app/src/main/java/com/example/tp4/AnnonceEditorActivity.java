package com.example.tp4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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

public class AnnonceEditorActivity extends AbstractApiConnectedActivity {

    protected EditText title, price, description, ville, cp;
    protected Button addImageButton, btnEnvoi;
    protected TextView textTargetUri;
    protected ImageView targetImage;

    private Annonce fedAnnonce;
    private Uri targetUri;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_creator);

        initToolbar();
        initView();

        targetUri = null;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("HELLO")) {
            fedAnnonce = (Annonce) bundle.getSerializable("HELLO");
            fillTextFields();
        }

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        btnEnvoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textHasChanged() && !imageIsLoaded()){
                    Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Vous n'avez rien modifié", Snackbar.LENGTH_LONG).show();
                } else {
                    if (textHasChanged()) {
                        if (isValid()) {
                            if (isConnected(getApplicationContext())) {
                                apiCallPOST(getCurrentFocus(), ApiConf.METHOD.POST.update);
                            } else {
                                Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur de connexion", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur dans le formulaire", Snackbar.LENGTH_LONG).show();
                        }
                    }
                    if (imageIsLoaded()) {
                        if (isConnected(getApplicationContext())) {
                            apiCallPOST(getCurrentFocus(), ApiConf.METHOD.POST.addImage);
                        } else {
                            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur de connexion", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        sharedPrefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            targetUri = data.getData();
            textTargetUri.setText(targetUri.getPath());
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillTextFields() {
        title.setText(fedAnnonce.getTitre());
        price.setText(String.valueOf(fedAnnonce.getPrix()));
        ville.setText(fedAnnonce.getVille());
        cp.setText(fedAnnonce.getCp());
        description.setText(fedAnnonce.getDescription());
    }

    protected boolean isValid() {

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
            ville.setError("Le nom de ville ne peux être vide");
            hasError = true;
        }

        if (TextUtils.isEmpty(strCp)) {
            cp.setError("Le Code Postal ne peux être vide");
            hasError = true;
        }

        return !hasError;


    }

    protected boolean imageIsLoaded() {
        if (targetUri == null || targetUri.toString().equals("")) {
            textTargetUri.setError("Image non choisie");
            return false;
        } else {
            return true;
        }
    }

    protected boolean textHasChanged() {
        String strTitre = title.getText().toString();
        String strPrix = price.getText().toString();
        String strDesc = description.getText().toString();
        String strVille = ville.getText().toString();
        String strCp = cp.getText().toString();

        if (!strTitre.equals(fedAnnonce.getTitre())
                || !strPrix.equals(fedAnnonce.getPrix())
                || !strDesc.equals(fedAnnonce.getDescription())
                || !strVille.equals(fedAnnonce.getVille())
                || !strCp.equals(fedAnnonce.getCp())
        ) {
            return true;
        }
        return false;
    }

    @Override
    protected void makeApiCall(String url, String method) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = null;

        boolean isLastRequest = false;

        if (method.equals(ApiConf.METHOD.POST.update)) {
            FormBody.Builder builder = new FormBody.Builder()
                    .add(ApiConf.PARAM.apikey, ApiConf.API_KEY)
                    .add(ApiConf.PARAM.method, method)
                    .add(ApiConf.PARAM.id, fedAnnonce.getId());

            String strTitre = title.getText().toString();
            String strPrix = price.getText().toString();
            String strDesc = description.getText().toString();
            String strVille = ville.getText().toString();
            String strCp = cp.getText().toString();

            if(!strTitre.equals(fedAnnonce.getTitre())){
                builder.add(ApiConf.PARAM.titre, strTitre);
            }
            if (!strPrix.equals(fedAnnonce.getPrix())) {
                builder.add(ApiConf.PARAM.prix, price.getText().toString());
            }
            if (!strDesc.equals(fedAnnonce.getDescription())) {
                builder.add(ApiConf.PARAM.description, description.getText().toString());
            }
            if (!strVille.equals(fedAnnonce.getVille())) {
                builder.add(ApiConf.PARAM.ville, ville.getText().toString());
            }
            if (!strCp.equals(fedAnnonce.getCp())) {
                builder .add(ApiConf.PARAM.cp, cp.getText().toString());
            }

            body = builder.build();

            isLastRequest = !imageIsLoaded();

        } else if (method.equals(ApiConf.METHOD.POST.addImage)) {
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
                        .addFormDataPart(ApiConf.PARAM.id, fedAnnonce.getId())
                        .addFormDataPart(ApiConf.PARAM.photo, targetUri.getPath(),
                                RequestBody.create(MediaType.parse("application/octet-stream"), temp))
                        .build();


            } catch (IOException e) {
                Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur: impossible de créer temp", Snackbar.LENGTH_LONG).show();
            }
            isLastRequest = true;
        }

        if (body != null) {
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();

            final boolean canParse = isLastRequest;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Échec du POST, réponse négative", Snackbar.LENGTH_LONG).show();
                            throw new IOException("Unexpected HTTP code" + response);
                        } else {
                            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Envoi réussi !", Snackbar.LENGTH_LONG).show();
                            final String body = responseBody.string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(canParse){
                                        parseResponse(body);
                                    }
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
        } else {
            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur: Corps de requête vide", Snackbar.LENGTH_LONG).show();
        }

    }


    @Override
    protected void initView() {
        btnEnvoi = (Button) findViewById(R.id.buttonEnvoi);
        title = (EditText) findViewById(R.id.editTitle);
        price = (EditText) findViewById(R.id.editPrix);
        description = (EditText) findViewById(R.id.editDescription);
        ville = (EditText) findViewById(R.id.editVille);
        cp = (EditText) findViewById(R.id.editCP);
        addImageButton = findViewById(R.id.addImageButton);

        textTargetUri = (TextView) findViewById(R.id.targeturi);
        targetImage = (ImageView) findViewById(R.id.targetimage);
    }
}


