import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.util.Duration;

/**
 * Child class of application that defines JavaFX application for jordle.
 * @author Thomas Le
 * @version 1
 */
public class Jordle extends Application {
    private static int[] stats = new int[7];

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Jordle");

        JordleGame game = new JordleGame();

        for (int i = 0; i < 7; i++) {
            stats[i] = 0;
        }

        GridPane letterGridPane = new GridPane();
        updateLetterGrid(game, letterGridPane);
        letterGridPane.setHgap(5);
        letterGridPane.setVgap(5);
        letterGridPane.setAlignment(Pos.CENTER);

        Stage instructionWindow = new Stage();
        instructionWindow.setTitle("Welcome to Jordle!");
        VBox instructionPane = new VBox();
        Label instructionMessage = new Label(
                "Instructions:\n\nGuess the JORDLE in 6 tries.\n\n"
                + "Each guess must be a 5-letter string. Hit the enter button to submit.\n\n"
                + "After each guess, the color of the tiles will change to show how close your guess was to the word.");
        Button btnCloseWindow = new Button("Got it!");
        btnCloseWindow.setOnAction((event) -> {
            instructionWindow.close();
        });
        instructionPane.setPadding(new Insets(10, 10, 10, 20));
        instructionPane.setSpacing(20);
        instructionPane.getChildren().add(instructionMessage);
        instructionPane.getChildren().add(btnCloseWindow);
        instructionWindow.setScene(new Scene(instructionPane, 600, 200));

        Stage statsWindow = new Stage();
        statsWindow.setTitle("Statistics");
        VBox statPane = new VBox();
        statPane.setAlignment(Pos.CENTER);
        Label statLabel = new Label();
        updateStats(statLabel);
        Button btnCloseStats = new Button("Close");
        btnCloseStats.setOnAction((event) -> {
            statsWindow.close();
        });
        statPane.setPadding(new Insets(10, 10, 10, 20));
        statPane.setSpacing(20);
        statPane.getChildren().add(statLabel);
        statPane.getChildren().add(btnCloseStats);
        statsWindow.setScene(new Scene(statPane, 200, 250));

        Alert alert = new Alert(Alert.AlertType.ERROR, "ALERT: Your guess MUST contain 5 letters. Try again!");

        FlowPane footer = new FlowPane();
        Label message = new Label();
        updateMessage(game, message);
        message.setFont(Font.font("Helvetica", FontWeight.MEDIUM, FontPosture.REGULAR, 14));
        message.setPadding(new Insets(0, 10, 0, 10));
        Button reset = new Button("Restart");
        reset.setFocusTraversable(false);
        reset.setPadding(new Insets(5, 20, 5, 20));
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetGame(game, letterGridPane, message);
            }
        });
        Button instructions = new Button("Instructions");
        instructions.setFocusTraversable(false);
        instructions.setPadding(new Insets(5, 20, 5, 20));
        instructions.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                instructionWindow.show();
            }
        });
        Button statsbtn = new Button("Stats");
        statsbtn.setFocusTraversable(false);
        statsbtn.setPadding(new Insets(5, 20, 5, 20));
        statsbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateStats(statLabel);
                statsWindow.show();
            }
        });
        footer.getChildren().add(message);
        footer.getChildren().add(reset);
        footer.getChildren().add(instructions);
        footer.getChildren().add(statsbtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(60, 0, 0, 0));
        footer.setHgap(10);

        StackPane titlePane = new StackPane();
        Label title = new Label("Jordle");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 52));
        titlePane.getChildren().add(title);
        titlePane.setPadding(new Insets(10, 0, 40, 0));

        VBox mainPane = new VBox();
        mainPane.getChildren().add(titlePane);
        mainPane.getChildren().add(letterGridPane);
        mainPane.getChildren().add(footer);

        Scene scene = new Scene(mainPane, 650, 700);
        scene.setOnKeyPressed((KeyEvent e) -> {
            String key = e.getCode().toString().toUpperCase();
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(key)) {
                game.addChar(key);
                updateLetterGrid(game, letterGridPane);
            }
            if ("ENTER".equals(key)) {
                if (!game.attempt()) {
                    showAlert(alert);
                }
                updateMessage(game, message);
                if (game.getHasWon()) {
                    System.out.println("Won");
                    updateLetterGridRowOnWin(game.getAttempt() - 1, game, letterGridPane);
                    stats[game.getAttempt() - 1] = stats[game.getAttempt() - 1] + 1;
                } else if (!game.getHasWon() && game.getAttempt() == 6) {
                    stats[6] = stats[6] + 1;
                } else {
                    updateLetterGridRow(game.getAttempt() - 1, game, letterGridPane);
                }
            }
            if ("BACK_SPACE".equals(key)) {
                game.removeChar();
                updateLetterGrid(game, letterGridPane);
            }
        });
        letterGridPane.requestFocus();

        primaryStage.setScene(scene);
        primaryStage.show();

        fade(letterGridPane, 3000);
    }

    /**
     * Method that updates a gridpane based on the game.
     * @param game JordleGame
     * @param grid gridPane
     */
    private void updateLetterGrid(JordleGame game, GridPane grid) {
        grid.getChildren().clear();
        for (int col = 0; col < 5; col++) {
            for (int row = 0; row < 6; row++) {
                setGridState(game.getLetterGrid()[col][row], game.getStateGrid()[col][row], grid, row, col);
            }
        }
    }

    /**
     * Updates letter grid on specific row and animates.
     * @param row row
     * @param game JordleGrid
     * @param grid grid
     */
    private void updateLetterGridRow(int row, JordleGame game, GridPane grid) {
        for (int col = 0; col < 5; col++) {
            fade(setGridState(game.getLetterGrid()[col][row], game.getStateGrid()[col][row], grid, row, col), 500);
        }
    }

    /**
     * Updates letter grid on win.
     * @param row row
     * @param game JordleGrid
     * @param grid grid
     */
    private void updateLetterGridRowOnWin(int row, JordleGame game, GridPane grid) {
        for (int col = 0; col < 5; col++) {
            rotate(setGridState(game.getLetterGrid()[col][row], game.getStateGrid()[col][row], grid, row, col), 3, 500);
        }
    }

    /**
     * Method that updates the message display.
     * @param game JordleGame
     * @param label label
     */
    private void updateMessage(JordleGame game, Label label) {
        label.setText(game.getMessage());
    }

    /**
     * Method that updates the stats display.
     * @param int stats
     * @param label label
     */
    private void updateStats(Label label) {
        label.setText("Statistics\n---------------\n1 ---------- " + stats[0] + "\n2 ---------- " + stats[1]
            + "\n3 ---------- " + stats[2] + "\n4 ---------- " + stats[3] + "\n5 ---------- " + stats[4]
            + "\n6 ---------- " + stats[5] + "\nLosses ----- " + stats[6]);
    }

    /**
     * Method to activate alert.
     * @param alert alert
     */
    private void showAlert(Alert alert) {
        alert.showAndWait();
    }

    /**
     * Method that adds a grid tile to the gridPane.
     * @return Label label
     * @param value value displayed in label
     * @param style style of tile
     * @param grid gridPane
     * @param row row
     * @param col column
     */
    private Label setGridState(String value, String style, GridPane grid, int row, int col) {
        Label square = new Label(value);
        if (style.equals("default")) {
            square.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 38));
            square.setTextFill(Color.BLACK);
            square.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            square.setBorder(new Border(new BorderStroke(Color.SLATEGRAY, BorderStrokeStyle.SOLID, new CornerRadii(3),
                    new BorderWidths(2))));
        } else if (style.equals("notPresent")) {
            square.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 38));
            square.setTextFill(Color.WHITE);
            square.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
            square.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(3),
                    new BorderWidths(2))));
        } else if (style.equals("present")) {
            square.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 38));
            square.setTextFill(Color.WHITE);
            square.setBackground(new Background(new BackgroundFill(Color.LIMEGREEN, null, null)));
            square.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(3),
                    new BorderWidths(2))));
        } else if (style.equals("correct")) {
            square.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 38));
            square.setTextFill(Color.WHITE);
            square.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
            square.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(3),
                    new BorderWidths(2))));
        }
        square.setAlignment(Pos.CENTER);
        square.setMinHeight(66);
        square.setMinWidth(66);
        grid.add(square, col, row);
        return square;
    }

    /**
     * Method that resets game.
     * @param game JordleGame
     * @param grid gridPane
     * @param label message label
     */
    private void resetGame(JordleGame game, GridPane grid, Label label) {
        game.initialize();
        updateMessage(game, label);
        updateLetterGrid(game, grid);
        fade(grid, 3000);
    }

    /**
     * Method that fades given node.
     * @param node node
     */
    private void fade(Node node, int time) {
        FadeTransition animate = new FadeTransition(Duration.millis(time));
        animate.setNode(node);
        animate.setFromValue(0.0);
        animate.setToValue(1.0);
        animate.playFromStart();
    }

    /**
     * Method that rotates given node.
     * @param node node
     * @param int rotations
     * @param int time
     */
    private void rotate(Node node, int rotations, int time) {
        RotateTransition animate = new RotateTransition(Duration.millis(time));
        animate.setNode(node);
        animate.setByAngle(360);
        animate.setCycleCount(rotations);
        animate.playFromStart();
    }
}