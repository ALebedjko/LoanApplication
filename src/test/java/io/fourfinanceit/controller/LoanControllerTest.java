package io.fourfinanceit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import io.fourfinanceit.domain.Client;
import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.domain.LoanExtension;
import io.fourfinanceit.domain.LoanRequest;
import io.fourfinanceit.repository.ClientRepository;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.repository.LoanRequestRepository;
import io.fourfinanceit.service.LoanService;
import io.fourfinanceit.utils.DateUtils;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoanControllerTest extends AbstractControllerTest {

    private static final String LOANS_URL = "/loans/";

    private static final TypeReference<List<Loan>> LOAN_LIST_TYPE = new TypeReference<>() {
    };

    private final Logger log = LoggerFactory.getLogger(LoanControllerTest.class);

    @Value("${MAX_LOAN_AMOUNT}")
    BigDecimal MAX_LOAN_AMOUNT;

    @Autowired
    LoanRequestRepository loanRequestRepository;

    @Autowired
    LoanService loanService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    LoanRepository loanRepository;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @AfterEach
    public void tearDown() {
        DateUtils.reset();
        loanRequestRepository.deleteAll();
    }

    @Test
    public void loanShouldBeSuccessfullyExtended() throws Exception {
        String uri = LOANS_URL + "extendLoan";
        Optional<Loan> optionalExpectedExtendedLoan = loanRepository.findById(1L);
        Loan expectedExtendedLoan = optionalExpectedExtendedLoan.get();

        LoanExtension loanExtension = new LoanExtension();
        loanExtension.setId(1L);
        loanExtension.setExtensionTermInDays(14);
        loanExtension.setAdditionalInterest(new BigDecimal("30.30"));
        expectedExtendedLoan.setInterest(new BigDecimal("45.80"));
        expectedExtendedLoan.addLoanExtension(loanExtension);

        MvcResult result = mvc
                .perform(post(uri).param("loanId", "1").param("extensionTermInDays", "14"))
                .andReturn();

        int mvcResultResponseStatus = result.getResponse().getStatus();
        HttpStatus httpResponseStatus = HttpStatus.valueOf(mvcResultResponseStatus);

        Optional<Loan> optionalActualExtendedLoan = loanRepository.findById(1L);
        Loan actualExtendedLoan = optionalActualExtendedLoan.get();

        assertLoansWithoutIdEqual(expectedExtendedLoan, actualExtendedLoan);
        assertEquals("failure - expected status " + OK, OK, httpResponseStatus);
    }

    @Test
    public void loanShouldBeSuccessfullyCreated() throws Exception {
        Client client = new Client("John", "Smith", "aaa-xxx0");
        client = clientRepository.save(client);

        Loan expectedLoan = new Loan(client, new BigDecimal("50.00"), new BigDecimal("150.00"), 14);

        MvcResult result = mvc.perform(post(LOANS_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectToJson(new LoanRequest(new BigDecimal("50.00"), 14, "aaa-xxx0", "John", "Smith"))))
                .andReturn();

        int mvcResultResponseStatus = result.getResponse().getStatus();
        HttpStatus httpResponseStatus = HttpStatus.valueOf(mvcResultResponseStatus);

        Loan actualLoan = loanService.listLoansByCustomerPersonalId("aaa-xxx0").get(0);

        assertLoansWithoutIdEqual(expectedLoan, actualLoan);
        assertEquals("failure - expected status " + OK, OK, httpResponseStatus);
    }

    @Test
    @Disabled
    public void listOfLoanShouldBeRetrievedSuccessfullyByPersonalId() throws Exception {
        Client existingClient = clientRepository.findOneByPersonalId("abc-xyz1");

        Loan firstExpectedLoan = new Loan(existingClient, new BigDecimal("20.30"), new BigDecimal("20.50"), 10);
        firstExpectedLoan.setId(2L);

        Loan secondExpectedLoan = new Loan(existingClient, new BigDecimal("100.50"), new BigDecimal("50.00"), 30);
        secondExpectedLoan.setId(3L);

        List<Loan> expectedLoans = new ArrayList<>();
        expectedLoans.add(firstExpectedLoan);
        expectedLoans.add(secondExpectedLoan);

        String personalId = "abc-xyz1";
        String uri = "/loans/personal-id/" + personalId;

        MvcResult result = mvc.perform(get(uri)).andReturn();

        String content = result.getResponse().getContentAsString();
        List<Loan> loanList = super.mapFromJson(content, LOAN_LIST_TYPE);

        int status = result.getResponse().getStatus();
        assertEquals("status should be 200", OK.value(), status);

        for (int i = 0; i < expectedLoans.size(); i++) {
            assertLoansWithoutIdEqual(expectedLoans.get(i), loanList.get(i));
        }
    }

    private void assertLoansWithoutIdEqual(Loan expected, Loan actual) {
        assertEquals("Amount should match", expected.getAmount(), actual.getAmount());
        assertEquals("Interest should match", expected.getInterest(), actual.getInterest());
        assertEquals("Term in days should match", expected.getTermInDays(), actual.getTermInDays());
        assertEquals("Client ID should match", expected.getClient().getId(), actual.getClient().getId());
    }
}