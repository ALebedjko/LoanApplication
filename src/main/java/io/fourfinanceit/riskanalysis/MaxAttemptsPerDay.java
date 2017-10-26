package io.fourfinanceit.riskanalysis;

import io.fourfinanceit.domain.LoanRequest;
import io.fourfinanceit.repository.LoanRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.fourfinanceit.exception.ExceptionMessages.DECLINED_DUE_RISK_ANALYSIS;
import static io.fourfinanceit.utils.DateUtils.today;

@Component
public class MaxAttemptsPerDay implements RiskAnalysis {

    private final LoanRequestRepository loanRequestRepository;
    @Value("${MAX_APPLICATIONS_PER_DAY}")
    Long MAX_APPLICATIONS_PER_DAY;

    public MaxAttemptsPerDay(LoanRequestRepository loanRequestRepository) {
        this.loanRequestRepository = loanRequestRepository;
    }

    @Override
    public void analyse(LoanRequest loanRequest) {
        String remoteAddress = loanRequest.getRemoteAddress();
        Long applicationCountFromSameIp = loanRequestRepository.q(remoteAddress, today());
        if (applicationCountFromSameIp > MAX_APPLICATIONS_PER_DAY) {
            throw new FailedRiskAnalysis(DECLINED_DUE_RISK_ANALYSIS.getDescription());
        }
    }
}
