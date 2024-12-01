package io.fourfinanceit.service;

import io.fourfinanceit.config.LoanConfig;
import io.fourfinanceit.domain.Client;
import io.fourfinanceit.domain.Loan;
import io.fourfinanceit.domain.LoanExtension;
import io.fourfinanceit.domain.LoanRequest;
import io.fourfinanceit.repository.CustomerRepository;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.repository.LoanRequestRepository;
import io.fourfinanceit.riskanalysis.RiskAnalysis;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.math.RoundingMode.HALF_EVEN;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final int DAYS_PER_WEEK = 7;
    private final Logger LOG = LoggerFactory.getLogger(LoanService.class);

    private final LoanConfig loanConfig;
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final List<RiskAnalysis> riskAnalyses;
    private final LoanRequestRepository loanRequestRepository;

    @Transactional
    public Loan createLoan(LoanRequest loanRequest) {
        String personalId = loanRequest.getPersonalId();
        Client client = customerRepository.findOneByPersonalId(personalId);

        riskAnalyses.forEach(riskAnalysis -> riskAnalysis.analyse(loanRequest));

        if (client == null) {
            client = new Client(loanRequest.getName(), loanRequest.getSurname(), loanRequest.getPersonalId());
            LOG.debug("LoanService -> created new Customer:\n" + client);
        }

        Loan loan = new Loan();
        loan.setAmount(loanRequest.getAmount());
        loan.setInterest(interest(loanRequest.getAmount(), loanRequest.getTermInDays()));
        loan.setClient(client);
        loan.setTermInDays(loanRequest.getTermInDays());
        client.addLoan(loan);

        LOG.debug("LoanService -> Loan to save:\n" + loan + "\n");
        LOG.debug("LoanService -> For Customer:\n\n" + client + "\n");
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
        Client client = customerRepository.findOneByPersonalId(personalId);
        return loanRepository.findAllByClientId(client.getId());
    }

    @Transactional
    public void extendLoanById(Long loanId, int extensionTermInDays) {
        Optional<Loan> loan = loanRepository.findById(loanId);

        loan.ifPresent( presentLoan -> {
            BigDecimal additionalInterest = interest(presentLoan.getAmount(), extensionTermInDays);
            presentLoan.setInterest(presentLoan.getInterest().add(additionalInterest));

            LoanExtension loanExtension = new LoanExtension();
            loanExtension.setExtensionTermInDays(extensionTermInDays);
            loanExtension.setAdditionalInterest(additionalInterest);
            presentLoan.addLoanExtension(loanExtension);

            loanRepository.save(presentLoan);
        });

    }
}
