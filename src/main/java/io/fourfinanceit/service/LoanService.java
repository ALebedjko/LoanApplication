package io.fourfinanceit.service;

import io.fourfinanceit.config.LoanConfig;
import io.fourfinanceit.domain.Customer;
import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.domain.LoanExtension;
import io.fourfinanceit.domain.LoanRequest;
import io.fourfinanceit.repository.CustomerRepository;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.repository.LoanRequestRepository;
import io.fourfinanceit.riskanalysis.RiskAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_EVEN;

@Service
public class LoanService {

    private static final int DAYS_PER_WEEK = 7;
    private final Logger LOG = LoggerFactory.getLogger(LoanService.class);


    private final LoanConfig loanConfig;
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final List<RiskAnalysis> riskAnalyses;
    private final LoanRequestRepository loanRequestRepository;

    @Autowired
    public LoanService(LoanConfig loanConfig, LoanRepository loanRepository, CustomerRepository customerRepository, List<RiskAnalysis> riskAnalysis, LoanRequestRepository loanRequestRepository) {
        this.loanConfig = loanConfig;
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.riskAnalyses = riskAnalysis;
        this.loanRequestRepository = loanRequestRepository;
    }

    @Transactional
    public Loan createLoan(LoanRequest loanRequest) {
        String personalId = loanRequest.getPersonalId();
        Customer customer = customerRepository.findOneByPersonalId(personalId);

        riskAnalyses.forEach(riskAnalysis -> riskAnalysis.analyse(loanRequest));

        if (customer == null) {
            customer = new Customer(loanRequest.getName(), loanRequest.getSurname(), loanRequest.getPersonalId());
            LOG.debug("LoanService -> created new Customer:\n" + customer);
        }

        Loan loan = new Loan();
        loan.setAmount(loanRequest.getAmount());
        loan.setInterest(interest(loanRequest.getAmount(), loanRequest.getTermInDays()));
        loan.setCustomer(customer);
        loan.setTermInDays(loanRequest.getTermInDays());
        customer.addLoan(loan);

        LOG.debug("LoanService -> Loan to save:\n" + loan + "\n");
        LOG.debug("LoanService -> For Customer:\n\n" + customer + "\n");
        loanRepository.save(loan);
        return loan;
    }

    @Transactional
    public void storeRequest(LoanRequest loanRequest) {
        loanRequestRepository.save(loanRequest);
    }

    private BigDecimal interest(BigDecimal amount, int termDays) {
        float interestFactorPerDay = loanConfig.getInterestFactorPerWeek() / DAYS_PER_WEEK;
        return amount.multiply(new BigDecimal(interestFactorPerDay)).multiply(new BigDecimal(termDays)).setScale(2, HALF_EVEN);
    }

    @Transactional
    public List<Loan> listLoans() {
        return loanRepository.findAll();
    }

    @Transactional
    public List<Loan> listLoansByCustomerPersonalId(String personalId) {
        Customer customer = customerRepository.findOneByPersonalId(personalId);
        return loanRepository.findAllByCustomer(customer);
    }

    @Transactional
    public void extendLoanById(Long loanId, int extensionTermInDays) {
        Loan loan = loanRepository.findOne(loanId);
        BigDecimal additionalInterest = interest(loan.getAmount(), extensionTermInDays);
        loan.setInterest(loan.getInterest().add(additionalInterest));

        LoanExtension loanExtension = new LoanExtension();
        loanExtension.setExtensionTermInDays(extensionTermInDays);
        loanExtension.setAdditionalInterest(additionalInterest);
        loan.addLoanExtension(loanExtension);

        loanRepository.save(loan);
    }
}
