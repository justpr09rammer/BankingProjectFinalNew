package com.example.bankingprojectfinal.Model.Entity;

import com.example.bankingprojectfinal.Model.Enums.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Builder
@Table(name = "card")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardEntity {
    @Id
    private String cardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    private LocalDate issueDate;
    private LocalDate expireDate;

    @Enumerated(EnumType.STRING)
    private CardStatus status;
}