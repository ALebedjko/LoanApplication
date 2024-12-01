package io.fourfinanceit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"id", "loanExtensions"})
public class Loan extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "loan_sequence")
    @SequenceGenerator(name = "loan_sequence", sequenceName = "loan_seq", allocationSize = 1)
    private Long id;

    private BigDecimal amount;
    private BigDecimal interest;
    private Integer termInDays;

    @ManyToOne(cascade = ALL, fetch = EAGER)
    @JsonIgnore
    private Client client;

    @OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LoanExtension> loanExtensions = new ArrayList<>();

    // Main constructor for required fields
    public Loan(Client client, BigDecimal amount, BigDecimal interest, int termInDays) {
        this.client = client;
        this.amount = amount;
        this.interest = interest;
        this.termInDays = termInDays;
    }

    public void addLoanExtension(LoanExtension loanExtension) {
        loanExtension.setLoan(this);
        this.loanExtensions.add(loanExtension);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", amount=" + amount +
                ", interest=" + interest +
                ", termInDays=" + termInDays +
                ", clientId=" + (client != null ? client.getId() : "null") +
                ", loanExtensionCount=" + (loanExtensions != null ? loanExtensions.size() : 0) +
                '}';
    }
}