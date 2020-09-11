package org.elliotpartridge;

/**
 * Util is a utility class housing functionality unrelated to other classes.
 */
public class Util {

    /**
     * euclideanDistance calculates the Euclidean distance (https://en.wikipedia.org/wiki/Euclidean_distance#Two_dimensions,
     * Wikipedia, n.d.) between Points p1 and p2.
     *
     * @param p1 Starting Point.
     * @param p2 Ending Point.
     * @return The Euclidean distance between Points p1 and p2.
     */
    public static double euclideanDistance(Point p1, Point p2) {
        return Math.sqrt((Math.pow(p2.getLongitude() - p1.getLongitude(), 2) + Math
            .pow(p2.getLatitude() - p1.getLatitude(), 2)));
    }

    /**
     * normaliseBetweenRange normalises a value between newMinVal and newMaxVal. Code adapted from
     * (https://stackoverflow.com/a/55212064, Andreas Myriounis, 2019)
     *
     * @param val       The current value within the current range.
     * @param minVal    The minimum value of the current range.
     * @param maxVal    The maximum value of the current range.
     * @param newMinVal The new minimum value of the new adapted range.
     * @param newMaxVal The new maximum value of the new adapted range.
     * @return val normalised between newMinVal and newMaxVal.
     */
    public static double normaliseBetweenRange(double val, double minVal, double maxVal,
        double newMinVal, double newMaxVal) {
        return newMinVal + (val - minVal) * (newMaxVal - newMinVal) / (maxVal - minVal);
    }
}
