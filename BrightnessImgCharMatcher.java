package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.*;

/**
 * class that matches ASCII characters based on brightness to sub-images of an image of colored pixels
 *
 * @author Aviel Raclaw
 */
public class BrightnessImgCharMatcher {
    // constants
    public static final int PIXELS = 16;
    public static final double BLUE_PERCENTAGE_OF_GREY = 0.0722;
    public static final double GREEN_PERCENTAGE_OF_GREY = 0.7152;
    public static final double RED_PERCENTAGE_OF_GREY = 0.2126;
    public static final int MAX_RGB = 255;

    // fields
    private final HashMap<Character, Double> characterToBrightness = new HashMap<>();
    private final HashMap<Double, Character> brightnessToCharacter = new HashMap<>();
    private final HashSet<Character> charactersAdded = new HashSet<>(); // characters already added
    private final image.Image img;
    private final String font;

    /**
     * constructor
     *
     * @param img  image
     * @param font font to use
     */
    public BrightnessImgCharMatcher(Image img, String font) {
        this.img = img;
        this.font = font;
    }

    /**
     * calculate character's brightness
     *
     * @param c character
     * @return sum of true's divided by total
     */
    private double getCharBrightness(char c) {
        int numOfTrue = 0;
        for (boolean[] row : CharRenderer.getImg(c, PIXELS, font)) {
            for (boolean i : row) {
                if (i) {
                    numOfTrue++;
                }
            }
        }
        return (double) numOfTrue / (PIXELS * PIXELS);
    }

    /**
     * normalize brightness
     *
     * @param charBrightness original brightness
     * @param minBrightness  minimum brightness of all characters
     * @param maxBrightness  max brightness of all characters
     * @return normalized value
     */
    private double normalizeCharBrightness(double charBrightness, double minBrightness,
                                           double maxBrightness) {
        return (charBrightness - minBrightness) / (maxBrightness - minBrightness);
    }

    /**
     * calculate brightness of sub-image
     *
     * @param subImage sub-image
     * @return brightness of sub-image
     */
    private double getSubImageBrightness(ArrayList<Color> subImage) {
        double totalGreyness = 0;
        for (Color pixel : subImage) {
            totalGreyness += pixel.getRed() * RED_PERCENTAGE_OF_GREY +
                    pixel.getGreen() * GREEN_PERCENTAGE_OF_GREY + pixel.getBlue() * BLUE_PERCENTAGE_OF_GREY;
        }
        return totalGreyness / (subImage.size() * MAX_RGB);
    }

    /**
     * find the closest value of brightness from characters to brightness of sub-image
     *
     * @param brightness              brightness of sub-image
     * @param normalizedBrightnessSet set of brightnesses of characters
     * @return closest brightness
     */
    private double getMostSuitableBrightness(double brightness, TreeSet<Double> normalizedBrightnessSet) {
        Double floor = normalizedBrightnessSet.floor(brightness);
        Double ceiling = normalizedBrightnessSet.ceiling(brightness);
        if (floor == null) {
            return ceiling;
        }
        if (ceiling == null) {
            return floor;
        }
        if (ceiling - brightness <= brightness - floor) {
            return ceiling;
        }
        return floor;
    }

    /**
     * choose characters to put in place of sub-images
     *
     * @param numCharsInRow number of characters to put in row
     * @param charSet       set of characters to use
     * @return 2-dimensional character array of character representing image
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        //set up characters
        ArrayList<Character> filteredCharSet = new ArrayList<>();
        for (char c : charSet) {
            if (!charactersAdded.contains(c)) {
                filteredCharSet.add(c);
            }
        }
        TreeSet<Double> notNormalizedBrightnessSet = new TreeSet<>();
        HashMap<Double, Double> normalizedToBrightness = new HashMap<>();
        TreeSet<Double> normalizedBrightnessSet = new TreeSet<>();
        setUpCharacters(filteredCharSet, charSet, notNormalizedBrightnessSet,
                normalizedToBrightness, normalizedBrightnessSet);

        int subImageSize = img.getWidth() / numCharsInRow;
        int numCharsInCol = img.getHeight() / (subImageSize);
        char[][] ASCIIImage = new char[numCharsInCol][numCharsInRow];

        ArrayList<ArrayList<Color>> subImages = img.getSubImageDivision(subImageSize);
        double subImageBrightness, suitableCharBrightness;
        int charRow = 0, charCol = 0;
        for (ArrayList<Color> subImage : subImages) {
            subImageBrightness = getSubImageBrightness(subImage);
            suitableCharBrightness = normalizedToBrightness.get(getMostSuitableBrightness(subImageBrightness,
                    normalizedBrightnessSet));
            ASCIIImage[charRow][charCol] = brightnessToCharacter.get(suitableCharBrightness);
            charCol++;
            if (charCol == numCharsInRow) {
                charCol = 0;
                charRow++;
            }
        }
        return ASCIIImage;
    }

    /**
     * sets up the characters
     *
     * @param filteredCharSet            filtered set characters that haven't been added yet
     * @param charSet                    set of characters to use
     * @param notNormalizedBrightnessSet set of original brightness values
     * @param normalizedToBrightness     maps normalized values to original values
     * @param normalizedBrightnessSet    normalized brightness set
     */
    private void setUpCharacters(ArrayList<Character> filteredCharSet, Character[] charSet,
                                 TreeSet<Double> notNormalizedBrightnessSet,
                                 HashMap<Double, Double> normalizedToBrightness,
                                 TreeSet<Double> normalizedBrightnessSet) {
        double charBrightness;
        for (char c : filteredCharSet) { //adds characters to database
            charactersAdded.add(c);
            charBrightness = getCharBrightness(c);
            characterToBrightness.put(c, charBrightness);
            brightnessToCharacter.put(charBrightness, c);
        }
        for (char c : charSet) { // adds requested character brightnesses to set
            notNormalizedBrightnessSet.add(characterToBrightness.get(c));
        }

        double maxBrightness = notNormalizedBrightnessSet.last();
        double minBrightness = notNormalizedBrightnessSet.first();
        double normalizedBrightness;
        for (double brightness : notNormalizedBrightnessSet) {
            normalizedBrightness = normalizeCharBrightness(brightness, minBrightness, maxBrightness);
            normalizedBrightnessSet.add(normalizedBrightness);
            //maps normalized brightnesses to original brightnesses
            normalizedToBrightness.put(normalizedBrightness, brightness);
        }
    }
}
