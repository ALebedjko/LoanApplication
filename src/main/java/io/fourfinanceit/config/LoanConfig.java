package io.fourfinanceit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoanConfig {
    @Value("${INTEREST_FACTOR_PER_WEEK}")
    private float INTEREST_FACTOR_PER_WEEK;

    public float getInterestFactorPerWeek() {
        return INTEREST_FACTOR_PER_WEEK;
    }
}
