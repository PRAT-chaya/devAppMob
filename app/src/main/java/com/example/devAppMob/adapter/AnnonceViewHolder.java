package com.example.devAppMob.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.devAppMob.model.Annonce;
import com.example.devAppMob.R;

public class AnnonceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView titreTextView, prixTextView, locationTextView;
    private ImageView imageView;
    private OnAnnonceListener onAnnonceListener;

    // itemView est la vue correspondante à 1 item
    AnnonceViewHolder(View itemView, OnAnnonceListener onAnnonceListener) {
        super(itemView);
        //obtenir les éléments de la vue d'un item
        titreTextView = (TextView) itemView.findViewById(R.id.itemTitreTextView);
        prixTextView = (TextView) itemView.findViewById(R.id.itemPrixTextView);
        locationTextView = (TextView) itemView.findViewById(R.id.itemLocationTextView);
        imageView = (ImageView) itemView.findViewById(R.id.itemImageView);
        this.onAnnonceListener = onAnnonceListener;
        itemView.setOnClickListener(this);
    }

    void bind(Annonce annonce) {
        titreTextView.setText(annonce.getTitre());
        prixTextView.setText(annonce.getPrix() + "€");
        locationTextView.setText(annonce.getCp() + " " + annonce.getVille());
        Activity a = (Activity) itemView.getContext();
        int imageNbr = annonce.getImages().size();
        if (imageNbr > 0) {
            Glide.with(a)
                .load(annonce.getImageUrl(0))
                .into(imageView);
        }
    }

    @Override
    public void onClick(View v) {
        this.onAnnonceListener.onAnnonceClick(getAdapterPosition());
    }
}
