package ru.netology.cardtranferback.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cardtranferback.model.Card;
import ru.netology.cardtranferback.model.DTO.TransferRequest;
import ru.netology.cardtranferback.model.PendingOperation;
import ru.netology.cardtranferback.repository.CardRepository;
import ru.netology.cardtranferback.repository.PendingOperationStore;

import java.beans.Transient;

@Slf4j
@Service
@AllArgsConstructor
public class TransferService {

    private final CardRepository cardRepository;
    private final PendingOperationStore pendingOperation;

    private static final String DEMO_CONFIRMATION_CODE = "0000";


    public String initTransfer(TransferRequest req) {
        log.info(" Попытка перевода: от {} к {}, сумма: {}",
                req.getCardFromNumber(),
                req.getCardToNumber(),
                req.getAmount().getValue());

        Card fromCard = cardRepository.findByNumber(req.getCardFromNumber())
                .orElseThrow(() -> {
                    log.error(" Карта отправителя не найдена: {}", req.getCardFromNumber());
                    return new IllegalArgumentException("Карта для списания не найдена");
                });


        if (!fromCard.getCvc().equals(req.getCardFromCVV())) {
            log.error("Неверный CVC-код для карты:{}", req.getCardFromNumber());
            throw new IllegalArgumentException("Неверный CVC-код");

        }
        Card toCard = cardRepository.findByNumber(req.getCardToNumber())
                .orElseThrow(() -> {
                    log.error("карта получателя {} не найдена", req.getCardToNumber());
                    return new IllegalArgumentException("Карта для зачисления не найдена");
                });

        long amountKopecks = req.getAmount().toKopecks();
        if (amountKopecks <= 0) {
            log.warn("Неверная сумма для перевода");
            throw new IllegalArgumentException("Сумма должна быть больше 0");
        }

        PendingOperation pending = new PendingOperation(
                fromCard, toCard, amountKopecks, DEMO_CONFIRMATION_CODE);

        String idOperation = pendingOperation.save(pending);
        log.info("Операция создана, ID: {}", idOperation);

        return idOperation;
    }

    @Transient
    public void confirmTransfer(String idOperation, String code) {
        PendingOperation pending = pendingOperation.consume(idOperation);
        log.info(" Подтверждение операции: ID={}, код={}", idOperation, code);

        if (pending == null) {
            log.error(" Операция с ID {} не найдена или уже удалена", idOperation);
            throw new IllegalArgumentException("Операция не найдена");
        }

        if (!DEMO_CONFIRMATION_CODE.equals(code)) {
            log.warn("️Неверный код подтверждения для операции {}", idOperation);
            throw new IllegalArgumentException("Неверный код подтверждения");
        }

        Card fromCard = pending.getFromCard();
        if (fromCard.getBalance()* 100 < pending.getAmountKopecks()) {
            log.error("Недостаточно средств на карте {}. Нужно: {}, есть: {}",
                    fromCard.getNumber(),
                    pending.getAmountKopecks(),
                    fromCard.getBalance());
            throw new IllegalArgumentException("Недостаточно средств");
        }

        Card toCard = pending.getToCard();


        fromCard.setBalance(fromCard.getBalance() - pending.getAmountKopecks());
        toCard.setBalance(toCard.getBalance() + pending.getAmountKopecks());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        log.info("ПЕРЕВОД ЗАВЕРШЕН УСПЕШНО! ID: {}, Сумма: {}, От: {}, Кому: {}",
                idOperation,
                pending.getAmountKopecks()/100,
                fromCard.getNumber(),
                toCard.getNumber());

    }


}
