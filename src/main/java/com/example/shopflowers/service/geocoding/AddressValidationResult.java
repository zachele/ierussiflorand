package com.example.shopflowers.service.geocoding;

public record AddressValidationResult(
        boolean valid,
        String normalizedAddress
) {

    public boolean isValid() {
        return valid;
    }

    @SuppressWarnings("unused")
    public String getNormalizedAddress() {
        return normalizedAddress;
    }

    public static AddressValidationResult invalid() {
        return new AddressValidationResult(false, null);
    }
}