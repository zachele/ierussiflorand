package com.example.shopflowers.external.geocoding;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class NominatimGeocodingApiClient implements GeocodingApiClient {

    private static final String BASE_URL =
            "https://nominatim.openstreetmap.org/search";

    private static final String DISPLAY_NAME_TOKEN = "\"display_name\":\"";
    private static final String LATITUDE_TOKEN = "\"lat\":\"";
    private static final String LONGITUDE_TOKEN = "\"lon\":\"";
    private static final String VALUE_END_TOKEN = "\"";

    private final HttpClient httpClient;

    public NominatimGeocodingApiClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public GeocodingApiResponse searchAddress(String address) {

        try {
            String encodedAddress =
                    URLEncoder.encode(address, StandardCharsets.UTF_8);

            String requestUrl =
                    BASE_URL
                            + "?q="
                            + encodedAddress
                            + "&format=json&limit=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header(
                            "User-Agent",
                            "IerussiFlowersISPWProject/1.0"
                    )
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            return parseResponse(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return GeocodingApiResponse.notFound();

        } catch (IOException | NumberFormatException e) {
            return GeocodingApiResponse.notFound();
        }
    }

    private GeocodingApiResponse parseResponse(String body) {

        if (body == null || body.isBlank() || body.equals("[]")) {
            return GeocodingApiResponse.notFound();
        }

        String displayName = extractValue(body, DISPLAY_NAME_TOKEN);

        double latitude =
                Double.parseDouble(
                        extractValue(body, LATITUDE_TOKEN)
                );

        double longitude =
                Double.parseDouble(
                        extractValue(body, LONGITUDE_TOKEN)
                );

        return new GeocodingApiResponse(
                true,
                displayName,
                latitude,
                longitude
        );
    }

    private String extractValue(String source, String startToken) {

        int startIndex = source.indexOf(startToken);

        if (startIndex < 0) {
            return "";
        }

        startIndex += startToken.length();

        int endIndex = source.indexOf(VALUE_END_TOKEN, startIndex);

        if (endIndex < 0) {
            return "";
        }

        return source.substring(startIndex, endIndex);
    }
}