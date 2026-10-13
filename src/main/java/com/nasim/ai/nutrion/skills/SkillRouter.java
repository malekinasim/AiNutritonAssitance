package com.nasim.ai.nutrion.skills;

import com.nasim.ai.nutrion.ai.EmbeddingAPI;
import com.nasim.ai.nutrion.util.SearchResult;
import com.nasim.ai.nutrion.util.VectorStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillRouter {


    private final EmbeddingAPI embeddingApi;
    private final VectorStore<SkillVector> entries;



    public SkillRouter(EmbeddingAPI embeddingApi, Map<String, Skill> skills, double threshold) {
        this.embeddingApi = embeddingApi;

        var type=VectorStore.Type.THRESHOLD;
        type.setValue(threshold);
        this.entries = new VectorStore<>(type);

        List<Skill> list = new ArrayList<>(skills.values());
        List<double[]> vectors = embeddingApi.embedAll(list.stream().map(Skill::description).toList());
        for (int i = 0; i < list.size(); i++) {
            entries.add(new SkillVector( vectors.get(i),list.get(i)));
        }
    }

    public List<SearchResult<SkillVector>> match(String userMessage) {
        double[] query = embeddingApi.embed(userMessage);
        return entries.search(query);
    }


}
