package io.fourfinanceit.controller;

import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.domain.LoanRequest;
import io.fourfinanceit.exception.ExceptionJSONInfo;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.response.LoanResponse;
import io.fourfinanceit.riskanalysis.FailedRiskAnalysis;
import io.fourfinanceit.service.LoanService;
import io.fourfinanceit.validator.LoanValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static io.fourfinanceit.response.LoanResponse.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/loan")
public class LoanController {

    private final LoanRepository loanRepository;
    private final LoanValidator loanValidator;
    private final LoanService loanService;

    @InitBinder("loanRequest")
    protected void initBinder(final WebDataBinder binder) {
        binder.setValidator(loanValidator);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<HttpStatus> createLoan(@Validated @RequestBody LoanRequest loanRequest, HttpServletRequest request) throws Exception {
        loanRequest.setRemoteAddress(request.getRemoteAddr());
        loanService.storeRequest(loanRequest);
        loanService.createLoan(loanRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<LoanResponse> list() {
        List<Loan> loanList = loanRepository.findAll();
        List<LoanResponse> responseList = new ArrayList<>();

        for (Loan loan : loanList) {
            builder().id(loan.getId());
             builder().created(loan.getCreated());
             builder().updated(loan.getUpdated());
             builder().amount(loan.getAmount());
             builder().interest(loan.getInterest());
             builder().termInDays(loan.getTermInDays());
             builder().loanExtensions(loan.getLoanExtensions());

            LoanResponse loanResponse = builder().build();
            responseList.add(loanResponse);
        }

        return responseList;
    }

    @RequestMapping(value = "/list-by-personal-id", method = RequestMethod.GET)
    public List<Loan> getLoanListByCustomerPersonalId(@RequestParam(value = "personalId") String personalId) {
        return loanService.listLoansByCustomerPersonalId(personalId);
    }

    @RequestMapping(value = "/extendLoan", method = RequestMethod.POST)
    public ResponseEntity<HttpStatus> extendLoanByLoanId(@RequestParam(value = "loanId") Long loanId,
                                                         @RequestParam(value = "extensionTermInDays") int extensionTermInDays) {
        loanService.extendLoanById(loanId, extensionTermInDays);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionJSONInfo handleValidationError(HttpServletRequest request, ConstraintViolationException exception) {
        List<String> errorMessages = new ArrayList<>();

//        for (ConstraintViolation constraintViolation : exception.getConstraintViolations()) {
//            errorMessages.add(constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage());
//        }

        ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo();
        exceptionJSONInfo.setMessages(errorMessages);
        exceptionJSONInfo.setUrl(request.getRequestURL().toString());

        return exceptionJSONInfo;
    }

    @ExceptionHandler(FailedRiskAnalysis.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionJSONInfo handleFailedRiskAnalysis(HttpServletRequest request, FailedRiskAnalysis exception) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(exception.getMessage());

        ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo();
        exceptionJSONInfo.setMessages(errorMessages);
        exceptionJSONInfo.setUrl(request.getRequestURL().toString());
        return exceptionJSONInfo;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionJSONInfo handleError(HttpServletRequest req, MethodArgumentNotValidException exception) {
        ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo();
        List<ObjectError> errorList = exception.getBindingResult().getAllErrors();

        for (ObjectError error : errorList) {
            exceptionJSONInfo.addMessage(req.getRequestURL().toString(), error.getCode());
        }

        return exceptionJSONInfo;
    }

}
