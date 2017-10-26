package io.fourfinanceit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Loan extends AuditableEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal interest;

    @NotNull
    private Integer termInDays;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Customer customer;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LoanExtension> loanExtensions;

    public Loan() {
    }

    public Loan(Customer customer, BigDecimal amount, Integer termInDays) {
        this.amount = amount;
        this.termInDays = termInDays;
        this.customer = customer;
    }

    public Loan(BigDecimal amount, BigDecimal interest, Integer termInDays) {
        this.amount = amount;
        this.interest = interest;
        this.termInDays = termInDays;
    }

    public Loan(BigDecimal amount, Integer termInDays) {
        this.amount = amount;
        this.termInDays = termInDays;
    }

    public void addLoanExtension(LoanExtension loanExtension) {
        if (this.loanExtensions == null) {
            this.loanExtensions = new ArrayList<>();
        }
        loanExtension.setLoan(this);
        this.loanExtensions.add(loanExtension);
    }

    public List<LoanExtension> getLoanExtensions() {
        return loanExtensions;
    }

    public void setLoanExtensions(List<LoanExtension> loanExtensions) {
        this.loanExtensions = loanExtensions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public Integer getTermInDays() {
        return termInDays;
    }

    public void setTermInDays(Integer termInDays) {
        this.termInDays = termInDays;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", amount=" + amount +
                ", interest=" + interest +
                ", termInDays=" + termInDays +
                ", loanExtensions=" + loanExtensions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Loan loan = (Loan) o;

        if (id != null ? !id.equals(loan.id) : loan.id != null) return false;
        if (amount != null ? !amount.equals(loan.amount) : loan.amount != null) return false;
        if (interest != null ? !interest.equals(loan.interest) : loan.interest != null) return false;
        return termInDays != null ? termInDays.equals(loan.termInDays) : loan.termInDays == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (interest != null ? interest.hashCode() : 0);
        result = 31 * result + (termInDays != null ? termInDays.hashCode() : 0);
        return result;
    }
}
