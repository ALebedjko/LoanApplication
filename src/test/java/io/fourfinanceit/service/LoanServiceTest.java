package io.fourfinanceit.service;

import io.fourfinanceit.config.LoanConfig;
import io.fourfinanceit.domain.Customer;
import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.domain.LoanRequest;
import io.fourfinanceit.repository.CustomerRepository;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.repository.LoanRequestRepository;
import io.fourfinanceit.riskanalysis.RiskAnalysis;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoanServiceTest {
    private static final Float INTEREST_RATE_FACTOR_PER_WEEK = 1.5f;
    private static final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private static final LoanRepository loanRepository = mock(LoanRepository.class);
    private static final List<RiskAnalysis> riskAnalyses = mock(List.class);
    private static final LoanConfig loanConfig = mock(LoanConfig.class);
    private static final LoanService loanService = new LoanService(loanConfig, loanRepository, customerRepository, riskAnalyses, mock(LoanRequestRepository.class));

    private final Logger LOG = LoggerFactory.getLogger(LoanService.class);

    @BeforeClass
    public static void setup() {

        when(customerRepository.findOneByPersonalId("abc-xyz0")).thenReturn(getFirstExpectedCustomer());
        when(customerRepository.findOneByPersonalId("abc-xyz1")).thenReturn(getSecondExpectedCustomer());
        when(customerRepository.findOneByPersonalId("abc-xyz2")).thenReturn(getThirdExpectedCustomer());
        when(loanRepository.findAll()).thenReturn(getListOfExpectedLoans());
        when(loanConfig.getInterestFactorPerWeek()).thenReturn(INTEREST_RATE_FACTOR_PER_WEEK);
    }

    private static Customer getFirstExpectedCustomer() {
        return new Customer("Vanja", "Petrov",
                "abc-xyz0");
    }

    private static Customer getSecondExpectedCustomer() {
        return new Customer("Fjodor", "Sumkin",
                "abc-xyz1");
    }

    private static Customer getThirdExpectedCustomer() {
        return new Customer("Sergej", "Salo",
                "abc-xyz2");
    }

    private static List<Loan> getListOfExpectedLoans() {
        List<Loan> expectedLoansList = new ArrayList<>();

        Loan loan0 = new Loan(getFirstExpectedCustomer(), new BigDecimal("100.00"), 30);
        expectedLoansList.add(loan0);

        Loan loan1 = new Loan(getSecondExpectedCustomer(), new BigDecimal("200.00"), 60);
        expectedLoansList.add(loan1);

        Loan loan2 = new Loan();
        loan2.setTermInDays(90);
        loan2.setAmount(new BigDecimal("800.00"));
        loan2.setCustomer(getSecondExpectedCustomer());
        expectedLoansList.add(loan2);

        Loan loan3 = new Loan();
        loan3.setTermInDays(50);
        loan3.setAmount(new BigDecimal("200.00"));
        loan3.setCustomer(getSecondExpectedCustomer());
        expectedLoansList.add(loan3);

        return expectedLoansList;
    }


    @Test
    public void loanShouldBeCreatedSuccessfully() throws Exception {
        Loan expectedLoan = new Loan();
        expectedLoan.setAmount(new BigDecimal(100));
        expectedLoan.setInterest(new BigDecimal("642.86"));
        expectedLoan.setTermInDays(30);

        Customer expectedCustomer = new Customer("Vanja", "Petrov",
                "abc-xyz0");
        expectedCustomer.addLoan(expectedLoan);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setAmount(new BigDecimal(100));
        loanRequest.setPersonalId("abc-xyz0");
        loanRequest.setTermInDays(30);
        loanRequest.setName("Vanja");
        loanRequest.setSurname("Ivanov");

        Loan actualLoan = loanService.createLoan(loanRequest);
        assertEquals(expectedLoan, actualLoan);
    }

    @Test
    public void listOfLoansShouldBeRetrievedSuccessfully() throws Exception {
        List<Loan> actualList = loanService.listLoans();
        assertNotNull("failure - expected not null", actualList);
        assertEquals(getListOfExpectedLoans(), actualList);
    }

    @Test
    public void listOfLoanShouldBeSuccessfullyRetrievedByPersonalId() throws Exception {
        String personalId = "abc-xyz1";
        List<Loan> actualLoans = loanService.listLoansByCustomerPersonalId(personalId);

        List<Loan> expectedLoans = getSecondExpectedCustomer().getLoans();
        assertEquals("failure - expected list size", expectedLoans.size(), actualLoans.size());
        assertEquals("failure - expected loan list equality", expectedLoans, actualLoans);
        assertNotNull("failure - expected not null", actualLoans);
    }


}