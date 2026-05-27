package com.example.shopflowers.external.geocoding;

public class GeocodingApiResponse {

    private final boolean found;
    private final String displayName;
    private final double latitude;
    private final double longitude;

    public GeocodingApiResponse(
            boolean found,
            String displayName,
            double latitude,
            double longitude
    ) {
        this.found = found;
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isFound() {
        return found;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static GeocodingApiResponse notFound() {
        return new GeocodingApiResponse(false, null, 0.0, 0.0);
    }
}