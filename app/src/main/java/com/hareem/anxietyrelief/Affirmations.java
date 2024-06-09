package com.hareem.anxietyrelief;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Affirmations {
    private String quote;
    private String id;
    private String patientId;
    private boolean isFavorite;
    private List<Favorite> favorites;
    @Override
    public String toString() {
        return "Affirmations{" +
                "quote='" + quote + '\'' +
                ", id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }

    public String getPatientid() {
        return patientId;
    }

    public void setPatientid(String patientid) {
        patientId = patientid;
    }



    public Affirmations(String quote, String id) {
        this.quote = quote;
        this.id = id;
    }
    public Affirmations() {

    }



    public Affirmations(String patientid, String id, boolean isFavorite,String quote) {
        this.patientId = patientid;
        this.id = id;
        this.isFavorite = isFavorite;
        this.quote = quote;
    }
    public Affirmations( String id, boolean isFavorite,String quote) {

        this.id = id;
        this.isFavorite = isFavorite;
        this.quote = quote;
    }

    public Affirmations(String quote) {
        this.quote = quote;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public List<Favorite> getFavorites() {
        return favorites;
    }

    public static class Favorite {
        private String id;
        private boolean isFavorite;
        private String quote;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isFavorite() {
            return isFavorite;
        }

        public void setFavorite(boolean favorite) {
            isFavorite = favorite;
        }

        public String getQuote() {
            return quote;
        }

        public void setQuote(String quote) {
            this.quote = quote;
        }

        public Favorite(String id, boolean isFavorite, String quote) {
            this.id = id;
            this.isFavorite = isFavorite;
            this.quote = quote;
        }
        public String toString() {
            return "Affirmations{" +
                    "quote='" + quote + '\'' +
                    ", id='" + id + '\'' +
                    ", isFavorite=" + isFavorite +
                    '}';
        }
    }

}
