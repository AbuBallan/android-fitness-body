package com.jordan.fitnessbody.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

public class GymLocation {

    private String id;

    private GeoPoint latlng;

    private String title;

    public GymLocation() {
    }

    public GymLocation(GeoPoint latlng, String title) {
        this.latlng = latlng;
        this.title = title;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public GeoPoint getLatlng() {
        return latlng;
    }

    public String getTitle() {
        return title;
    }
}
