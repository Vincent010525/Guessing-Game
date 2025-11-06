// Node class which is used for the grid system in the main class

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class Node extends StackPane {
    private final Image correctImg;
    private final Image wrongImg;
    private final Image okImg;
    private final ImageView imageView;
    private String type = "ok";
    private boolean clickable = true;

    // Gets sent the images as parameters so they don't have to load every time a new node is initialized
    public Node(Image defaultImg, Image correctImg, Image wrongImg, Image okImg) {
        this.correctImg = correctImg;
        this.wrongImg = wrongImg;
        this.okImg = okImg;
        imageView = new ImageView(defaultImg);
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        getChildren().add(imageView);
    }

    // Gets type
    public String getType() {
        return type;
    }

    // Sets type
    public void setType(String type) {
        this.type = type;
    }

    // Gets clickable
    public boolean getClickable() {
        return clickable;
    }

    // Sets the image to the type of the node, and sets clickable to false so user cant click it again
    public void showImage() {
        if (Objects.equals(type, "ok")) {
            imageView.setImage(okImg);
            clickable = false;
        }
        if (Objects.equals(type, "right")) {
            imageView.setImage(correctImg);
            clickable = false;
        }
        if (Objects.equals(type, "wrong")) {
            imageView.setImage(wrongImg);
            clickable = false;
        }
    }
}
