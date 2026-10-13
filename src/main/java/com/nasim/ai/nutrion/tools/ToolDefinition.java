package com.nasim.ai.nutrion.tools;

import java.util.Map;

public record ToolDefinition<I,R>(ToolType type, FunctionDefinition<I,R> functionDefinition) {

    public Map<String, Object> getToolDefinitionForModel() {

        return Map.of(
                "type",this.type() ,
                "function",this.getToolDefinitionForModel()
        );
    }
}

