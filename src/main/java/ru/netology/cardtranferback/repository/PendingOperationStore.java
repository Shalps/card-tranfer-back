package ru.netology.cardtranferback.repository;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.netology.cardtranferback.model.Card;
import ru.netology.cardtranferback.model.PendingOperation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Data
@Component
public class PendingOperationStore {

    private final Map<String, PendingOperation> operations = new ConcurrentHashMap<>();


    public String save(PendingOperation operation) {
        String id = UUID.randomUUID().toString();
        operations.put(id, operation);
        return id;
    }

    public PendingOperation consume(String operationId) {
        return operations.remove(operationId);
    }
}
