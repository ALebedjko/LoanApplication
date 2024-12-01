package io.fourfinanceit.service;

import io.fourfinanceit.config.LoanConfig;
import io.fourfinanceit.domain.Client;
import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.domain.LoanRequest;
import io.fourfinanceit.repository.ClientRepository;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.repository.LoanRequestRepository;
import io.fourfinanceit.riskanalysis.RiskAnalysis;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoanServiceTest {

    private static final Float INTEREST_RATE_FACTOR_PER_WEEK = 1.5f;
    private static final ClientRepository CLIENT_REPOSITORY = mock(ClientRepository.class);
    private static final LoanRepository loanRepository = mock(LoanRepository.class);
    private static final List<RiskAnalysis> riskAnalyses = mock(List.class);
    private static final LoanConfig loanConfig = mock(LoanConfig.class);

    private static final LoanService loanService = new LoanService(loanConfig, loanRepository, CLIENT_REPOSITORY, riskAnalyses, mock(LoanRequestRepository.class));

    private final Logger LOG = LoggerFactory.getLogger(LoanServiceTest.class);

    @BeforeAll
    public static void setup() {
        when(CLIENT_REPOSITORY.findOneByPersonalId("abc-xyz0")).thenReturn(getFirstExpectedCustomer());
        when(CLIENT_REPOSITORY.findOneByPersonalId("abc-xyz1")).thenReturn(getSecondExpectedCustomer());
        when(CLIENT_REPOSITORY.findOneByPersonalId("abc-xyz2")).thenReturn(getThirdExpectedCustomer());
        when(loanRepository.findAll()).thenReturn(getListOfExpectedLoans());
        when(loanConfig.getInterestFactorPerWeek()).thenReturn(INTEREST_RATE_FACTOR_PER_WEEK);
    }

    private static Client getFirstExpectedCustomer() {
        return new Client("Vanja", "Petrov", "abc-xyz0");
    }

    private static Client getSecondExpectedCustomer() {
        return new Client("Fjodor", "Sumkin", "abc-xyz1");
    }

    private static Client getThirdExpectedCustomer() {
        return new Client("Sergej", "Salo", "abc-xyz2");
    }

    private static List<Loan> getListOfExpectedLoans() {
        List<Loan> expectedLoansList = new ArrayList<>();

        Client firstClient = getFirstExpectedCustomer();
        Loan loan0 = new Loan(firstClient, new BigDecimal("100.00"), new BigDecimal("5.00"), 30);
        expectedLoansList.add(loan0);

        Client secondClient = getSecondExpectedCustomer();
        Loan loan1 = new Loan(secondClient, new BigDecimal("200.00"), new BigDecimal("10.00"), 60);
        expectedLoansList.add(loan1);

        Loan loan2 = new Loan(secondClient, new BigDecimal("800.00"), new BigDecimal("20.00"), 90);
        expectedLoansList.add(loan2);

        Loan loan3 = new Loan(secondClient, new BigDecimal("200.00"), new BigDecimal("15.00"), 50);
        expectedLoansList.add(loan3);

        return expectedLoansList;
    }

    @Test
    public void loanShouldBeCreatedSuccessfully() throws Exception {
        BigDecimal expectedAmount = new BigDecimal(100);
        BigDecimal expectedInterest = new BigDecimal("642.86");
        int expectedTermInDays = 30;

        Loan expectedLoan = new Loan();
        expectedLoan.setAmount(expectedAmount);
        expectedLoan.setInterest(expectedInterest);
        expectedLoan.setTermInDays(expectedTermInDays);

        Client expectedClient = new Client("Vanja", "Petrov", "abc-xyz0");
        expectedClient.addLoan(expectedLoan);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setAmount(expectedAmount);
        loanRequest.setPersonalId("abc-xyz0");
        loanRequest.setTermInDays(expectedTermInDays);
        loanRequest.setName("Vanja");
        loanRequest.setSurname("Ivanov");

        Loan actualLoan = loanService.createLoan(loanRequest);

        assertEquals(expectedAmount, actualLoan.getAmount());
        assertEquals(expectedInterest, actualLoan.getInterest());
        assertEquals(expectedTermInDays, actualLoan.getTermInDays());
        assertEquals("abc-xyz0", actualLoan.getClient().getPersonalId());
    }

    @Test
    public void listOfLoansShouldBeRetrievedSuccessfully() {
        List<Loan> actualList = loanService.listLoans();
        assertNotNull(actualList, "failure - expected not null");
        assertEquals(getListOfExpectedLoans(), actualList);
    }

    @Test
    public void listOfLoanShouldBeSuccessfullyRetrievedByPersonalId() {
        String personalId = "abc-xyz1";
        List<Loan> actualLoans = loanService.listLoansByCustomerPersonalId(personalId);

        List<Loan> expectedLoans = getSecondExpectedCustomer().getLoans();
        assertEquals(expectedLoans.size(), actualLoans.size(), "failure - expected list size");
        assertEquals(expectedLoans, actualLoans, "failure - expected loan list equality");
        assertNotNull(actualLoans, "failure - expected not null");
    }
}