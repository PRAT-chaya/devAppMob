package com.example.tp4.adapter;

import android.util.Log;

import com.example.tp4.model.Annonce;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;

import java.io.IOException;
import java.util.List;

public class ApiAnnonceListAdapter {
    @FromJson
    List<Annonce> fromJson(JsonReader reader, JsonAdapter<List<Annonce>> delegate) throws IOException {
        List<Annonce> result = null;

        // démarrer le parsing du Json
        reader.beginObject();
        while (reader.hasNext()) {
            // récupérer le nom de la clé
            String name = reader.nextName();
            if (name.equals("success")) {
                boolean success = reader.nextBoolean();
                if (!success) {
                    // @todo : récupérer le message d'erreur et le donner à l'exception
                    // @todo : créer une exception spécifique pour la distinguer des IOException
                    throw new IOException("API a répondu FALSE");
                }
            } else if (name.equals("response")) {
                // déléguer l'extraction à l'adapteur qui transforme du Json en Annonce
                result = delegate.fromJson(reader);
            } else {
                // dans notre cas on ne devrait pas avoir d'autres clés que success et response dans le Json
                throw new IOException("Response contient des données non conformes");
            }
        }
        // fermer le reader
        reader.endObject();
        return result;
    }
}
