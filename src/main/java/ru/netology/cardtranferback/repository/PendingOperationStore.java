package ru.netology.cardtranferback.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.netology.cardtranferback.model.Card;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
@Component
@NoArgsConstructor
@Data
public class PendingOperation {
    private Card fromCard;
    private Card toCard;
    private long amountKopecks;
    private String expectedCode;
    private long createdAt;

    public PendingOperation(Card fromCard, Card toCard, long amountKopecks, String expectedCode) {
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amountKopecks = amountKopecks;
        this.expectedCode = expectedCode;
        this.createdAt = System.currentTimeMillis();
    }

    private final Map<String, PendingOperation> operations = new ConcurrentHashMap<>();

    public String save(PendingOperation operation) {
        String id = UUID.randomUUID().toString();
        operations.put(id, operation);
        return id;
    }
}
