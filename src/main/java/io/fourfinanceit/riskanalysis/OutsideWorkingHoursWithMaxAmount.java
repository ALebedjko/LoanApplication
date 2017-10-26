package io.fourfinanceit.riskanalysis;

import io.fourfinanceit.domain.LoanRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.fourfinanceit.exception.ExceptionMessages.DECLINED_DUE_RISK_ANALYSIS;

@Component
public class OutsideWorkingHoursWithMaxAmount implements RiskAnalysis {

    @Value("${MAX_LOAN_AMOUNT}")
    BigDecimal MAX_LOAN_AMOUNT;

    @Value("${RISK_ANALYSIS_STARTING_HOUR}")
    private int RISK_ANALYSIS_STARTING_HOUR;

    @Value("${RISK_ANALYSIS_ENDING_HOUR}")
    private int RISK_ANALYSIS_ENDING_HOUR;

    @Override
    public void analyse(LoanRequest loanRequest) {
        if (isMaxLoanAmountAttempt(loanRequest) && isInRiskAnalysisTimeFrame(loanRequest)) {
            throw new FailedRiskAnalysis(DECLINED_DUE_RISK_ANALYSIS.getDescription());
        }
    }

    private boolean isMaxLoanAmountAttempt(LoanRequest request) {
        return MAX_LOAN_AMOUNT.equals(request.getAmount());
    }

    private boolean isInRiskAnalysisTimeFrame(LoanRequest loanRequest) {
        LocalDateTime localDateTime = loanRequest.getCreated();
        int hour = localDateTime.getHour();
        return hour >= RISK_ANALYSIS_STARTING_HOUR && hour < RISK_ANALYSIS_ENDING_HOUR;
    }

}
