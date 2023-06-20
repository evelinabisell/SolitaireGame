import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Card extends ImageView {
    private final int number;
    private final Suit suit;
    private final String color;
    private final Image image;
    private boolean isOpen;


    public Card(int number, Suit suit, Image image) {
        this.number = number;
        this.suit = suit;
        this.image = image;

        if (this.suit == Suit.hearts || this.suit == Suit.diamonds) {
            this.color = "red";
        } else {
            this.color = "black";
        }

        //Set size of cards
        setFitHeight(200);
        setFitWidth(130);
    }


    //Change card image to show its back
    public void setCovered() {
        setImage(new Image("PNG-cards-1.3/card_back2.png"));
        setOnMouseClicked(null);
        isOpen = false;
    };

    //Show cards front
    public void setOpen(EventHandler handler) {
        setImage(image);
        setOnMouseClicked(handler);
        isOpen = true;
    };

    public int getNumber() {
        return number;
    }

    public Suit getSuit() {
        return suit;
    }

    public String getColor() {
        return color;
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public String toString() {
        return number + " of " + suit;
    }
}
