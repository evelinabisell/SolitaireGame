import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public ArrayList<Card> getCards() {
        return cards;
    };

    public void fillDeck() {
        makeSuit(Suit.clubs);
        makeSuit(Suit.diamonds);
        makeSuit(Suit.hearts);
        makeSuit(Suit.spades);
    }

    public void makeSuit(Suit suit) {
        //Making Ace card of specified suit and adding to cards list
        cards.add(new Card(1, suit, new Image("PNG-cards-1.3/ace_of_"+suit+".png")));

        //Making number cards of specified suit and adding to cards list
        for (int i  = 2; i < 11; i++) {
            Card c = new Card(i, suit, new Image("PNG-cards-1.3/"+i+"_of_"+suit+".png"));
            cards.add(c);
        }
        //Making clothes cards (or what they're called in english) of specified suit and adding to cards list
        cards.add(new Card(11, suit, new Image("PNG-cards-1.3/jack_of_"+suit+"2.png")));
        cards.add(new Card(12, suit, new Image("PNG-cards-1.3/queen_of_"+suit+"2.png")));
        cards.add(new Card(13, suit, new Image("PNG-cards-1.3/king_of_"+suit+"2.png")));
    }

    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(cards.size()-1);
        }
        return null;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public void removeCard(int i) {
        cards.remove(i);
    }
}
