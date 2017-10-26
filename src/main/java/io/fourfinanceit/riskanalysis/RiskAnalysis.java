package io.fourfinanceit.riskanalysis;

import io.fourfinanceit.domain.LoanRequest;

public interface RiskAnalysis {
    void analyse(LoanRequest loanRequest);
}
