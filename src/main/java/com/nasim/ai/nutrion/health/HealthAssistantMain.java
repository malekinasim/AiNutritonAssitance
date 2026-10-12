package com.nasim.ai.nutrion.health;

import com.nasim.ai.nutrion.ai.AiChatAPI;
import com.nasim.ai.nutrion.ai.EmbeddingAPI;

import java.util.Scanner;

public class HealthAssistantMain {

    public static void main(String[] args) {
        HealthAssistant assistant = new HealthAssistant(new AiChatAPI(), new EmbeddingAPI());

        System.out.println("Indexing health documents...");
        assistant.index("/health/health_docs.txt");
        System.out.println("Ready. " + assistant.documentCount() + " documents indexed. Type 'exit' to quit.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            System.out.println("AI: " + assistant.ask(input));
        }
    }
}
