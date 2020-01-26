package com.example.tp4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnnonceListAdapter extends RecyclerView.Adapter<AnnonceViewHolder> {

    List<Annonce> list;

    // donner la liste à l'adapteur
    public AnnonceListAdapter(List<Annonce> list) {
        this.list = list;
    }

    // créer les conteneurs de vue
    // et leur dire quel layout utiliser pour un item
    @Override
    public AnnonceViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup,false);
        return new AnnonceViewHolder(view);
    }

    // méthode appelée lorsque un conteneur de vue (pour un item) reçoit l'objet qui servira à remplir l'item
    // dire alors au conteneur de vue de faire ce "bind"
    @Override
    public void onBindViewHolder(AnnonceViewHolder annonceViewHolder, int position) {
        Annonce annonce = list.get(position);
        annonceViewHolder.bind(annonce);
    }

    // méthode obligatoire car déclarée abstraite dans la class mère
    @Override
    public int getItemCount() {
        return list.size();
    }
}
