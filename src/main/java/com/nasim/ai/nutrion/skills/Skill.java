package com.nasim.ai.nutrion.skills;

public record Skill(String name, String description, String instructions) {
    @Override
    public String toString() {
        return this.name+":"+this.description;
    }
}
