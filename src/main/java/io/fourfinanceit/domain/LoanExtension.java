package io.fourfinanceit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class LoanExtension {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private int extensionTermInDays;

    @NotNull
    private BigDecimal additionalInterest;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Loan loan;

    public LoanExtension() {
    }

    public LoanExtension(int extensionTermInDays, BigDecimal additionalInterest) {
        this.extensionTermInDays = extensionTermInDays;
        this.additionalInterest = additionalInterest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getExtensionTermInDays() {
        return extensionTermInDays;
    }

    public void setExtensionTermInDays(int extensionTermInDays) {
        this.extensionTermInDays = extensionTermInDays;
    }

    public BigDecimal getAdditionalInterest() {
        return additionalInterest;
    }

    public void setAdditionalInterest(BigDecimal additionalInterest) {
        this.additionalInterest = additionalInterest;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoanExtension that = (LoanExtension) o;

        if (extensionTermInDays != that.extensionTermInDays) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (additionalInterest != null ? !additionalInterest.equals(that.additionalInterest) : that.additionalInterest != null)
            return false;
        return loan != null ? loan.equals(that.loan) : that.loan == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + extensionTermInDays;
        result = 31 * result + (additionalInterest != null ? additionalInterest.hashCode() : 0);
        result = 31 * result + (loan != null ? loan.hashCode() : 0);
        return result;
    }
}
