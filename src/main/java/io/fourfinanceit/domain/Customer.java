package io.fourfinanceit.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer extends AuditableEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    private String personalId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Loan> loans = new ArrayList<>();

    public Customer() {
    }

    public Customer(String name, String surname, String personalId) {
        this.name = name;
        this.surname = surname;
        this.personalId = personalId;
    }

    public void addLoan(Loan loan) {
        if (this.loans == null) {
            this.loans = new ArrayList<>();
        }
        loan.setCustomer(this);
        this.loans.add(loan);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", personalId='" + personalId + '\'' +
                ", loans=" + loans +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;
        if (!name.equals(customer.name)) return false;
        if (!surname.equals(customer.surname)) return false;
        if (!personalId.equals(customer.personalId)) return false;
        return loans != null ? loans.equals(customer.loans) : customer.loans == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + surname.hashCode();
        result = 31 * result + personalId.hashCode();
        result = 31 * result + (loans != null ? loans.hashCode() : 0);
        return result;
    }
}
