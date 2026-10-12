package com.nasim.ai.nutrion.rag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VectorStore {
    List<Document> documents=new ArrayList<>();
    public  void add(Document document){
         documents.add(document);
    }
    public int size() {
        return documents.size();
    }

    public List<SearchResult> search(double[] queryVector, int k) {
        return documents.stream()
                .map(doc -> new SearchResult(doc, cosineSimilarity(queryVector, doc.vector())))
                .sorted(Comparator.comparingDouble(SearchResult::score).reversed())
                .limit(k)
                .toList();
    }

    static double cosineSimilarity(double[] a, double[] b) {

        if (a == null || b == null || a.length != b.length) {
            throw new IllegalArgumentException("the vectors shouldn't be null and should have equals size");
        }
        if (a.length == 0) {
            return 0.0;
        }

        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
