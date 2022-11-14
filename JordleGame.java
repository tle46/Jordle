/**
 * Class that represents instance of jordle game.
 * @author Thomas Le
 * @version 1
 */
public class JordleGame {
    private int attempt = 0;
    private int cursorIndex = 0;
    private String[][] letterGrid = new String[5][6];
    private String[][] stateGrid = new String[5][6];
    private String strAnswer;
    private String[] answer = new String[5];
    private boolean hasWon;
    private String message;

    /**
     * Constructor with no params.
     */
    public JordleGame() {
        initialize();
    }

    /**
     * Method that returns random string in word list file.
     * @return String string
     */
    private String getRandomAnswer() {
        strAnswer = Words.list.get((int) (Math.random() * Words.list.size())).toUpperCase();
        System.out.println(strAnswer);
        return strAnswer;
    }

    /**
     * Method that updates the stategrid according to guess.
     * @param guess guess
     */
    public void checkWord(String[] guess) {
        String modAnswer = new String(strAnswer);

        for (int col = 0; col < 5; col++) {
            if (guess[col].equals(answer[col])) {
                stateGrid[col][attempt] = "correct";
                modAnswer = modAnswer.replaceFirst(guess[col], "0");
            }
        }

        for (int col = 0; col < 5; col++) {
            if (stateGrid[col][attempt].equals("correct")) {
                continue;
            } else if (modAnswer.contains(guess[col])) {
                stateGrid[col][attempt] = "present";
                modAnswer = modAnswer.replaceFirst(guess[col], "0");
            } else {
                stateGrid[col][attempt] = "notPresent";
            }
        }
    }

    /**
     * Method that represents a guess.
     * @return boolean if guess was valid
     */
    public boolean attempt() {
        if (cursorIndex == 5 && !hasWon) {
            String[] guess = new String[5];
            for (int i = 0; i < 5; i++) {
                guess[i] = letterGrid[i][attempt];
            }
            checkWord(guess);
            hasWon = won();
            attempt++;
            cursorIndex = 0;
            updateMessage();
            return true;
        } else {
            message = "Your guess MUST contain 5 letters. Try again!";
            return false;
        }
    }

    /**
     * Method that represents when letter is added to grid.
     * @param s letter
     */
    public void addChar(String s) {
        if (cursorIndex < 5 && attempt < 6 && !hasWon) {
            letterGrid[cursorIndex][attempt] = s;
            cursorIndex++;
        }
    }

    /**
     * Method that represents when letter is removed.
     */
    public void removeChar() {
        if (cursorIndex > 0 && !hasWon) {
            letterGrid[cursorIndex - 1][attempt] = "";
            cursorIndex--;
        }
    }

    /**
     * Checks if the user has won.
     * @return boolean hasWon
     */
    public boolean won() {
        for (int i = 0; i < 5; i++) {
            if (!stateGrid[i][attempt].equals("correct")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates message to user.
     */
    public void updateMessage() {
        if (hasWon) {
            message = "Congratulations you guessed the correct word!";
        } else if (!hasWon && attempt == 6) {
            message = "You ran out of guesses. The correct answer was " + strAnswer + ".";
        } else {
            message = "Guess a string! You have " + (6 - attempt) + " attempts left.";
        }
    }

    /**
     * Initializes game to starting settings and data.
     */
    public void initialize() {
        attempt = 0;
        cursorIndex = 0;
        hasWon = false;
        answer = getRandomAnswer().split("");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                letterGrid[i][j] = "";
                stateGrid[i][j] = "default";
            }
        }
        updateMessage();
    }

    /**
     * Getter for message.
     * @return String message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for attempt.
     * @return int attempt
     */
    public int getAttempt() {
        return attempt;
    }

    /**
     * Getter for hasWon.
     * @return int attempt
     */
    public boolean getHasWon() {
        return hasWon;
    }

    /**
     * Getter for stateGrid.
     * @return String[][] stateGrid
     */
    public String[][] getStateGrid() {
        return stateGrid;
    }

    /**
     * Getter for letterGrid.
     * @return String[][] letterGrid
     */
    public String[][] getLetterGrid() {
        return letterGrid;
    }
}
