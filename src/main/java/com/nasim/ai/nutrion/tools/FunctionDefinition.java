package com.nasim.ai.nutrion.tools;


import com.fasterxml.jackson.databind.JsonNode;
import com.nasim.ai.nutrion.util.SchemaMapper;

import java.util.Map;

public interface FunctionDefinition<I,R>{
    String getName();
    String getDescription();
    Class<I> getInputClass();

    R execute(I input) throws Exception;

    public default Map<String, Object> getFunctionDefinitionForOllama() {

        JsonNode parametersSchema = SchemaMapper.generateSchemaFromClass(this.getInputClass());

        return Map.of(
                        "name", this.getName(),
                        "description", this.getDescription(),
                        "parameters", parametersSchema
                );
    }
}
