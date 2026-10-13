package com.nasim.ai.nutrion.ai_api;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatRole {
    SYSTEM, USER, ASSISTANT,TOOL;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
