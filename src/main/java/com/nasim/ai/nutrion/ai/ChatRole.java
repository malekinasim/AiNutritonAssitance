package com.nasim.ai.nutrion.ai;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatRole {
    SYSTEM, USER, ASSISTANT;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
