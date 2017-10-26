package io.fourfinanceit.exception;


public enum ExceptionMessages {
    AMOUNT_NOT_NULL_MSG("Loan amount must be provided"),
    TERM_NOT_NULL_MSG("Loan term must be provided"),
    NAME_NOT_NULL_MSG("Name of applicant must be provided"),
    SURNAME_NOT_NULL_MSG("Surname of applicant must be provided"),
    PERSONAL_ID_NOT_NULL_MSG("Personal ID of applicant must be provided"),
    DECLINED_DUE_RISK_ANALYSIS("Attempt declined due risk analysis.");

    private final String description;

    ExceptionMessages(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}

