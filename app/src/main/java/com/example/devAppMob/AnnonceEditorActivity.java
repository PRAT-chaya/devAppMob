package com.example.devAppMob;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.devAppMob.dialogs.ChoosePictureSourceDialog;
import com.example.devAppMob.model.Annonce;
import com.example.devAppMob.model.ApiConf;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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

public class AnnonceEditorActivity extends AbstractApiConnectedActivity implements ChoosePictureSourceDialog.ChoosePictureSourceDialogListener {

    protected EditText title, price, description, ville, cp;
    protected Button addImageButton, btnEnvoi;
    protected TextView textTargetUri;
    protected ImageView targetImage;

    private Annonce fedAnnonce;
    protected Uri targetUri = null;
    protected Bitmap bitmap = null;

    static final int REQUEST_TAKE_PHOTO = 1;
    protected String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_creator);

        initToolbar();
        initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("HELLO")) {
            fedAnnonce = (Annonce) bundle.getSerializable("HELLO");
            fillTextFields();
        }

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoosePictureSourceDialog();
            }
        });

        btnEnvoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textHasChanged() && !imageIsLoaded()) {
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                targetUri = data.getData();
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                if (data != null) {
                    bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                } else {
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                File temp = File.createTempFile("temp", ".jpg");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                byte[] myByteArray = baos.toByteArray();

                try (FileOutputStream fos = new FileOutputStream(temp)) {
                    fos.write(myByteArray);
                }
                int newWidth = 600;
                int newHeight = (int) Math.round(newWidth*0.75);
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            } catch (IOException e) {
                Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur: impossible de créer temp", Snackbar.LENGTH_LONG).show();
            }

            if(targetUri != null) {
                textTargetUri.setText(targetUri.getPath());
            } else {
                textTargetUri.setError("Erreur de chargement de l'image");
            }

            targetImage.setImageBitmap(bitmap);
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

        return !strTitre.equals(fedAnnonce.getTitre())
                || !strPrix.equals(String.valueOf(fedAnnonce.getPrix()))
                || !strDesc.equals(fedAnnonce.getDescription())
                || !strVille.equals(fedAnnonce.getVille())
                || !strCp.equals(fedAnnonce.getCp());
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

            if (!strTitre.equals(fedAnnonce.getTitre())) {
                builder.add(ApiConf.PARAM.titre, strTitre);
            }
            if (!strPrix.equals(String.valueOf(fedAnnonce.getPrix()))) {
                builder.add(ApiConf.PARAM.prix, price.getText().toString());
            }
            if (!strDesc.equals(fedAnnonce.getDescription())) {
                builder.add(ApiConf.PARAM.description, description.getText().toString());
            }
            if (!strVille.equals(fedAnnonce.getVille())) {
                builder.add(ApiConf.PARAM.ville, ville.getText().toString());
            }
            if (!strCp.equals(fedAnnonce.getCp())) {
                builder.add(ApiConf.PARAM.cp, cp.getText().toString());
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

            final boolean canStartView = isLastRequest;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Échec du POST, réponse négative", Snackbar.LENGTH_LONG).show();
                            throw new IOException("Unexpected HTTP code" + response);
                        } else {
                            Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Envoi réussi !", Snackbar.LENGTH_LONG).show();
                            assert responseBody != null;
                            final String body = responseBody.string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (canStartView) {
                                        Annonce annonce = parseResponseAsAnnonce(body);
                                        startActivityFromAnnonce(annonce);
                                    }
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

    protected void searchPhoneGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    protected void showChoosePictureSourceDialog() {
        DialogFragment dialog = new ChoosePictureSourceDialog();
        dialog.show(getSupportFragmentManager(), "choose_picture_dialog");
    }

    @Override
    public void onDialogPhoneGalleryClick(DialogFragment dialog) {
        searchPhoneGallery();
    }

    @Override
    public void onDialogTakePictureClick(DialogFragment dialog) {
        dispatchTakePictureIntent();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                galleryAddPic();
            } catch (IOException e) {
                Snackbar.make(findViewById(R.id.annonce_creator_main_layout), "Erreur lors de la création du fichier", Snackbar.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                targetUri = FileProvider.getUriForFile(this,
                        "com.example.tp4.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}


