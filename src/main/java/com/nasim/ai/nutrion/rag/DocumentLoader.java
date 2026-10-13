package com.nasim.ai.nutrion.rag;

import com.nasim.ai.nutrion.util.Utils;
import java.util.ArrayList;
import java.util.List;

public class DocumentLoader {

    public static List<DocumentChunk> loadFromClasspath(String path) {
        return parse(Utils.read(path));
    }

    static List<DocumentChunk> parse(String content) {
        List<DocumentChunk> documentChunks = new ArrayList<>();

        for (String block : content.replace("\r\n", "\n").split("\n\\s*\n")) {
            block = block.strip();
            if (!block.startsWith("#")) continue;

            int newline = block.indexOf('\n');
            if (newline == -1) continue;

            String title = block.substring(1, newline).strip();
            String text = block.substring(newline + 1).strip().replace("\n", " ");
            documentChunks.add(new DocumentChunk(title, text));
        }
        return documentChunks;
    }
}
