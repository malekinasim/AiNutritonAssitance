package com.nasim.ai.nutrion.health;

import com.nasim.ai.nutrion.ai.AiChatAPI;
import com.nasim.ai.nutrion.ai.ChatRole;
import com.nasim.ai.nutrion.ai.EmbeddingAPI;
import com.nasim.ai.nutrion.ai.Message;
import com.nasim.ai.nutrion.rag.*;
import com.nasim.ai.nutrion.skills.Skill;
import com.nasim.ai.nutrion.skills.SkillLoader;
import com.nasim.ai.nutrion.skills.SkillRouter;
import com.nasim.ai.nutrion.skills.SkillVectorEntry;
import com.nasim.ai.nutrion.util.SearchResult;
import com.nasim.ai.nutrion.util.VectorStore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HealthAssistant {
    private static final int TOP_K = 3;
    private static final int MAX_SKILL_ROUNDS = 2;
    private static final double SKILL_THRESHOLD = 0.60;


    private static final Pattern LOAD_SKILL =
            Pattern.compile("\\[\\[\\s*LOAD_SKILL\\s*:\\s*([\\w-]+)\\s*]]", Pattern.CASE_INSENSITIVE);

    private static final String BASE_PROMPT = """
            You are a friendly nutrition assistant.
            Rules:
            - For facts, use ONLY the information in the SOURCES section of the user's message.
            - If the sources do not contain the answer, say: "I don't have information about that."
            - Mention the title of the source(s) you used.
            - Never diagnose illnesses. For medical conditions or medications, advise the user to talk to a doctor.
            - Keep answers short and simple.
            """;
    private final List<Message> history = new ArrayList<>();
    private final AiChatAPI chatApi;
    private final EmbeddingAPI embeddingApi;
    private final VectorStore<DocumentChunkVectorEntry> documentVectorStore;
    private Map<String, Skill> skills = Map.of();
    private SkillRouter router;
    private final Map<String, Skill> activeSkills = new LinkedHashMap<>();

    public HealthAssistant(AiChatAPI chatApi, EmbeddingAPI embeddingApi,int topK) {
        this.chatApi = chatApi;
        this.embeddingApi = embeddingApi;
       this.documentVectorStore= new VectorStore<>(new VectorStore.Type(VectorStore.SearchType.TOP, (double) topK));
    }

    public void indexDocument(String ... documentsPaths) {
        for(String documentsPath :documentsPaths) {
            List<DocumentChunk> documentChunks = DocumentLoader.loadFromClasspath(documentsPath);
            documentChunks.forEach(c->{
                        double[] vector = embeddingApi.embed(c.title()+":"+c.text());
                        documentVectorStore.add(new DocumentChunkVectorEntry( vector,c));
                    });
        }
    }
    public void loadSkills(String baseDir) {
      this.skills= SkillLoader.loadFromClasspath(baseDir);
        this.router=new SkillRouter(embeddingApi,skills,SKILL_THRESHOLD);

    }
    public int documentCount() {
        return documentVectorStore.size();
    }

    public int skillCount() {return skills.size(); }

    public String ask(String question) throws Exception {
        //Skill Matcher
        if (router != null) {
            for (SearchResult<SkillVectorEntry> m : router.match(question)) {
                activate(m.item().getSkill(), String.format("router, score %.3f", m.score()));
            }
        }
        //RAG
        List<SearchResult<DocumentChunkVectorEntry>> results = documentVectorStore.search(embeddingApi.embed(question));
        printRetrieved(results);

        String sources = results.stream()
                .map(r -> "[" + r.item().getChunk().title() + "] " + r.item().getChunk().text())
                .collect(Collectors.joining("\n"));
        String augmented = "SOURCES:\n" + sources + "\n\nQUESTION: " + question;
        String answer = "";
        for (int round = 0; round <= MAX_SKILL_ROUNDS; round++) {

            List<Message> request = new ArrayList<>();
            request.add(Message.getMessage(ChatRole.SYSTEM, buildSystemPrompt()));
            request.addAll(history);
            request.add(Message.getMessage(ChatRole.USER, augmented));
            answer = chatApi.chat(request);

            boolean loadedNew = false;
            Matcher m = LOAD_SKILL.matcher(answer);
            while (m.find()) {
                Skill requested = skills.get(m.group(1));
                if (requested != null) {
                    loadedNew |= activate(requested,"requested by model");
                } else {
                    System.out.println("  [unknown skill requested: " + m.group(1) + "]");
                }
            }
            if (!loadedNew) break;
        }

        answer = LOAD_SKILL.matcher(answer).replaceAll("").strip();
        remember(question, answer);
        return answer;
    }

    private void remember(String question, String answer) {
        history.add(Message.getMessage(ChatRole.USER,question));
        history.add(Message.getMessage(ChatRole.ASSISTANT,answer));
    }

    private boolean activate(Skill skill,String reason) {
        if (activeSkills.putIfAbsent(skill.name(), skill) == null) {
            System.out.println("  [skill activated: " + skill.name() + " (" + reason + ")]");
            return true;
        }
        return false;
    }

    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder(BASE_PROMPT);

        sb.append("\nAVAILABLE SKILLS (name: when to use):\n");
        skills.values().forEach(skill ->
                sb.append("- ").append(skill.toString()).append('\n'));
        sb.append("""
                If an active skill (or the conversation) says you need another skill that is not active yet,
                reply with ONLY this line and nothing else: [[LOAD_SKILL: skill-name]]
                """);

        for (Skill s : activeSkills.values()) {
            sb.append("\n=== ACTIVE SKILL: ").append(s.name()).append(" ===\n")
                    .append(s.instructions()).append('\n');
        }
        return  sb.toString();
    }
    private void printRetrieved(List<SearchResult<DocumentChunkVectorEntry>> results) {
        System.out.println("  [retrieved]");
        for (SearchResult<DocumentChunkVectorEntry> r : results) {
            System.out.printf("   %.3f  %s%n", r.score(), r.item().getChunk().title());
        }
    }
}
