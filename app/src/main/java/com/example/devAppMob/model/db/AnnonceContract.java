package com.example.devAppMob.model.db;

import android.provider.BaseColumns;

public final class AnnonceContract {
    private AnnonceContract() {
    }

    /* Inner class that defines the table contents */
    public static class AnnonceEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ID = "annonce_id";
        public static final String COLUMN_NAME_TITRE = "annonce_titre";
        public static final String COLUMN_NAME_DESCRIPTION = "annonce_description";
        public static final String COLUMN_NAME_PRIX = "annonce_prix";
        public static final String COLUMN_NAME_PSEUDO = "annonce_pseudo";
        public static final String COLUMN_NAME_EMAIL_CONTACT = "annonce_emailContact";
        public static final String COLUMN_NAME_TEL_CONTACT = "annonce_telContact";
        public static final String COLUMN_NAME_VILLE = "annonce_ville";
        public static final String COLUMN_NAME_CP = "annonce_cp";
        public static final String COLUMN_NAME_IMAGES = "annonce_images";
        public static final String COLUMN_NAME_DATE = "annonce_date";
    }
}
