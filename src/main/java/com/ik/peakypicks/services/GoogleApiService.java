package com.ik.peakypicks.services;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ik.peakypicks.model.Pick;
import com.ik.peakypicks.model.Place;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.logging.Logger;

@Service
public class GoogleApiService {

    private final RestTemplate restTemplate;
    private static final Logger logger = Logger.getLogger(GoogleApiService.class.getName());

    @Value("${google.api.key}")
    private final String googleApiKey;
    final double RATING_WEIGHT = 0.7;
    final double REVIEW_WEIGHT = 0.3;


    public GoogleApiService(RestTemplate restTemplate, @Value("${google.api.key}")String googleApiKey) {
        this.restTemplate = restTemplate;
        this.googleApiKey = googleApiKey;
    }

    private ResponseEntity<String> callExternalApi(String textQuery) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        String requestBody = String.format("{\"textQuery\": \"%s\"}", textQuery);

        String url = "https://places.googleapis.com/v1/places:searchText?fields=" +
                "places.id," +
                "places.displayName," +
                "places.formattedAddress," +
                "places.rating," +
                "places.userRatingCount," +
                "places.types," +
                "places.primaryType," +
                "places.viewport";
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", googleApiKey);

//        HashMap<String, String> params = new HashMap<>();
//        params.put("fields", "places.displayName,places.formattedAddress,places.rating");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            System.out.println(result);
            return result;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Log the error for debugging
            logger.severe("Error calling external API: "+ e.getMessage());

            // Return an appropriate response based on the error
            return new ResponseEntity<>("API error: " + e.getMessage(), e.getStatusCode());

        } catch (RestClientException e) {
            // Handle other types of rest client exceptions
            logger.severe("Rest client exception: " + e.getMessage());
            return new ResponseEntity<>("Client error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            // Catch any other exceptions (fallback for unexpected issues)
            logger.severe("Unexpected error: " +  e.getMessage());
            return new ResponseEntity<>("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<Place> temporarilySaveResults(HttpEntity<String> apiResult ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Place> places = new ArrayList<>();
        JsonNode rootNode = mapper.readTree(apiResult.getBody());

        JsonNode placesArray = rootNode.path("places");
        if (placesArray.isArray()) {
            for (JsonNode place : placesArray) {
                String googleId = place.path("id").asText();
                String name = place.path("displayName").path("text").asText();
                String address = place.path("formattedAddress").asText();
                double rating = place.path("rating").asDouble();
                int userRatingCount = place.path("userRatingCount").asInt();  // Handle as integer
                List<String> types = new ArrayList<>();
                JsonNode typesArray = place.path("types");
                if (typesArray.isArray()) {
                    for (JsonNode type : typesArray) {
                        types.add(type.asText());
                    }
                }
                String primaryType = place.path("primaryType").asText(null); // Default to null if not found

                // Geometry details - for Google interactive map on the front end
                JsonNode lowNode = place.path("viewport").path("low");
                List<Double> lowCoordinates = Arrays.asList(
                        lowNode.path("latitude").asDouble(),
                        lowNode.path("longitude").asDouble()
                );
                JsonNode highNode = place.path("viewport").path("high");
                List<Double> highCoordinates = Arrays.asList(
                        highNode.path("latitude").asDouble(),
                        highNode.path("longitude").asDouble()
                );

                Place newPlace = new Place(googleId, name, address, rating, userRatingCount, types, primaryType, lowCoordinates, highCoordinates);
                places.add(newPlace);
            }
        }
        return places;
    }

    private List<Pick> filterResults(List<Place> places) {
        List<Pick> picks = new ArrayList<>();

        // Get the max review count found in a result
        int maxReviewCount = Integer.MIN_VALUE;
        for (Place place : places) {
            maxReviewCount = Math.max(maxReviewCount, place.getUserRatingCount());
        }

        // Iterate over all results and calculate their score
        for (Place place : places) {
            place.calculatePlaceScore(maxReviewCount, RATING_WEIGHT, REVIEW_WEIGHT);
        }
        // Rank results in ascending order
        places.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));

        // Pick top 5 and transform to Picks
        for (int i = 0; i < Math.min(places.size(), 5); i++) {
            Place place = places.get(i);

            Pick pick = new Pick(
                    place.getGoogleId(),
                    place.getDisplayName(),
                    place.getFormattedAddress(),
                    place.getRating(),
                    place.getUserRatingCount(),
                    place.getTypes(),
                    place.getPrimaryType(),
                    place.getLowCordinates(),
                    place.getHighCordinates()
            );
            picks.add(pick);
        }
        return picks;
    }

    public List<Pick> getPicks(String textQuery) throws JsonProcessingException {
        ResponseEntity<String> apiResult = callExternalApi(textQuery);
        List<Place> fetchedPlaces = temporarilySaveResults(apiResult);
        return filterResults(fetchedPlaces);
    }

}
