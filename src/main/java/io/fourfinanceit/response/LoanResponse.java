package io.fourfinanceit.response;

import io.fourfinanceit.domain.LoanExtension;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class LoanResponse {

    Long id;
    BigDecimal amount;
    BigDecimal interest;
    Integer termInDays;
    List<LoanExtension> loanExtensions;
    LocalDateTime created;
    LocalDateTime updated;
}
