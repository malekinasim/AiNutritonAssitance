package com.nasim.ai.nutrion.health;

import com.nasim.ai.nutrion.ai.AiChatAPI;
import com.nasim.ai.nutrion.ai.ChatRole;
import com.nasim.ai.nutrion.ai.EmbeddingAPI;
import com.nasim.ai.nutrion.ai.Message;
import com.nasim.ai.nutrion.rag.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HealthAssistant {
    private static final int TOP_K = 3;

    private static final String SYSTEM_PROMPT = """
            You are a friendly nutrition assistant.
            Rules:
            - Answer ONLY using the information in the SOURCES section of the user's message.
            - If the sources do not contain the answer, say: "I don't have information about that."
            - Mention the title of the source(s) you used.
            - Never diagnose illnesses. For medical conditions or medications, advise the user to talk to a doctor.
            - Keep answers short and simple.
            """;
    private final List<Message> history = new ArrayList<>();
    private final AiChatAPI chatApi;
    private final EmbeddingAPI embeddingApi;
    private final VectorStore store = new VectorStore();

    public HealthAssistant(AiChatAPI chatApi, EmbeddingAPI embeddingApi) {
        this.chatApi = chatApi;
        this.embeddingApi = embeddingApi;
        history.add(Message.getMessage(ChatRole.SYSTEM,SYSTEM_PROMPT));
    }

    public void index(String ... documentsPaths) {
        for(String documentsPath :documentsPaths) {
            List<Chunk> chunks = DocumentLoader.loadFromClasspath(documentsPath);
            chunks.forEach(c->{
                        double[] vector = embeddingApi.embed(c.title()+":"+c.text());
                        store.add(new Document(c, vector));
                    });
        }
    }

    public int documentCount() {
        return store.size();
    }

    public String ask(String question) {
        List<SearchResult> results = store.search(embeddingApi.embed(question), TOP_K);
        printRetrieved(results);

        String sources = results.stream()
                .map(r -> "[" + r.document().chunk().title() + "] " + r.document().chunk().text())
                .collect(Collectors.joining("\n"));
        String augmented = "SOURCES:\n" + sources + "\n\nQUESTION: " + question;

        List<Message> request = new ArrayList<>(history);
        request.add(Message.getMessage(ChatRole.USER,augmented));
        String answer = chatApi.chat(request);


        history.add(Message.getMessage(ChatRole.USER,question));
        history.add(Message.getMessage(ChatRole.ASSISTANT,answer));

        return answer;
    }

    private void printRetrieved(List<SearchResult> results) {
        System.out.println("  [retrieved]");
        for (SearchResult r : results) {
            System.out.printf("   %.3f  %s%n", r.score(), r.document().chunk().title());
        }
    }
}
