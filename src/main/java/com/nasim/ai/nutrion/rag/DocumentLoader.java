package com.nasim.ai.nutrion.rag;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DocumentLoader {

    public static List<Chunk> loadFromClasspath(String path) {
        try (InputStream in = DocumentLoader.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("File not found on classpath: " + path);
            }
            return parse(new String(in.readAllBytes(),StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + path, e);
        }
    }

    static List<Chunk> parse(String content) {
        List<Chunk> chunks = new ArrayList<>();

        for (String block : content.replace("\r\n", "\n").split("\n\\s*\n")) {
            block = block.strip();
            if (!block.startsWith("#")) continue;

            int newline = block.indexOf('\n');
            if (newline == -1) continue;

            String title = block.substring(1, newline).strip();
            String text = block.substring(newline + 1).strip().replace("\n", " ");
            chunks.add(new Chunk(title, text));
        }
        return chunks;
    }
}
