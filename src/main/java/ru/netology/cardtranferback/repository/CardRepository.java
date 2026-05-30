package ru.netology.cardtranferback.repository;

import org.springframework.stereotype.Repository;
import ru.netology.cardtranferback.model.Card;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Repository
public class CardRepository {

    private final Map<String, Card> cards = new ConcurrentHashMap<>();

    private void addTestCard(String number, String validTill, String cvc, Long balance) {
        Card card = new Card();
        card.setNumber(number);
        card.setDate(validTill);
        card.setCvc(cvc);
        card.setBalance(balance);
        cards.put(number, card);
    }



    public Optional<Card> findByNumber(String number) {
        Card card = cards.get(number);
        return Optional.ofNullable(card);
    }
    public CardRepository() {
        addTestCard("5469123456781234", "12/26", "123", 100_000L);  // 1000 ₽
        addTestCard("5469876543215678", "11/26", "456", 50_000L);    // 500 ₽
        addTestCard("4276123456789012", "01/27", "789", 200_000L);   // 2000 ₽
    }


    public void save(Card card) {
        cards.put(card.getNumber(), card);
    }

}
