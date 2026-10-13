package com.nasim.ai.nutrion.tools.bmi;

import com.nasim.ai.nutrion.tools.FunctionDefinition;

public class CalculateBmiFunction implements FunctionDefinition<BmiRequest,BmiResponse> {
    @Override
    public String getName() {
        return "CalculateBmiFunction";
    }

    @Override
    public String getDescription() {
        return """
                calculate user bmi based on weight and height
                """;
    }

    @Override
    public Class<BmiRequest> getInputClass() {
        return BmiRequest.class;
    }

    @Override
    public BmiResponse execute(BmiRequest input) throws Exception {
        if(input==null || input.weight()==null || input.height()==null)
            throw new Exception("the weight and height shouldn't be null");
        if(input.weight().intValue()==0 || input.height().intValue()==0)
            throw new Exception("the weight and height shouldn't be zero");
        double bmi=calculate(input.weight(),input.height());
        return new BmiResponse(bmi,getCategory(bmi));
    }

    private double calculate(double weightKg, double heightCm) {
        double heightMeter = heightCm / 100;
        return weightKg / (heightMeter * heightMeter);
    }
    public static String getCategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obesity";
        }
    }
}
