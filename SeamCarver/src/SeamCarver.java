import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import static java.lang.Math.sqrt;

public class SeamCarver {
    private Picture picture;
    private int width;
    private int height;

    private static final double BORDER_ENERGY = 1000.0;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Parameter of constructor can not be null");
        }
        this.picture = new Picture(picture);
        width = this.picture.width();
        height = this.picture.height();

    }

    // current picture
    public Picture picture() {
        return this.picture;
    }

    // width of current picture
    public int width() {
        return this.width;
    }

    // height of current picture
    public int height() {
        return this.height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width) {
            throw new IllegalArgumentException(String.format("x coordinates can only be in [0, %d)", width));
        }

        if (y < 0 || y >= height) {
            throw new IllegalArgumentException(String.format("y coordinates can only be in [0, %d)", height));
        }

        return dualGradient(x, y);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return null;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return null;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, null);

    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, null);

    }

    private void validateSeam(int[] seam, IndexMinPQ<Double>[] arrays) {
        if (seam == null) {
            throw new IllegalArgumentException("Seam array is a null pointer!");
        }
        if (arrays.length <= 1) {
            throw new IllegalArgumentException("Picture has only one column or row which can not be removed!");
        }
        if (seam.length != arrays.length) {
            throw new IllegalArgumentException("The length of seam array is not equal to height of picture!");
        }
        for (int ix = 0; ix < seam.length; ++ix) {
            if (!arrays[ix].contains(seam[ix])) {
                throw new IllegalArgumentException("Not a valid seam pixel!");
            }
        }
    }

    private double dualGradient(int x, int y) {
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return BORDER_ENERGY;
        }

        int x_gradient = gradient(picture.get(x -1 , y), picture.get(x + 1, y));
        int y_gradient = gradient(picture.get(x, y - 1), picture.get(x, y + 1));

        return sqrt(x_gradient + y_gradient);
    }

    private int gradient(Color prev, Color next) {
        int red_diff = prev.getRed() - next.getRed();
        int green_diff = prev.getGreen() - next.getGreen();
        int blue_diff = prev.getBlue() - next.getBlue();

        return red_diff * red_diff + green_diff * green_diff + blue_diff * blue_diff;
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }
}
