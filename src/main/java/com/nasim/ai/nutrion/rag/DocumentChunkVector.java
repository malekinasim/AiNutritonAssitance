package com.nasim.ai.nutrion.rag;

import com.nasim.ai.nutrion.util.Vector;

public class DocumentChunkVector extends Vector {
    private final DocumentChunk documentChunk;


    public DocumentChunkVector(double[] vector, DocumentChunk documentChunk) {
        super(vector);
        this.documentChunk = documentChunk;
    }

    public DocumentChunk getChunk() {
        return documentChunk;
    }
}