package com.nasim.ai.nutrion.util;

import com.nasim.ai.nutrion.rag.DocumentLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static double cosineSimilarity(double[] a, double[] b) {

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

    public static String read(String path){
        try (InputStream in = DocumentLoader.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("File not found on classpath: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + path, e);
        }
    }
}
