package image;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Facade for the image module and an interface representing an image.
 *
 * @author Dan Nirel
 */
public interface Image {
    Color getPixel(int x, int y);

    int getWidth();

    int getHeight();

    /**
     * Open an image from file. Each dimensions of the returned image is guaranteed
     * to be a power of 2, but the dimensions may be different.
     *
     * @param filename a path to an image file on disk
     * @return an object implementing Image if the operation was successful,
     * null otherwise
     */
    static Image fromFile(String filename) {
        try {
            return new FileImage(filename);
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * Allows iterating the pixels' colors by order (first row, second row and so on).
     *
     * @return an Iterable<Color> that can be traversed with a foreach loop
     */
    default Iterable<Color> pixels() {
        return new ImageIterableProperty<>(
                this, this::getPixel);
    }

    /**
     * separates the image into sub-images.
     *
     * @param subImageSize sub-image square dimensions
     * @return ArrayList of ArrayLists of Colors. each inner ArrayList represents a sub-image, the outer
     * ArrayList represents the whole image.
     */
    default ArrayList<ArrayList<Color>> getSubImageDivision(int subImageSize) {
        ArrayList<ArrayList<Color>> subImages = new ArrayList<>();
        int subImageIndex = 0;
        for (int row = 0; row < getHeight(); row += subImageSize) {
            for (int col = 0; col < getWidth(); col += subImageSize) {
                subImages.add(new ArrayList<>()); // new sub-image
                // adds to sub-image pixels square by square
                for (int innerRow = row; innerRow < row + subImageSize; innerRow++) {
                    for (int innerCol = col; innerCol < col + subImageSize; innerCol++) {
                        subImages.get(subImageIndex).add(getPixel(innerRow, innerCol));
                    }
                }
                subImageIndex++;
            }
        }
        return subImages;
    }
}
