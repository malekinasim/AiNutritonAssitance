package com.nasim.ai.nutrion.util;

public record SearchResult<T extends Vector>(T item, double score) {
}
