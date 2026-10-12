package com.nasim.ai.nutrion.rag;

public record Document(Chunk chunk, double[] vector) {
}