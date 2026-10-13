package com.nasim.ai.nutrion.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.*;

public class SchemaMapper {

    public static <I> JsonNode generateSchemaFromClass(Class<I> inputClass) {
        SchemaGeneratorConfigBuilder configBuilder =
                new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);
        SchemaGeneratorConfig config =  configBuilder.with(
                new com.github.victools.jsonschema.generator.impl.module.SimpleTypeModule())
                .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                .with(Option.FLATTENED_ENUMS_FROM_TOSTRING)
                .build();

        SchemaGenerator generator = new SchemaGenerator(config);

        return generator.generateSchema(inputClass);

    }
    public static <R> R generateClassTypeFromJson(Class<R> Clazz,String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, Clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
