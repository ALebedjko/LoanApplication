package io.fourfinanceit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.domain.LoanExtension;
import io.fourfinanceit.domain.LoanRequest;
import io.fourfinanceit.exception.ExceptionJSONInfo;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.repository.LoanRequestRepository;
import io.fourfinanceit.service.LoanService;
import io.fourfinanceit.utils.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.fourfinanceit.exception.ExceptionMessages.*;
import static org.junit.Assert.assertEquals;

public class LoanControllerTest extends AbstractControllerTest {
    private static final String LOAN_URL = "/loan/";
    private static final TypeReference LOAN_LIST_TYPE = new TypeReference<List<Loan>>() {
    };

    private static final TypeReference EXCEPTION_JSON_TYPE = new TypeReference<ExceptionJSONInfo>() {
    };
    private final Logger log = LoggerFactory.getLogger(LoanService.class);
    @Value("${MAX_LOAN_AMOUNT}")
    BigDecimal MAX_LOAN_AMOUNT;

    @Autowired
    LoanRequestRepository loanRequestRepository;

    @Autowired
    LoanService loanService;

    @Autowired
    LoanRepository loanRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        DateUtils.reset();
        loanRequestRepository.deleteAll();
    }

    @Test
    public void loanShouldBeSuccessfullyExtended() throws Exception {
        String uri = LOAN_URL + "extendLoan";
        Loan expectedExtendedLoan = loanRepository.findOne(1L);

        LoanExtension loanExtension = new LoanExtension();
        loanExtension.setId(1L);
        loanExtension.setExtensionTermInDays(14);
        loanExtension.setAdditionalInterest(new BigDecimal("30.30"));
        expectedExtendedLoan.setInterest(new BigDecimal("45.80"));
        expectedExtendedLoan.addLoanExtension(loanExtension);

        MvcResult result = mvc
                .perform(MockMvcRequestBuilders.post(uri).param("loanId", "1").param("extensionTermInDays", "14"))
                .andReturn();

        int mvcResultResponseStatus = result.getResponse().getStatus();
        HttpStatus httpResponseStatus = HttpStatus.valueOf(mvcResultResponseStatus);

        Loan actualExtendedLoan = loanRepository.findOne(1L);

        assertEquals("failure - expected status " + HttpStatus.OK, HttpStatus.OK, httpResponseStatus);
        assertEquals(expectedExtendedLoan, actualExtendedLoan);
        assertEquals(expectedExtendedLoan.getLoanExtensions().get(0), actualExtendedLoan.getLoanExtensions().get(0));
    }

    @Test
    public void loanShouldBeSuccessfullyCreated() throws Exception {

        Loan expectedLoan = new Loan(new BigDecimal("50.00"), new BigDecimal("150.00"), 14);
        expectedLoan.setId(7L);
        expectedLoan.setLoanExtensions(new ArrayList<>());

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post(LOAN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(new LoanRequest(new BigDecimal(50), 14, "aaa-xxx0", "John", "Smith"))))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        log.debug("content = " + content);

        int mvcResultResponseStatus = result.getResponse().getStatus();
        HttpStatus httpResponseStatus = HttpStatus.valueOf(mvcResultResponseStatus);

        Loan actualLoan = loanService.listLoansByCustomerPersonalId("aaa-xxx0").get(0);

        assertEquals(expectedLoan, actualLoan);
        assertEquals("failure - expected content " + HttpStatus.OK, HttpStatus.OK, httpResponseStatus);
    }


    @Test
    public void loanIsNotCreatedWhenOutsideWorkingHoursWithMaxAmount() throws Exception {
        String expectedExceptionMessage = DECLINED_DUE_RISK_ANALYSIS.getDescription();

        DateUtils.setNow(LocalDate.now().atStartOfDay());

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post(LOAN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(new LoanRequest(MAX_LOAN_AMOUNT, 1, "abc-xyz0", "John", "Smith"))))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionJSONInfo exceptionJSONInfo = super.mapFromJson(content, EXCEPTION_JSON_TYPE);
        String exceptionMessage = exceptionJSONInfo.getMessages().iterator().next();
        log.debug("exceptionMessage = " + exceptionMessage);

        HttpStatus httpResponseStatus = HttpStatus.valueOf(result.getResponse().getStatus());
        assertEquals("failure - expected status " + HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, httpResponseStatus);
        assertEquals("failure - expected exception message" + expectedExceptionMessage, expectedExceptionMessage, exceptionMessage);
    }

    @Test
    public void loanIsNotCreatedWhenMaxRequestAttemptsExceeded() throws Exception {
        String expectedExceptionMessage = DECLINED_DUE_RISK_ANALYSIS.getDescription();

        DateUtils.setNow(LocalDateTime.now());
        String uri = LOAN_URL;
        mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(new LoanRequest(new BigDecimal(10), 1, "abc-xyz0", "John", "Smith"))))
                .andReturn();
        mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(new LoanRequest(new BigDecimal(10), 1, "abc-xyz0", "John", "Smith"))))
                .andReturn();
        mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(new LoanRequest(new BigDecimal(10), 1, "abc-xyz0", "John", "Smith"))))
                .andReturn();

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(new LoanRequest(new BigDecimal(10), 1, "abc-xyz0", "John", "Smith"))))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionJSONInfo exceptionJSONInfo = super.mapFromJson(content, EXCEPTION_JSON_TYPE);
        String exceptionMessage = exceptionJSONInfo.getMessages().iterator().next();
        log.debug("exceptionMessage = " + exceptionMessage);

        HttpStatus httpResponseStatus = HttpStatus.valueOf(result.getResponse().getStatus());
        assertEquals("failure - expected status " + HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, httpResponseStatus);
        assertEquals("failure - expected exception message" + expectedExceptionMessage, expectedExceptionMessage, exceptionMessage);
    }

    @Test
    public void loanWithoutAmountIsNotCreated() throws Exception {
        String expectedExceptionMessage = AMOUNT_NOT_NULL_MSG.getDescription();

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post(LOAN_URL).contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(new LoanRequest(null, 1, "abc-xyz", "Vanja", "Ivanov"))))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionJSONInfo exceptionJSONInfo = super.mapFromJson(content, EXCEPTION_JSON_TYPE);
        String exceptionMessage = exceptionJSONInfo.getMessages().iterator().next();
        log.debug("exceptionMessage = " + exceptionMessage);

        HttpStatus httpResponseStatus = HttpStatus.valueOf(result.getResponse().getStatus());
        assertEquals("failure - expected status " + HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, httpResponseStatus);
        assertEquals("failure - expected exception message" + expectedExceptionMessage, expectedExceptionMessage, exceptionMessage);
    }

    @Test
    public void loanWithExceedingAmountIsNotCreated() throws Exception {
        String expectedExceptionMessage = "The attempt to take loan is made with amount, which is greater than max allowed amount. " +
                "Maximum loan amount is " + MAX_LOAN_AMOUNT;

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post(LOAN_URL).contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(new LoanRequest(new BigDecimal(301), 1, "abc-xyz", "Vanja", "Ivanov"))))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionJSONInfo exceptionJSONInfo = super.mapFromJson(content, EXCEPTION_JSON_TYPE);
        String exceptionMessage = exceptionJSONInfo.getMessages().iterator().next();
        log.debug("exceptionMessage = " + exceptionMessage);

        HttpStatus httpResponseStatus = HttpStatus.valueOf(result.getResponse().getStatus());
        assertEquals("failure - expected status " + HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, httpResponseStatus);
        assertEquals("failure - expected exception message" + expectedExceptionMessage, expectedExceptionMessage, exceptionMessage);
    }

    @Test
    public void loanWithOutMandatoryFieldsIsNotCreated() throws Exception {
        List<String> expectedExceptionMessages = new ArrayList<>();
        expectedExceptionMessages.add(AMOUNT_NOT_NULL_MSG.getDescription());
        expectedExceptionMessages.add(TERM_NOT_NULL_MSG.getDescription());
        expectedExceptionMessages.add(NAME_NOT_NULL_MSG.getDescription());
        expectedExceptionMessages.add(SURNAME_NOT_NULL_MSG.getDescription());
        expectedExceptionMessages.add(PERSONAL_ID_NOT_NULL_MSG.getDescription());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post(LOAN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(new LoanRequest(null, null, "", "", ""))))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionJSONInfo exceptionJSONInfo = super.mapFromJson(content, EXCEPTION_JSON_TYPE);
        List<String> exceptionMessages = exceptionJSONInfo.getMessages();
        log.debug("exceptionMessage = " + exceptionMessages);

        int status = result.getResponse().getStatus();
        assertEquals("failure - expected status 500", 500, status);
        assertEquals("failure - expected exception message" + expectedExceptionMessages, expectedExceptionMessages, exceptionMessages);
    }

    @Test
    public void listOfLoansShouldBeSuccessfullyRetrieved() throws Exception {
        String uri = "/loan/list/";

        List<Loan> expectedLoanList = loanRepository.findAll();

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<Loan> loanList = (super.mapFromJson(content, LOAN_LIST_TYPE));
        log.debug("loanList = " + loanList);

        int status = result.getResponse().getStatus();
        assertEquals("failure - expected status 200", 200, status);
        assertEquals("failure - Loan lists should be equal", expectedLoanList, loanList);
    }

    @Test
    public void listOfLoanShouldBeRetrievedSuccessfullyByPersonalId() throws Exception {
        Loan firstExpectedLoan = new Loan(new BigDecimal("20.30"), new BigDecimal("20.50"), 10);
        firstExpectedLoan.setId(2L);
        Loan secondExpectedLoan = new Loan(new BigDecimal("100.50"), new BigDecimal("50.00"), 30);
        secondExpectedLoan.setId(3L);

        List<Loan> expectedLoans = new ArrayList<>();
        expectedLoans.add(firstExpectedLoan);
        expectedLoans.add(secondExpectedLoan);

        String uri = "/loan/list-by-personal-id";
        MvcResult result = mvc
                .perform(MockMvcRequestBuilders.get(uri).param("personalId", "abc-xyz1"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<Loan> loanList = (super.mapFromJson(content, LOAN_LIST_TYPE));

        int status = result.getResponse().getStatus();
        assertEquals("failure - expected status 200", 200, status);
        assertEquals("failure - Loan lists should be equal", expectedLoans, loanList);

    }


}
