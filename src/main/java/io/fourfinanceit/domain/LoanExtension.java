package io.fourfinanceit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static jakarta.persistence.CascadeType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class LoanExtension {

    @Id
    @GeneratedValue
    private Long id;
    private int extensionTermInDays;
    private BigDecimal additionalInterest;

    @ManyToOne(cascade = ALL)
    @JsonIgnore
    private Loan loan;

}
