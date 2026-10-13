package com.nasim.ai.nutrion.util;

public record SearchResult<T extends VectorEntry>(T item, double score) {
}
