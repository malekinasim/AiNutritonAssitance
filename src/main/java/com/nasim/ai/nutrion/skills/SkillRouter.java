package com.nasim.ai.nutrion.skills;

import com.nasim.ai.nutrion.ai_api.EmbeddingAPI;
import com.nasim.ai.nutrion.util.SearchResult;
import com.nasim.ai.nutrion.util.VectorStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillRouter {


    private final EmbeddingAPI embeddingApi;
    private final VectorStore<SkillVectorEntry> entries;



    public SkillRouter(EmbeddingAPI embeddingApi, Map<String, Skill> skills, double threshold) {
        this.embeddingApi = embeddingApi;

        this.entries = new VectorStore<>(new VectorStore.Type(VectorStore.SearchType.THRESHOLD,threshold));

        List<Skill> list = new ArrayList<>(skills.values());
        List<double[]> vectors = embeddingApi.embedAll(list.stream().map(Skill::description).toList());
        for (int i = 0; i < list.size(); i++) {
            entries.add(new SkillVectorEntry( vectors.get(i),list.get(i)));
        }
    }

    public List<SearchResult<SkillVectorEntry>> match(String userMessage) {
        double[] query = embeddingApi.embed(userMessage);
        return entries.search(query);
    }


}
