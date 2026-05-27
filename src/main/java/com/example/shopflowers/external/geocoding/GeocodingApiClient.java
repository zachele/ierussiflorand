package com.example.shopflowers.external.geocoding;

public interface GeocodingApiClient {

    GeocodingApiResponse searchAddress(String address);
}