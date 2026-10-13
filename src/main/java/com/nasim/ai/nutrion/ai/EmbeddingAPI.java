package com.nasim.ai.nutrion.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.SequencedCollection;

public class EmbeddingAPI {

    private record EmbedRequest(String model, List<String> input) {}

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;
    private final String model;

    public EmbeddingAPI() {
        this.baseUrl = System.getenv().getOrDefault("AI_BASE_URL", "http://localhost:11434");
        this.model = System.getenv().getOrDefault("EMBED_MODEL", "nomic-embed-text");
    }
    public double[] embed(String text) {
       return this.embedAll(List.of(text)).getFirst();
    }
    public  List<double[]> embedAll(List<String> texts) {
        try {
            String json = mapper.writeValueAsString(new EmbedRequest(model, texts));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/embed"))
                    .timeout(Duration.ofMinutes(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Embedding error " + response.statusCode() + ": " + response.body());
            }

           return extractResponse(response.body());

        } catch (IOException e) {
            throw new IllegalStateException("Could not reach the embedding model: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Embedding request was interrupted.", e);
        }
    }
    private List<double[]> extractResponse(String body) throws JsonProcessingException {
       List<double[]> result = new ArrayList<>();
        for (JsonNode vectorNode : mapper.readTree(body).path("embeddings")) {
            double[] vector = mapper.convertValue(vectorNode, double[].class);
            result.add(vector);
        }
        return result;
    }
}