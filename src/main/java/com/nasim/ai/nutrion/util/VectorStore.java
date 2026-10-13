package com.nasim.ai.nutrion.util;

import java.util.ArrayList;
import java.util.List;

public class VectorStore<T extends Vector> {
    VectorStore.Type type;
    List<T> stores =new ArrayList<>();
    public VectorStore(Type type) {
        this.type = type;
    }

    public  void add(T item){
         stores.add(item);
    }
    public int size() {
        return stores.size();
    }

    public List<SearchResult<T>> search(double[] queryVector) {
        List<SearchResult<T>> list= stores.stream()
                .map(doc -> new SearchResult<T>(doc, Utils.cosineSimilarity(queryVector, doc.getVector())))
                .sorted((a, b) -> Double.compare(b.score(), a.score()))
                .toList();
        if(this.type==Type.TOP) return list.stream().limit(this.type.value.intValue()).toList();
        else return list.stream().filter(item->item.score()>=type.value).toList();
    }
    public enum Type{
        THRESHOLD(50.0),
        TOP(3.0);
        private Double value;

        public Double getValue() {
            return value;
        }

        Type(Double value) {
            this.value = value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

    }
}
