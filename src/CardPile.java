import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class CardPile extends Pane{

    CardPile(Node... items) {
        super(items);
        setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        setPrefSize(130, 200);
    }

    @Override
    public String toString() {
        return "cardpile";
    }

    public void addCard(Node node) {
        node.relocate(getTranslateX(), getTranslateY() + 25 * getChildren().size());
        getChildren().add(node);
    }

    public Card drawCard() {
        if (!getChildren().isEmpty()) {
            return (Card) getChildren().remove(getChildren().size()-1);
        }
        return null;
    }
}

