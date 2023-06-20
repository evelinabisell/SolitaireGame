import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;

public class GUI extends Application {

    private final BorderPane root = new BorderPane();
    private final Pane center = new Pane();
    private Deck deck;

    private CardPile drawPile = new CardPile();
    private CardPile presentPile = new CardPile();
    private ArrayList<CardPile> piles = new ArrayList<CardPile>();
    private ArrayList<CardPile> finishPiles = new ArrayList<CardPile>();

    private Card firstCard;

    @Override
    public void start(Stage stage) {
        makeTop();
        makeCenter();

        play();

        Scene scene = new Scene(root, 1300, 850);
        stage.setScene(scene);
        stage.setTitle("Harpan");
        stage.setResizable(false);
        stage.show();
    }

    private void makeTop() {
        FlowPane top = new FlowPane();
        top.setAlignment(Pos.CENTER);
        top.setStyle("-fx-background-color: #c2f1ff");
        Label title = new Label("Harpan");
        title.setStyle("-fx-font-size: 52px;\n" +
                "-fx-font-family: \"Arial Black\";\n" +
                "-fx-font-weight: bold;\n" +
                "-fx-text-fill: #a084ce;\n");
        top.getChildren().addAll(title);
        root.setTop(top);
    }

    private void makeCenter() {
        root.setCenter(center);
        center.setStyle("-fx-background-color: #f3daee");

        drawPile.relocate(70,25);
        presentPile.relocate(240,25);

        for (int i = 0; i < 4; i++) {
            CardPile pile = new CardPile();
            pile.setOnMouseClicked(new FinishPileHandler());
            pile.relocate(580 + 170*i, 25);
            finishPiles.add(pile);
        }

        for (int i = 0; i < 7; i++) {
            CardPile pile = new CardPile();
            pile.relocate(70 + 170*i, 250);
            piles.add(pile);
        }

        center.getChildren().addAll(drawPile, presentPile);
        center.getChildren().addAll(finishPiles);
        center.getChildren().addAll(piles);
    }

    private void play() {
        deck = new Deck();
        deck.fillDeck();
        deck.shuffleDeck();

        placeCards(deck);
        makeDrawPile("new");
    }

    private void placeCards(Deck deck) {
        int j = 0;
        for (CardPile pile : piles) {
            double pileX = pile.getTranslateX();
            double pileY = pile.getTranslateY();

            for (int i = 0; i < j; i++) {
                Card card = deck.drawCard();
                card.relocate(pileX, pileY + 25*i);
                card.setCovered();
                pile.getChildren().add(card);
            }
            Card card = deck.drawCard();
            card.relocate(pileX, pileY + 25*j);
            card.setOpen(new SelectHandler());
            pile.getChildren().add(card);

            j++;
        }
    }

    private void makeDrawPile(String str) {
        // Take every card in the deck (if new) or present pile and remove it, flip it and add it to the draw pile
        if (str.equals("new")) {
            int deckSize = deck.getCards().size();
            for (int i = 0; i < deckSize; i++) {
                Card card = deck.drawCard();
                card.setCovered();
                drawPile.getChildren().add(card);
            }
        } else {
            int pileSize = presentPile.getChildren().size();
            for (int i = 0; i < pileSize; i++) {
                Card card = presentPile.drawCard();
                card.setCovered();
                drawPile.getChildren().add(card);
            }
        }
        // Set a DrawHandler on the top card of the drawPile
        drawPile.getChildren().get(drawPile.getChildren().size()-1).setOnMouseClicked(new DrawHandler());
    }

    class SelectHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Card card = (Card) event.getSource();

            if (firstCard == null) {
                firstCard = card;

                Bloom bloom = new Bloom();
                bloom.setThreshold(0.4);
                card.setEffect(bloom);
                return;
            }

            if (card.getNumber() == firstCard.getNumber() + 1 && !card.getColor().equals(firstCard.getColor()) && piles.contains(card.getParent())) {
                moveCard((CardPile) card.getParent());

            } else if (card.getNumber() == firstCard.getNumber() - 1 && card.getSuit().equals(firstCard.getSuit()) && finishPiles.contains(card.getParent())) {
                moveCardToFinishPile((CardPile) card.getParent());

            } else {
                deselectFirstCard();
            }
        }
    }

    class DrawHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Card card = (Card) event.getSource();

            drawPile.getChildren().remove(card);
            presentPile.getChildren().add(card);

            card.setOpen(new SelectHandler());

            if (!drawPile.getChildren().isEmpty()) {
                drawPile.getChildren().get(drawPile.getChildren().size()-1).setOnMouseClicked(new DrawHandler());
            } else {
                drawPile.setOnMouseClicked(new EmptyDrawPileHandler());
            }
        }
    }

    class EmptyDrawPileHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            // Only refill the empty draw pile if you double-click it
            // TODO add button for this too
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2){

                    makeDrawPile("nope");

                    // Remove handler from draw pile
                    drawPile.setOnMouseClicked(null);
                }
            }
        }
    }

    class FinishPileHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            CardPile pile = (CardPile) event.getSource();
            if (firstCard == null) return;

            // Only accepts an Ace as first card added in this pile
            if (firstCard.getNumber() == 1) {
                moveCardToFinishPile(pile);
                return;
            }
            // Deselects first card if it's not an Ace
            deselectFirstCard();
        }
    }

    class EmptyPileHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            CardPile pile = (CardPile) event.getSource();
            if (firstCard == null) return;

            // Only accepts a King as first card added in this pile
            if (firstCard.getNumber() == 13) {
                moveCard(pile);
                return;
            }
            // Deselects first card if it's not a King
            deselectFirstCard();
        }
    }

    private void moveCard(CardPile toPile) {
        CardPile fromPile = (CardPile) firstCard.getParent();

        // If there are cards on top of the card to move, move them too
        int numOfCards = numOfCardsOnTop(firstCard);
        ArrayList<Card> cardsOnTop = new ArrayList<>();

        if (numOfCards != 0) {
            for (int i = 0; i < numOfCards + 1; i++) {
                Card card = fromPile.drawCard();
                cardsOnTop.add(card);
            }

            Collections.reverse(cardsOnTop);

            for (Card card : cardsOnTop) {
                toPile.addCard(card);
            }
        } else {
            // Remove from old pile and add to new pile slightly below previous card with CardPile.addCard()
            fromPile.getChildren().remove(firstCard);
            toPile.addCard(firstCard);
        }

        // Remove EmptyPileHandler on pile moved to if it has one
        toPile.setOnMouseClicked(null);

        // If pile card moves from now is empty, put EmptyPileHandler on it (so it can take a king)
        if (fromPile.getChildren().isEmpty() && !fromPile.equals(presentPile)) {
            fromPile.setOnMouseClicked(new EmptyPileHandler());
        }

        flipUpRemainingCard(fromPile);
        deselectFirstCard();
    }

    private void moveCardToFinishPile(CardPile toPile) {

        int numOfCards = numOfCardsOnTop(firstCard);
        // If there are cards on top of the card, don't move it
        if (numOfCards !=0) {
            deselectFirstCard();
            return;
        }
        // Remove it from the pile it came from
        CardPile fromPile = (CardPile) firstCard.getParent();
        fromPile.getChildren().remove(firstCard);

        // Add it straight on top of cards in finish pile
        firstCard.relocate(toPile.getTranslateX(), toPile.getTranslateY());
        toPile.getChildren().add(firstCard);

        // Remove handler on finishPile
        toPile.setOnMouseClicked(null);

        // If pile card moves from now is empty, put EmptyPileHandler on it (so it can take a king)
        if (fromPile.getChildren().isEmpty() && !fromPile.equals(presentPile)) {
            fromPile.setOnMouseClicked(new EmptyPileHandler());
        }

        flipUpRemainingCard(fromPile);
        deselectFirstCard();
    }

    private void flipUpRemainingCard(CardPile fromPile) {

        // If the remaining pile isn't empty get the top card
        if (!fromPile.getChildren().isEmpty()) {
            Card topCard = (Card) fromPile.getChildren().get(fromPile.getChildren().size()-1);

            // If the card isn't already open, flip it
            if (!topCard.isOpen()) {
                topCard.setOpen(new SelectHandler());
            }

        // If the pile is empty, and it's a finish pile, activate its handler
        } else if (finishPiles.contains(fromPile.getParent())){
            fromPile.setOnMouseClicked(new FinishPileHandler());
        }
    }

    private void deselectFirstCard() {
        firstCard.setEffect(null);
        firstCard = null;
    }

    private int numOfCardsOnTop(Card card) {
        CardPile pile = (CardPile) card.getParent();

        int cardIndex = pile.getChildren().indexOf(card);

        return pile.getChildren().subList(cardIndex, pile.getChildren().size()-1).size();
    }
}
