package com.nasim.ai.nutrion.health;

import com.nasim.ai.nutrion.ai_api.AiChatAPI;
import com.nasim.ai.nutrion.ai_api.EmbeddingAPI;

import java.util.Scanner;

public class HealthAssistantMain {

    public static void main(String[] args) {
        HealthAssistant assistant = new HealthAssistant(new AiChatAPI(), new EmbeddingAPI(),3);

        System.out.println("Indexing health documents...");
        assistant.indexDocument("/health/health_docs.txt");
        assistant.loadSkills("/health/skills");
        System.out.println("Ready. " + assistant.documentCount() + " documents, "
            + assistant.skillCount() + " skills. Type 'exit' to quit.");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            try {
                System.out.println("AI: " + assistant.ask(input));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                break;
            }
        }
    }
}
