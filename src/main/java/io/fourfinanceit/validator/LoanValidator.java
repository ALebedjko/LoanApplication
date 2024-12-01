package io.fourfinanceit.validator;

import io.fourfinanceit.domain.Client;
import io.fourfinanceit.domain.LoanRequest;
import io.fourfinanceit.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static io.fourfinanceit.exception.ExceptionMessages.*;

@Component
public class LoanValidator implements Validator {

    @Value("${MAX_LOAN_AMOUNT}")
    BigDecimal MAX_LOAN_AMOUNT;

    private final
    CustomerRepository customerRepository;

    @Autowired
    public LoanValidator(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return LoanRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoanRequest request;

        if (target instanceof LoanRequest) {
            request = (LoanRequest) target;
        } else {
            errors.reject("Bad request");
            return;
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "amount", AMOUNT_NOT_NULL_MSG.getDescription());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "termInDays", TERM_NOT_NULL_MSG.getDescription());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", NAME_NOT_NULL_MSG.getDescription());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", SURNAME_NOT_NULL_MSG.getDescription());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalId", PERSONAL_ID_NOT_NULL_MSG.getDescription());

        rejectIfMaxLoanAmountExceeded(request, errors);

        if (errors.getFieldErrorCount() == 0) {
            String personalId = request.getPersonalId();
            Client client = customerRepository.findOneByPersonalId(personalId);

            if (client != null && (!client.getName().equals(request.getName()) || !client.getSurname().equals(request.getSurname()))) {
                errors.reject("Provided name and surname doesn't corresponds existing customer with personal id " + personalId);
            }

        }

    }

    private void rejectIfMaxLoanAmountExceeded(LoanRequest request, Errors errors) {
        if (request.getAmount() != null && MAX_LOAN_AMOUNT.compareTo(request.getAmount()) == -1) {
            errors.reject("The attempt to take loan is made with amount, which is greater than max allowed amount. " +
                    "Maximum loan amount is " + MAX_LOAN_AMOUNT);
        }

    }

}


