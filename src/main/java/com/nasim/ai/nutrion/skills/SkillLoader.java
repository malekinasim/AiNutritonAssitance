package com.nasim.ai.nutrion.skills;

import com.nasim.ai.nutrion.rag.DocumentLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class SkillLoader {

    public static Map<String, Skill> loadFromClasspath(String baseDir) {
        Map<String, Skill> skills = new LinkedHashMap<>();

        for (String line : read(baseDir + "/index.txt").split("\n")) {
            String name = line.strip();
            if (name.isEmpty() || name.startsWith("#")) continue;

            Skill skill = parse(read(baseDir + "/" + name + "/SKILL.md"));
            skills.put(skill.name(), skill);
        }
        return skills;
    }
    private static Skill parse(String content){
        content = content.replace("\r\n", "\n").strip();
        if (!content.startsWith("---")) {
            throw new IllegalArgumentException("SKILL.md must start with a --- frontmatter block");
        }
        int end = content.indexOf("\n---", 3);
        if (end == -1) {
            throw new IllegalArgumentException("SKILL.md frontmatter is not closed with ---");
        }
        String[] lins=content.substring(3, end).split("\n");
        String name=null, description=null;
        for (String line :lins) {
            int colon = line.indexOf(':');
            if (colon == -1) continue;
            String key = line.substring(0, colon).strip();
            String value = line.substring(colon + 1).strip();
            if (key.equals("name")) name = value;
            if (key.equals("description")) description = value;
        }

        if (name == null || description == null) {
            throw new IllegalArgumentException("SKILL.md needs both name and description");
        }

        String instructions = content.substring(end + 4).strip();
        return new Skill(name, description, instructions);
    }
    private static String read(String path){
        try (InputStream in = DocumentLoader.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("File not found on classpath: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + path, e);
        }
    }
}
