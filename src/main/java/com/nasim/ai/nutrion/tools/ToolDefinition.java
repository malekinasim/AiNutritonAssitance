package com.nasim.ai.nutrion.tools;

import java.util.Map;

public interface ToolDefinition<F extends FunctionDefinition<I,R>,I,R> {
    ToolType getType();
    F getFunctionDefinition();
    default Map<String, Object> getToolDefinitionForModel() {
        return Map.of(
                "type",this.getType(),
                "function",this.getFunctionDefinition()
        );
    }
}

