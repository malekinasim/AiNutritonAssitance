package com.nasim.ai.nutrion.tools;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ToolType {
    FUNCTION,RETRIEVAL,CODE_INTERPRETER;
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

}
