package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 *
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private final Color[][] pixelArray;
    private final int width;
    private final int height;

    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();

        width = getNewDimension(origWidth);
        height = getNewDimension(origHeight);

        pixelArray = new Color[height][width];

        int widthPadding = (width - origWidth) / 2; // padding pixels each side
        int heightPadding = (height - origHeight) / 2; // padding pixels each side, up and down
        // create new image with padding applied
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (row < heightPadding || row >= heightPadding + origHeight
                        || col < widthPadding || col >= widthPadding + origWidth) {
                    pixelArray[row][col] = DEFAULT_COLOR; // add padding pixel
                } else { // add original pixel
                    pixelArray[row][col] = new Color(im.getRGB(col - widthPadding, row - heightPadding));
                }
            }
        }
    }

    /**
     * gets the closest larger power of 2 to input
     *
     * @param origDimension original dimension
     * @return closest power of 2 to original dimension
     */
    private static int getNewDimension(int origDimension) {
        double log = Math.log(origDimension) / Math.log(2);
        if ((int) (Math.ceil(log)) == (int) (Math.floor(log))) {
            log--;
        }
        return (int) Math.pow(2, ((int) log + 1));
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Color getPixel(int x, int y) {
        return pixelArray[x][y];
    }

}
