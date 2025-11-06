// Main class which handles all UI

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;

public class GuessingGame extends Application implements ReceiverHandler{
    private BorderPane root;
    private StackPane center;
    private GridPane myGridPane;
    private GridPane opponentGridPane;
    private Node[][] myGrid;
    private Node[][] opponentGrid;
    private Connector connector;

    private Button startBtn;
    private Button joinBtn;
    private TextField hostField;
    private TextField portField;
    private Text text;
    private Text resultText;

    private boolean goingFirst = false;
    private int firstTurnPlacements = 0;
    private int correctGuesses = 0;

    public static void main(String[] args) {
        launch();
    }

    // Sets up all the initial UI, creates all grids and nodes, handles button events
    @Override
    public void start(Stage stage) throws Exception {
        root = new BorderPane();
        center = new StackPane();
        opponentGridPane = new GridPane();
        myGridPane = new GridPane();
        opponentGrid = new Node[5][5];
        myGrid = new Node[5][5];
        connector = new Connector();
        startBtn = new Button("Start Game");
        joinBtn = new Button("Join Game");
        hostField = new TextField();
        portField = new TextField();
        hostField.setPromptText("Enter IP address:");
        portField.setPromptText("Enter port number:");
        text = new Text("Guessing Game");
        text.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
        resultText = new Text();

        final Image defaultImg = new Image(new File("resources/default.jpg").toURI().toString());
        final Image correctImg = new Image(new File("resources/correct.png").toURI().toString());
        final Image wrongImg = new Image(new File("resources/wrong.jpg").toURI().toString());
        final Image okImg = new Image(new File("resources/ok.jpg").toURI().toString());
        int cols = 5;
        int rows = 5;
        int size = 100;

        HBox topBox = new HBox(text);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(20));

        VBox textBox = new VBox(15);
        textBox.setAlignment(Pos.CENTER);
        textBox.setPadding(new Insets(20));
        textBox.getChildren().addAll(hostField, portField);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20));
        buttons.getChildren().addAll(joinBtn, startBtn);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                myGrid[row][col] = new Node(defaultImg, correctImg, wrongImg, okImg);
                myGrid[row][col].setPrefSize(size, size);
                myGrid[row][col].setDisable(true);

                opponentGrid[row][col] = new Node(defaultImg, correctImg, wrongImg, okImg);
                opponentGrid[row][col].setPrefSize(size, size);
                opponentGrid[row][col].setDisable(true);

                myGridPane.add(myGrid[row][col], col, row);
                opponentGridPane.add(opponentGrid[row][col], col, row);
            }
        }

        root.setTop(topBox);
        // Set gridpane first to make window correct size automatically
        root.setCenter(myGridPane);
        root.setBottom(buttons);

        // Starts the server via startGame with host and port, and show opponents grid and remove buttons
        startBtn.setOnAction(e -> {
            int port = Integer.parseInt(portField.getText().trim());
            root.setCenter(opponentGridPane);
            root.setBottom(new VBox());
            connector.startGame(port, this);
        });

        // Connects to the server via joinGame with host and port, and show opponents grid and remove buttons
        joinBtn.setOnAction(e -> {
            String host = hostField.getText();
            int port = Integer.parseInt(portField.getText().trim());
            goingFirst = true;
            root.setCenter(opponentGridPane);
            root.setBottom(new VBox());
            connector.joinGame(host, port, this);
            text.setText("Your turn");
        });

        stage.setScene(new Scene(root));
        stage.setTitle("GuessingGame");
        stage.show();
        root.setCenter(textBox);
    }


    // Handles receiving message from the other program, either changing type of a node, ending game by winning or losing, or letting user make guess
    @Override
    public void handleReceived(String message) {
        // User makes guess
        if (message.isEmpty()) {
            text.setText("Your turn");
            guess();
        // User gets to place right and wrong nodes
        } else if (message.equals("place")) {
            text.setText("Your turn");
            firstPlacements();
        // User wins
        } else if (message.equals("win")) {
            resultText.setText("You Win!");
            resultText.setFill(Color.RED);
            resultText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
            center.getChildren().addAll(myGridPane, resultText);
            StackPane.setAlignment(resultText, Pos.CENTER);
            root.setCenter(center);
        // User loses
        } else if (message.equals("loss")) {
            resultText.setText("You Lose!");
            resultText.setFill(Color.RED);
            resultText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
            center.getChildren().addAll(myGridPane, resultText);
            StackPane.setAlignment(resultText, Pos.CENTER);
            root.setCenter(center);
        // User's node's type gets set
        } else {
            String[] pieces = message.split("\\|");

            int row = Integer.parseInt(pieces[1]);
            int col = Integer.parseInt(pieces[2]);

            if (pieces[0].equals("wrong")) {
                myGrid[row][col].setType("wrong");
            } else if (pieces[0].equals("right")) {
                myGrid[row][col].setType("right");
            }
        }
    }

    // Allows the user who joined game to place the right and wrong nodes
    @Override
    public void handleConnected() {
        if (goingFirst) {
            firstPlacements();
        }
    }

    // Iterates through every node in opponents grid and allows user to place right and wrong nodes
    private void firstPlacements() {
        for (int row = 0; row < opponentGrid.length; row++) {
            for (int col = 0; col < opponentGrid[row].length; col++) {
                final Node node = opponentGrid[row][col];
                node.setDisable(false);
                int finalRow = row;
                int finalCol = col;
                node.setOnMouseClicked(e -> {
                    node.setDisable(true);
                    firstTurnPlacements++;
                    // First placement is of type wrong
                    if (firstTurnPlacements == 1) {
                        node.setType("wrong");
                        node.showImage();
                        connector.sendMessage("wrong|" + finalRow + "|" + finalCol);
                    // Next three placements are of type right
                    } else if (firstTurnPlacements > 1) {
                        node.setType("right");
                        node.showImage();
                        connector.sendMessage("right|" + finalRow + "|" + finalCol);
                    }
                    // After 4 placements, it is opponent's turn and users grid gets shown instead of opponents
                    if (firstTurnPlacements == 4) {
                        root.setCenter(myGridPane);
                        disableNodes();
                        text.setText("Opponent's turn");
                        if (goingFirst) {
                            connector.sendMessage("place");
                        } else {
                            connector.sendMessage("");
                        }
                    }
                });
            }
        }
    }

    // Iterates through users grid and allows user to guess a node, losing the game if node is wrong, and winning the game if all right nodes has been found
    private void guess() {
        for (int row = 0; row < myGrid.length; row++) {
            for (int col = 0; col < myGrid[row].length; col++) {
                final Node node = myGrid[row][col];
                // Only lets user click nodes that hasn't been clicked already
                if (node.getClickable()) {
                    node.setDisable(false);
                }
                node.setOnMouseClicked(e -> {
                    disableNodes();
                    node.showImage();
                    text.setText("Opponent's turn");
                    switch (node.getType()) {
                        // Nothing happens, opponents turn
                        case "ok" -> {
                            connector.sendMessage("");
                        }
                        // Right guess. If user has pressed all three of the right nodes, they win, otherwise opponents turn
                        case "right" -> {
                            correctGuesses++;
                            if (correctGuesses >= 3) {
                                resultText.setText("You Win!");
                                resultText.setFill(Color.RED);
                                resultText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
                                center.getChildren().addAll(myGridPane, resultText);
                                StackPane.setAlignment(resultText, Pos.CENTER);
                                root.setCenter(center);
                                connector.sendMessage("loss");
                            } else {
                                connector.sendMessage("");
                            }
                        }
                        // Wrong guess, user lose
                        case "wrong" -> {
                            resultText.setText("You Lose!");
                            resultText.setFill(Color.RED);
                            resultText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
                            center.getChildren().addAll(myGridPane, resultText);
                            StackPane.setAlignment(resultText, Pos.CENTER);
                            root.setCenter(center);
                            connector.sendMessage("win");
                        }
                    }
                });
            }
        }
    }

    // Disables all nodes so user can't press them
    private void disableNodes() {
        for (int row = 0; row < myGrid.length; row++) {
            for (int col = 0; col < myGrid[row].length; col++) {
                myGrid[row][col].setDisable(true);
            }
        }
    }
}