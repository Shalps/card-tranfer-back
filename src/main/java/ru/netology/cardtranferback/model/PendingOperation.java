package ru.netology.cardtranferback.model;

import lombok.Getter;

@Getter
public class PendingOperation {

    private final Card fromCard;
    private final Card toCard;
    private final long amountKopecks;
    private final String expectedCode;
    private final long createdAt;

    public PendingOperation(Card fromCard, Card toCard, long amountKopecks, String expectedCode) {
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amountKopecks = amountKopecks;
        this.expectedCode = expectedCode;
        this.createdAt = System.currentTimeMillis();
    }
}
