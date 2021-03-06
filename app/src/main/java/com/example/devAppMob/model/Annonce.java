package com.example.devAppMob.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Annonce implements Serializable {
    private String id;
    private String titre;
    private String description;
    private int prix;
    private String pseudo;
    private String emailContact;
    private String telContact;
    private String ville;
    private String cp;
    private List<String> images;
    private long date;

    public Annonce(String id, String titre, String description, int prix,
                   String pseudo, String emailContact, String telContact,
                   String ville, String cp, List<String> images, long date) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.pseudo = pseudo;
        this.emailContact = emailContact;
        this.telContact = telContact;
        this.ville = ville;
        this.cp = cp;
        this.images = images;
        this.date = date;
    }

    public Annonce(){
        this.id = null;
        this.titre = null;
        this.description = null;
        this.prix = 0;
        this.pseudo = null;
        this.emailContact = null;
        this.telContact = null;
        this.ville = null;
        this.cp = null;
        this.images = new ArrayList<String>();
        this.date = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmailContact() {
        return emailContact;
    }

    public void setEmailContact(String emailContact) {
        this.emailContact = emailContact;
    }

    public String getTelContact() {
        return telContact;
    }

    public void setTelContact(String telContact) {
        this.telContact = telContact;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public List<String> getImages() {
        return images;
    }

    public String getImageUrl(int index){ return images.get(index);}

    public void setImages(List<String> images) {
        this.images = images;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
