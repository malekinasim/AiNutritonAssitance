package com.nasim.ai.nutrion.rag;

import com.nasim.ai.nutrion.util.VectorEntry;

public class DocumentChunkVectorEntry extends VectorEntry {
    private final DocumentChunk documentChunk;


    public DocumentChunkVectorEntry(double[] vector, DocumentChunk documentChunk) {
        super(vector);
        this.documentChunk = documentChunk;
    }

    public DocumentChunk getChunk() {
        return documentChunk;
    }
}