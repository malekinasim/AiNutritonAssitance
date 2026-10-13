package com.nasim.ai.nutrion.ai_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nasim.ai.nutrion.tools.ToolDefinition;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;


public class AiChatAPI {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String baseUrl;
    private final String model;
    private final ObjectMapper mapper = new ObjectMapper();
    public AiChatAPI() {
        this.baseUrl = System.getenv().getOrDefault("AI_BASE_URL", "http://localhost:11434");
        this.model = System.getenv().getOrDefault("AI_MODEL", "qwen2.5:3b");
    }
    public Map<String,String> chat(List<Message> messages,List<ToolDefinition> tools) throws Exception {
        Map<String, Object>  requestBody = Map.of(
                "model",this.model,
                "messages", messages,
                 "stream", false,
                "tools",tools.stream().map( ToolDefinition::getToolDefinitionForModel).toList()
        );
         return callPrompt(requestBody);
    }
    public Map<String,String> callPrompt(Map<String, Object>  requestBody) throws Exception {
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl+"/api/chat"))
                    .timeout(Duration.ofMinutes(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return Map.of("answer",response.body());
            }
            return Map.of("answer",extractResponse(response.body()),
                    "tool_calls", extractToolCalls(response.body()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Could not connect to the AI model: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception( "The AI request was interrupted."+e.getMessage());
        }
    }

    private String extractResponse(String body) throws JsonProcessingException {
        JsonNode json = mapper.readTree(body);
        return json.path("message").path("content").asText();
    }
    private String extractToolCalls(String body) throws JsonProcessingException {
        JsonNode json = mapper.readTree(body);
        return json.path("message").path("tool_calls").asText();
    }
}
