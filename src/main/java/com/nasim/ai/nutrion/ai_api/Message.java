package com.nasim.ai.nutrion.ai_api;

public record Message (ChatRole role, String content){
    public static Message getMessage(ChatRole role,String content) {
        return new Message(role, content);
    }
}
