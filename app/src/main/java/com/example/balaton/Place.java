package com.example.balaton;

public class Place {
    private String placeName;
    private String shortDetail;
    private float ratedInfo;
    private int imageResource;

    public Place() {
    }

    public Place(String placeName, String shortDetail, float ratedInfo, int imageResource) {
        this.placeName = placeName;
        this.shortDetail = shortDetail;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getShortDetail() {
        return shortDetail;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource() {
        return imageResource;
    }
}
