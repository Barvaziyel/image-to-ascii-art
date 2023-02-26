package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.Scanner;
import java.util.TreeSet;

/**
 * Shell class, runs the program.
 *
 * @author Aviel Raclaw
 */
public class Shell {
    // constants
    public static final int ZERO_CHARACTER = 48;
    public static final int TEN_CHARACTER = 58;
    public static final String AWAITING_INPUT = ">>> ";
    public static final String EXIT = "exit";
    public static final String PRINT_CHARS = "chars";
    public static final String SPACE = " ";
    public static final String ADD = "add ";
    public static final String ALL = "all";
    public static final char SPACE_CHAR = ' ';
    public static final int CHAR_LENGTH = 1;
    public static final int FIRST_VALID_CHAR = 33;
    public static final int LAST_VALID_CHAR = 126;
    public static final String SPACE_WORD = "space";
    public static final char DASH_CHAR = '-';
    public static final int ADD_LENGTH = 4;
    public static final int REMOVE_LENGTH = 7;
    public static final String REMOVE = "remove ";
    public static final String REMOVING_ERR_MSG = "Did not remove due to incorrect format";
    public static final String ADDING_ERR_MSG = "Did not add due to incorrect format";
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final int INITIAL_CHARS_IN_ROW = 64;
    public static final int RES_LENGTH = 4;
    public static final String RES = "res ";
    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String RES_ERR_MSG = "Did not change due to exceeding boundaries";
    public static final int CHARS_IN_ROW_MULTIPLIER = 2;
    public static final String WIDTH_SET_TO = "Width set to ";
    public static final String CONSOLE = "console";
    public static final String INPUT_ERR_MSG = "Did not executed due to incorrect command";
    public static final String RENDER = "render";
    public static final String DEFAULT_FONT = "Courier New";
    public static final String HTML_FILE_NAME = "out.html";

    //fields
    private final Image image;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private boolean consoleFlag = false;

    /**
     * Shell constructor
     *
     * @param image image to work with
     */
    public Shell(Image image) {
        this.image = image;
        minCharsInRow = Math.max(1, image.getWidth() / image.getHeight());
        maxCharsInRow = image.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
    }

    /**
     * runs the program
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        TreeSet<Character> charSet = new TreeSet<>();
        for (int i = ZERO_CHARACTER; i < TEN_CHARACTER; i++) {
            charSet.add((char) i); //adds default characters
        }
        BrightnessImgCharMatcher artCreator = new BrightnessImgCharMatcher(image, DEFAULT_FONT);
        while (true) { //get input until exit is input
            System.out.print(AWAITING_INPUT);
            userInput = scanner.nextLine();
            if (!userInputDelegator(userInput, charSet, artCreator)) {
                return;
            }
        }
    }

    /**
     * delegates to other functions based on user input
     *
     * @param userInput  user input
     * @param charSet    set of added characters
     * @param artCreator creates ASCII art from image
     * @return false if user input is 'exit', else true
     */
    private boolean userInputDelegator(String userInput, TreeSet<Character> charSet,
                                       BrightnessImgCharMatcher artCreator) {
        if (userInput.equals(EXIT)) { //exit
            return false;
        } else if (userInput.equals(PRINT_CHARS)) { //chars
            printChars(charSet);
            return true;
        } else if (userInput.startsWith(ADD)) { //add
            addOrRemoveChars(userInput, charSet, false);
            return true;
        } else if (userInput.startsWith(REMOVE)) { //remove
            addOrRemoveChars(userInput, charSet, true);
            return true;
        } else if (userInput.startsWith(RES)) { //res
            if (userInput.substring(RES_LENGTH).equals(UP)) {
                upOrDownRes(true);
                return true;
            } else if (userInput.substring(RES_LENGTH).equals(DOWN)) {
                upOrDownRes(false);
                return true;
            }
        } else if (userInput.equals(CONSOLE)) { // console
            consoleFlag = true;
            return true;
        } else if (userInput.equals(RENDER)) { // render
            renderASCIIArt(charSet, artCreator);
            return true;
        }
        System.out.println(INPUT_ERR_MSG);
        return true;
    }

    /**
     * renders the ASCII art
     *
     * @param charSet    characters to use in rendering
     * @param artCreator creates the art from the image
     */
    private void renderASCIIArt(TreeSet<Character> charSet, BrightnessImgCharMatcher artCreator) {
        char[][] ASCIIArt = getASCIIArt(charSet, artCreator);
        if (consoleFlag) { // render to console
            ConsoleAsciiOutput output = new ConsoleAsciiOutput();
            output.output(ASCIIArt);
        } else { // render to html
            HtmlAsciiOutput output = new HtmlAsciiOutput(HTML_FILE_NAME, DEFAULT_FONT);
            output.output(ASCIIArt);
        }
    }

    /**
     * gets the ASCII art from the image
     *
     * @param charSet    characters to use in ASCII art
     * @param artCreator creates the art
     * @return 2-dimensional array of ASCII characters, representing the ASCII art
     */
    private char[][] getASCIIArt(TreeSet<Character> charSet, BrightnessImgCharMatcher artCreator) {
        Character[] charArray = new Character[charSet.size()];
        int ind = 0;
        for (char c : charSet) {
            charArray[ind] = c; // add characters from charSet to a character array
            ind++;
        }
        return artCreator.chooseChars(charsInRow, charArray);
    }

    /**
     * raise or lower resolution
     *
     * @param upFlag flag that is true if raising, false if lowering
     */
    private void upOrDownRes(boolean upFlag) {
        if ((upFlag && charsInRow == maxCharsInRow) || (!upFlag && charsInRow == minCharsInRow)) {
            System.out.println(RES_ERR_MSG); //out of bounds
            return;
        }
        if (upFlag) { // up resolution
            charsInRow *= CHARS_IN_ROW_MULTIPLIER;
        } else { // down resolution
            charsInRow /= CHARS_IN_ROW_MULTIPLIER;
        }
        System.out.println(WIDTH_SET_TO + charsInRow);
    }

    /**
     * adds or removes characters
     *
     * @param userInput  user input
     * @param charSet    character set
     * @param removeFlag flag, true if removing, false if adding
     */
    private void addOrRemoveChars(String userInput, TreeSet<Character> charSet, boolean removeFlag) {
        String substring = userInput.substring(ADD_LENGTH);
        if (removeFlag) {
            substring = userInput.substring(REMOVE_LENGTH);
        }
        // add or remove one character
        if (substring.length() == CHAR_LENGTH && !substring.equals(SPACE)) {
            addOrRemoveOneChar(userInput, charSet, removeFlag);
            return;
        }
        // add or remove all characters
        if (substring.equals(ALL)) {
            addOrRemoveAllChars(charSet, removeFlag);
            return;
        }
        // add or remove range of characters
        if (FIRST_VALID_CHAR <= substring.charAt(FIRST) &&
                substring.charAt(FIRST) <= LAST_VALID_CHAR && //first character is valid
                substring.charAt(SECOND) == DASH_CHAR && //second character is a dash '-'
                FIRST_VALID_CHAR <= substring.charAt(THIRD) &&
                substring.charAt(THIRD) <= LAST_VALID_CHAR) { //third character is valid
            addOrRemoveCharRange(substring, charSet, removeFlag);
            return;
        }
        // add or remove space character
        if (substring.equals(SPACE_WORD)) {
            if (removeFlag) {
                charSet.remove(SPACE_CHAR);
            } else {
                charSet.add(SPACE_CHAR);
            }
            return;
        }
        // if invalid print error message
        String errorMessage = REMOVING_ERR_MSG;
        if (removeFlag) {
            errorMessage = ADDING_ERR_MSG;
        }
        System.out.println(errorMessage);
    }

    /**
     * adds or removes one character
     *
     * @param userInput  user input
     * @param charSet    character set
     * @param removeFlag flag, true if removing, false if adding
     */
    private static void addOrRemoveOneChar(String userInput, TreeSet<Character> charSet, boolean removeFlag) {
        if (removeFlag) {
            charSet.remove(userInput.charAt(REMOVE_LENGTH)); // removes the character
        } else {
            charSet.add(userInput.charAt(ADD_LENGTH)); // adds the character
        }
    }

    /**
     * adds or removes a range of characters
     *
     * @param substring  the substring of the range
     * @param charSet    character set
     * @param removeFlag flag, true if removing, false if adding
     */
    private static void addOrRemoveCharRange(String substring, TreeSet<Character> charSet,
                                             boolean removeFlag) {
        char smallerChar = substring.charAt(FIRST);
        char biggerChar = substring.charAt(THIRD);
        if (smallerChar > biggerChar) { // swaps values if smallerChar is bigger than biggerChar
            char temp = smallerChar;
            smallerChar = biggerChar;
            biggerChar = temp;
        }
        if (removeFlag) { //removes range of chars
            for (char character = smallerChar; character <= biggerChar; character++) {
                charSet.remove(character);
            }
        } else { // adds range of chars
            for (char character = smallerChar; character <= biggerChar; character++) {
                charSet.add(character);
            }
        }
    }

    /**
     * adds or removes all chars
     *
     * @param charSet    char set
     * @param removeFlag flag, true if removing, false if adding
     */
    private static void addOrRemoveAllChars(TreeSet<Character> charSet, boolean removeFlag) {
        if (removeFlag) { //removes all chars
            for (int i = FIRST_VALID_CHAR; i <= LAST_VALID_CHAR; i++) {
                charSet.remove((char) i);
            }
            charSet.remove(SPACE_CHAR);
        } else { //adds all chars
            for (int i = FIRST_VALID_CHAR; i <= LAST_VALID_CHAR; i++) {
                charSet.add((char) i);
            }
            charSet.add(SPACE_CHAR);
        }
    }

    /**
     * prints all chars in charSet
     *
     * @param charSet character set
     */
    private static void printChars(TreeSet<Character> charSet) {
        for (char character : charSet) {
            System.out.print(character + SPACE);
        }
        System.out.println();
    }
}
