package com.nasim.ai.nutrion.tools;

import java.util.*;

public class ToolLoader {


    public static  Map<String, ToolDefinition>  loadToolsList() {
        // 1. You must use the raw class for the service interface type, then cast if necessary
        @SuppressWarnings({"rawtypes", "unchecked"})
        ServiceLoader<ToolDefinition> loader = ServiceLoader.load(ToolDefinition.class);

        Map<String, ToolDefinition> toolsList = new HashMap<>();

        // 3. Iterate through the loader and populate the list
        for (var tool : loader) {
            toolsList.putIfAbsent(tool.getFunctionDefinition().getName(),tool);
        }

        return toolsList;
    }

}
