import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

import java.lang.Math;

public class SeamCarver {
    private static final double BORDER_ENERGY = 1000.0;

    private Picture picture;
    private int width;
    private int height;
    // used to store gradient of each pixel
    private final double[][] gradients;
    // used to store vertical shortest energy from current pixel to the top pixel
    private double[][] vse;
    private double[][] hse;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Parameter of constructor can not be null");
        }
        this.picture = new Picture(picture);
        width = this.picture.width();
        height = this.picture.height();

        // store by column vectors
        gradients = new double[width][height];
        vse = new double[height][width];
        hse = new double[width][height];
        for (int ix = 0; ix < width; ++ix) {
            for (int jx = 0; jx < height; ++jx) {
                gradients[ix][jx] = BORDER_ENERGY;
                vse[jx][ix] = BORDER_ENERGY;
                hse[ix][jx] = BORDER_ENERGY;
            }
        }

        updateGradient(width, height);
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
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

        return gradients[x][y];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] seam = new int[width];

        if (width > 1 && height > 1) {
            updateHorizontalSE();
        }
        seam[width - 1] = minIndex(hse[width - 1]);

        for (int ix = width - 2; ix >= 0; --ix) {
            for (int jx = 0; jx < height; ++jx) {
                double first = hse[ix + 1][seam[ix + 1]];
                double second = hse[ix][jx] + gradients[ix + 1][seam[ix + 1]];
                if (Math.abs(first - second) < 1e-5 && Math.abs(jx - seam[ix + 1]) <= 1) {
                    seam[ix] = jx;
                    break;
                }
            }
        }

        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] seam = new int[height];

        if (width > 1 && height > 1) {
            updateVerticalSE();
        }
        seam[height - 1] = minIndex(vse[height - 1]);

        for (int ix = height - 2; ix >= 0; --ix) {
            for (int jx = 0; jx < width; ++jx) {
                double first = vse[ix + 1][seam[ix + 1]];
                double second = vse[ix][jx] + gradients[seam[ix + 1]][ix + 1];
                if (Math.abs(first - second) < 1e-5 && Math.abs(jx - seam[ix + 1]) <= 1) {
                    seam[ix] = jx;
                    break;
                }
            }
        }

        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, width, height);

        Picture elder = picture;
        picture = new Picture(width, height - 1);
        for (int ix = 0; ix < width; ++ix) {
            for (int jx = 0, kx = 0; jx < height; ++jx) {
                if (jx == seam[ix]) {
                    continue;
                } else {
                    picture.set(ix, kx++, elder.get(ix, jx));
                }
            }
        }

        --height;
        updateGradient(width, height);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, height, width);

        Picture elder = picture;
        picture = new Picture(width - 1, height);
        for (int ix = 0; ix < height; ++ix) {
            for (int jx = 0, kx = 0; jx < width; ++jx) {
                if (jx == seam[ix]) {
                    continue;
                } else {
                    picture.set(kx++, ix, elder.get(jx, ix));
                }
            }
        }

        --width;
        updateGradient(width, height);
    }

    private int minIndex(double[] energy) {
        double minimum = Double.POSITIVE_INFINITY;
        int index = -1;
        for (int ix = 0; ix < energy.length; ++ix) {
            if (energy[ix] < minimum) {
                index = ix;
                minimum = energy[ix];
            }
        }

        return index;
    }

    // dynamic programming
    private void updateVerticalSE() {
        for (int ix = 1; ix < height; ++ix) {
            for (int jx = 0; jx < width; ++jx) {
                // The digraph is acyclic, where there is a downward edge from pixel(x, y) to pixels
                // (x - 1, y + 1), (x, y + 1) and (x + 1, y + 1)
                if (jx == 0) {
                    vse[ix][jx] = min(vse[ix - 1][jx], vse[ix - 1][jx + 1]) + gradients[jx][ix];

                } else if (jx == width - 1) {
                    vse[ix][jx] = min(vse[ix - 1][jx], vse[ix - 1][jx - 1]) + gradients[jx][ix];
                } else {
                    vse[ix][jx] = min(vse[ix - 1][jx - 1], vse[ix - 1][jx], vse[ix - 1][jx + 1])
                            + gradients[jx][ix];
                }
            }
        }
    }

    private void updateHorizontalSE() {
        for (int ix = 1; ix < width; ++ix) {
            for (int jx = 0; jx < height; ++jx) {
                // transposed graph, edge should be from (x, y) to (x + 1, y), (x + 1, y - 1), (x + 1, y + 1)
                if (jx == 0) {
                    hse[ix][jx] = min(hse[ix - 1][jx], hse[ix - 1][jx + 1]) + gradients[ix][jx];
                } else if (jx == height - 1) {
                    hse[ix][jx] = min(hse[ix - 1][jx], hse[ix - 1][jx - 1]) + gradients[ix][jx];
                } else {
                    hse[ix][jx] = min(hse[ix - 1][jx - 1], hse[ix - 1][jx], hse[ix - 1][jx + 1])
                            + gradients[ix][jx];
                }
            }
        }
    }

    private void updateGradient(int width, int height) {
        for (int ix = 1; ix < width - 1; ++ix) {
            for (int jx = 1; jx < height - 1; ++jx) {
                gradients[ix][jx] = dualGradient(ix, jx);
            }
        }
    }


    private double min(double a, double b) { return Math.min(a, b); }
    private double min(double a, double b, double c) {
        return Math.min(a, Math.min(b, c));
    }

    private void validateSeam(int[] seam, int dimBoundary, int pixelRange) {
        if (seam == null) {
            throw new IllegalArgumentException("Seam array is a null pointer!");
        }
        if (pixelRange <= 1) {
            throw new IllegalArgumentException("Width or height of picture is 1 thus can not be removed");
        }
        if (seam.length != dimBoundary) {
            throw new IllegalArgumentException("The length of seam array is not equal to width or height of picture!");
        }

        for (int ix = 0; ix < seam.length; ++ix) {
            if (seam[ix] >= pixelRange) {
                throw new IllegalArgumentException("Not a valid seam pixel which is out of boundary!");
            }
        }
        for (int ix = 1; ix < seam.length; ++ix) {
            if (Math.abs(seam[ix] - seam[ix - 1]) > 1) {
                throw new IllegalArgumentException("Not a valid seam which pixels are not connected!");
            }
        }
    }

    private double dualGradient(int x, int y) {
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return BORDER_ENERGY;
        }

        int x_gradient = gradient(picture.get(x -1 , y), picture.get(x + 1, y));
        int y_gradient = gradient(picture.get(x, y - 1), picture.get(x, y + 1));

        return Math.sqrt(x_gradient + y_gradient);
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
