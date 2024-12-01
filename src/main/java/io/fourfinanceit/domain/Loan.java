package io.fourfinanceit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Loan extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_sequence")
    @SequenceGenerator(name = "loan_sequence", sequenceName = "loan_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal interest;

    @NotNull
    private Integer termInDays;

    @ManyToOne(cascade = ALL)
    @JsonIgnore
    private Client client;

    @OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LoanExtension> loanExtensions;



    public Loan(Client client, @NotNull BigDecimal amount, @NotNull Integer termInDays) {
        this.client = client;
        this.amount = amount;
        this.termInDays = termInDays;

    }

    public Loan() {
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

        if (!Objects.equals(id, loan.id)) return false;
        if (!Objects.equals(amount, loan.amount)) return false;
        if (!Objects.equals(interest, loan.interest)) return false;
        return Objects.equals(termInDays, loan.termInDays);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + amount.hashCode();
        result = 31 * result + interest.hashCode();
        result = 31 * result + termInDays.hashCode();
        return result;
    }
}
