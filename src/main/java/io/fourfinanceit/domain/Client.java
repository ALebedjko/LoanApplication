package io.fourfinanceit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Client extends AuditableEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String surname;

    private String personalId;

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Loan> loans = new ArrayList<>();

    public Client() {
    }

    public Client(String name, String surname, String personalId) {
        this.name = name;
        this.surname = surname;
        this.personalId = personalId;
    }

    public void addLoan(Loan loan) {
        if (this.loans == null) {
            this.loans = new ArrayList<>();
        }
        loan.setClient(this);
        this.loans.add(loan);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", personalId='" + personalId + '\'' +
                ", loans=" + loans.stream().map(Loan::getId).toList() +
                '}';
    }
}