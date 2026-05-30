package ru.netology.cardtranferback.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.cardtranferback.model.Amount;
import ru.netology.cardtranferback.model.Card;
import ru.netology.cardtranferback.model.DTO.TransferRequest;
import ru.netology.cardtranferback.model.PendingOperation;
import ru.netology.cardtranferback.repository.CardRepository;
import ru.netology.cardtranferback.repository.PendingOperationStore;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private PendingOperationStore pendingOperationStore;

    @InjectMocks
    private TransferService transferService;


    private TransferRequest createValidRequest() {
        TransferRequest req = new TransferRequest();
        req.setCardFromNumber("5469123456781234");
        req.setCardFromCVV("123");
        req.setCardToNumber("5469876543215678");

        Amount amount = new Amount();
        amount.setValue(100.0);
        amount.setCurrency("RUB");
        req.setAmount(amount);
        return req;
    }

    private Card createFromCard() {
        Card card = new Card();
        card.setNumber("5469123456781234");
        card.setCvc("123");
        card.setBalance(100_000L); // 1000 ₽
        return card;
    }

    private Card createToCard() {
        Card card = new Card();
        card.setNumber("5469876543215678");
        card.setBalance(50_000L);
        return card;
    }

    @Test
    void initTransfer_shouldReturnId_whenValid() {
        when(cardRepository.findByNumber("5469123456781234")).thenReturn(Optional.of(createFromCard()));
        when(cardRepository.findByNumber("5469876543215678")).thenReturn(Optional.of(createToCard()));
        when(pendingOperationStore.save(any())).thenReturn("test-op-id");

        String id = transferService.initTransfer(createValidRequest());

        assertEquals("test-op-id", id);
    }

    @Test
    void initTransfer_shouldThrow_whenFromCardNotFound() {
        when(cardRepository.findByNumber(anyString())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> transferService.initTransfer(createValidRequest()));
    }

    @Test
    void initTransfer_shouldThrow_whenInvalidCvc() {
        Card card = createFromCard();
        card.setCvc("999");
        when(cardRepository.findByNumber("5469123456781234")).thenReturn(Optional.of(card));
        assertThrows(IllegalArgumentException.class, () -> transferService.initTransfer(createValidRequest()));
    }

    @Test
    void confirmTransfer_shouldSuccess_whenValidCodeAndBalance() {
        Card from = createFromCard();
        Card to = createToCard();
        PendingOperation op = new PendingOperation(from, to, 10_000L, "0000");
        when(pendingOperationStore.consume("op-1")).thenReturn(op);

        assertDoesNotThrow(() -> transferService.confirmTransfer("op-1", "0000"));

    }

    @Test
    void confirmTransfer_shouldThrow_whenInvalidCode() {
        PendingOperation op = new PendingOperation(createFromCard(), createToCard(), 10_000L, "123456");
        when(pendingOperationStore.consume("op-1")).thenReturn(op);
        assertThrows(IllegalArgumentException.class, () -> transferService.confirmTransfer("op-1", "000000"));
    }

    @Test
    void confirmTransfer_shouldThrow_whenInsufficientFunds() {
        Card poor = createFromCard();
        poor.setBalance(5_000L); // Всего 50 ₽
        PendingOperation op = new PendingOperation(poor, createToCard(), 10_000L, "123456");
        when(pendingOperationStore.consume("op-1")).thenReturn(op);
        assertThrows(IllegalArgumentException.class, () -> transferService.confirmTransfer("op-1", "123456"));
    }
}
