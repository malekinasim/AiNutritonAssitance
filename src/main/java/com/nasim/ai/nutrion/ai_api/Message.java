package com.nasim.ai.nutrion.ai_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
        ChatRole role,
        String content,
        @JsonProperty("tool_calls") List<ToolCall> toolCalls,
        @JsonProperty("tool_name") String toolName) {

    public static Message user(String content)   { return new Message(ChatRole.USER, content, null, null); }
    public static Message system(String content)   { return new Message(ChatRole.SYSTEM, content, null, null); }
    public static Message assistant(String content)   { return new Message(ChatRole.ASSISTANT, content, null, null); }
    public static Message tool(String name, String result) { return new Message(ChatRole.TOOL, result, null, name); }

}

