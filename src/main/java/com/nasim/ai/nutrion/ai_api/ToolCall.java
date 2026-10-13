package com.nasim.ai.nutrion.ai_api;

import com.fasterxml.jackson.databind.JsonNode;

public record ToolCall(Function function) {
    public record Function(String name, JsonNode arguments) {}
}