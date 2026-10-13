package com.nasim.ai.nutrion.tools.bmi;

import com.google.auto.service.AutoService;
import com.nasim.ai.nutrion.tools.ToolDefinition;
import com.nasim.ai.nutrion.tools.ToolType;

@AutoService(ToolDefinition.class)
public class CalculateBmiTool implements ToolDefinition<CalculateBmiFunction,BmiRequest,BmiResponse> {
    @Override
    public ToolType getType() {
        return ToolType.FUNCTION;
    }

    @Override
    public CalculateBmiFunction getFunctionDefinition() {
        return new CalculateBmiFunction();
    }
}
