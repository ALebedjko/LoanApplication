package io.fourfinanceit.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Client extends AuditableEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
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

}
