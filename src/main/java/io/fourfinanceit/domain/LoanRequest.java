package io.fourfinanceit.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class LoanRequest extends AuditableEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String remoteAddress;
    private BigDecimal amount;
    private Integer termInDays;
    private String personalId;
    private String name;
    private String surname;

    public LoanRequest() {
    }

    public LoanRequest(BigDecimal amount, Integer termInDays, String personalId, String name, String surname) {
        this.amount = amount;
        this.termInDays = termInDays;
        this.personalId = personalId;
        this.name = name;
        this.surname = surname;
    }

    @Override
    public String toString() {
        return "LoanRequest{" +
                " amount = \"" + amount + "\"" +
                ", termInDays = \"" + termInDays + "\"" +
                ", personalId = \"" + personalId + "\"" +
                ", name = \"" + name + "\"" +
                ", surname = \"" + surname + "\"" +
                " }";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTermInDays() {
        return termInDays;
    }

    public void setTermInDays(Integer termInDays) {
        this.termInDays = termInDays;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }
}
