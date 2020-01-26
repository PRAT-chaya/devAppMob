package com.example.tp4;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Random;

public class AnnonceViewHolder extends RecyclerView.ViewHolder {

    private TextView titreTextView, prixTextView, locationTextView;
    private ImageView imageView;

    // itemView est la vue correspondante à 1 item
    public AnnonceViewHolder(View itemView) {
        super(itemView);
        //obtenir les éléments de la vue d'un item
        titreTextView = (TextView) itemView.findViewById(R.id.itemTitreTextView);
        prixTextView = (TextView) itemView.findViewById(R.id.itemPrixTextView);
        locationTextView = (TextView) itemView.findViewById(R.id.itemLocationTextView);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.itemImageView);
    }

    public void bind(Annonce annonce) {
        titreTextView.setText(annonce.getTitre());
        prixTextView.setText(annonce.getPrix());
        locationTextView.setText(annonce.getCp() + " " + annonce.getVille());
        Random r = new Random();
        Glide.with(imageView)
                .load(annonce.getImageUrl(r.nextInt(annonce.getImages().size())))
                .into(imageView);
    }
}
