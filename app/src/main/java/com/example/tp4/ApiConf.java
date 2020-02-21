package com.example.tp4;

public final class ApiConf {
    public final static String API_URL = "https://ensweb.users.info.unicaen.fr/android-api/";
    public final static String API_KEY = "21907858";

    public static final class METHOD {
        public static final class GET {
            public final static String randomAd = "randomAd";
            public final static String details = "details";
            public final static String listAll = "listAll";
            public final static String listbyPseudo = "listByPseudo";
            public final static String delete = "delete";
            public final static String reset = "reset";
            public final static String populate = "populate";
        }

        public static final class POST {
            public final static String save = "save";
            public final static String update = "update";
            public final static String addImage = "addImage";
        }
    }

    public static final class PARAM {
        public final static String id = "id";
        public final static String pseudo = "pseudo";
    }
}
