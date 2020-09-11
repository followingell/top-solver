package org.elliotpartridge;

import ch.hsr.geohash.GeoHash;
import java.util.Comparator;

/**
 * Point stores information about and provides functionality related to TOP Format points.
 * <p>
 * Note Point utilises the external library, GeoHash (https://github.com/kungfoo/geohash-java,
 * Heuberger et al., 2008) which is made available under the Apache-2.0 license:
 * http://www.apache.org/licenses/LICENSE-2.0.
 */
public class Point {

    private final int id;
    private final double longitude; // x coordinate
    private final double latitude; // y coordinate
    private final double score;
    private final GeoHash ghRep;

    /**
     * Point constructor.
     *
     * @param id        Id of the point (starts at 1).
     * @param longitude Longitude (x coordinate) of the point.
     * @param latitude  Latitude (y coordinate) of the point.
     * @param score     The point's associated score.
     */
    public Point(int id, double longitude, double latitude, double score) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.score = score;
        this.ghRep = GeoHash.withCharacterPrecision(this.latitude, this.longitude, 12);
    }

    /**
     * id getter.
     *
     * @return Id of the point.
     */
    public int getId() {
        return id;
    }

    /**
     * longitude (x coordinate) getter.
     *
     * @return Longitude (x coordinate) of the point.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * latitude (y coordinate) getter.
     *
     * @return Latitude (y coordinate) of the point.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * score getter.
     *
     * @return The point's associated score.
     */
    public double getScore() {
        return score;
    }

    /**
     * ghRep getter.
     *
     * @return A GeoHash representation of the point.
     */
    public GeoHash getGhRep() {
        return ghRep;
    }

    /**
     * createComparator creates a Comparator that can be used to order points based upon their
     * distance from a specified Point, p. Code adapted from (https://stackoverflow.com/a/22683665,
     * Marco13, 2014)
     *
     * @param p Point by which other points are ordered against.
     * @return Point Comparator.
     */
    public static Comparator<Point> createComparator(Point p) {
        final Point finalP = new Point(p.getId(), p.getLongitude(), p.getLatitude(), p.getScore());
        return (p0, p1) -> {
            double ds0 = Util.euclideanDistance(p0, finalP);
            double ds1 = Util.euclideanDistance(p1, finalP);
            return Double.compare(ds0, ds1);
        };
    }

    /**
     * POINT_GH_COMPARATOR is a Comparator that utilises GeoHash's GeoHash representation to order
     * Points i.e. points that are closer together will be closer together within a data structure.
     */
    public static Comparator<Point> GH_COMPARATOR = Comparator.comparing(Point::getGhRep)
        .thenComparing(Point::getGhRep);

    public static Comparator<Point> SCORE_COMPARATOR = Comparator.comparing(Point::getScore)
        .thenComparing(Point::getScore);

}
