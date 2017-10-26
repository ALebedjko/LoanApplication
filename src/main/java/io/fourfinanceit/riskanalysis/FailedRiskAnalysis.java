package io.fourfinanceit.riskanalysis;

public class FailedRiskAnalysis extends RuntimeException{
    public FailedRiskAnalysis(String s) {
        super(s);
    }
}
