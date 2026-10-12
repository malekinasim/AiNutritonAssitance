package com.nasim.ai.nutrion.ai;

public record Message (ChatRole role,String content){
    public static Message getMessage(ChatRole role,String content) {
        return new Message(role, content);
    }
}
