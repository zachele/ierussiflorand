package com.example.shopflowers.service.geocoding;

import com.example.shopflowers.external.geocoding.GeocodingApiResponse;
import com.example.shopflowers.external.geocoding.NominatimGeocodingApiClient;

public class AddressValidationServiceImpl
        implements AddressValidationService {

    private final NominatimGeocodingApiClient apiClient;

    public AddressValidationServiceImpl() {
        this.apiClient = new NominatimGeocodingApiClient();
    }

    @Override
    public AddressValidationResult validateAddress(String address) {

        if (address == null || address.isBlank()) {
            return AddressValidationResult.invalid();
        }

        GeocodingApiResponse response =
                apiClient.searchAddress(address);

        if (!response.isFound()) {
            return AddressValidationResult.invalid();
        }

        return new AddressValidationResult(
                true,
                response.getDisplayName()
        );
    }
}