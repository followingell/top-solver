package org.elliotpartridge;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Route stores information about and provides functionality related to routes comprised of Points.
 */
public class Route {

    private ArrayList<Point> points;
    private double totalDistance;
    private double totalScore;
    private boolean containsDuplicatePoints;

    // NonC stands for non-cumulative
    private ArrayList<Double> nonCPointScores;
    private ArrayList<Double> nonCInterPointTravelDistances;

    /**
     * Route constructor.
     *
     * @param points The ArrayList of Points by which to create the route from. The first point is
     *               the starting point and the final point is the ending point. If the starting and
     *               ending points are the same the point should be listed twice.
     */
    public Route(ArrayList<Point> points) {
        this.nonCInterPointTravelDistances = new ArrayList<>();
        this.nonCPointScores = new ArrayList<>();

        this.points = points;
        this.calculateAndSetTotalDistance();
        this.calculateAndSetTotalScore();
        this.calculateAndSetContainsDuplicatePoints();
    }

    /**
     * points getter.
     *
     * @return Points that make up a route.
     */
    public ArrayList<Point> getPoints() {
        return points;
    }

    /**
     * points setter. Note this also recalculates and sets totalDistance, totalScore &
     * containsDuplicatePoints values.
     *
     * @param points The ArrayList of Points which become the Route's new route.
     */
    public void setPoints(ArrayList<Point> points) {
        this.points = points;
        this.calculateAndSetTotalDistance();
        this.calculateAndSetTotalScore();
        this.calculateAndSetContainsDuplicatePoints();
    }

    /**
     * getPoint returns the point at pointIndex from the points ArrayList.
     *
     * @param pointIndex The index of the point to be returned from points.
     * @return Point at pointIndex from points.
     */
    public Point getPoint(int pointIndex) {
        return this.getPoints().get(pointIndex);
    }

    /**
     * setPoint replaces the Point at pointIndex within points with newPoint. Note this also
     * recalculates and sets totalDistance, totalScore & containsDuplicatePoints values.
     *
     * @param pointIndex The index of the point to replaced.
     * @param newPoint   The new Point to replace the point at pointIndex.
     */
    public void setPoint(int pointIndex, Point newPoint) {
        this.getPoints().set(pointIndex, newPoint);
        this.calculateAndSetTotalDistance();
        this.calculateAndSetTotalScore();
        this.calculateAndSetContainsDuplicatePoints();
    }

    /**
     * totalDistance getter.
     *
     * @return The total Euclidean distance of the Route.
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * calculateAndSetTotalDistance calculates and sets both the totalDistance and the
     * non-cumulative inter-point travel distances.
     */
    public void calculateAndSetTotalDistance() {
        double intraDistance;
        double cumulativeDistance = 0;
        int routeSize = this.getPoints().size();
        if (routeSize >= 2) {

            this.nonCInterPointTravelDistances = new ArrayList<>();
            this.getNonCInterPointTravelDistances()
                .add(0.); // adding start point distance which is always 0
            for (int i = 0, j = i + 1; j < routeSize; i++, j++) {
                intraDistance = Util.euclideanDistance(this.getPoint(i), this.getPoint(j));
                this.getNonCInterPointTravelDistances().add(j, intraDistance);
                cumulativeDistance += intraDistance;
            }
        }
        this.totalDistance = cumulativeDistance;
    }

    /**
     * totalScore getter.
     *
     * @return The total score of all of the Points visited in a Route.
     */
    public double getTotalScore() {
        return totalScore;
    }

    /**
     * calculateAndSetTotalScore calculates and sets both the totalScore and the non-cumulative
     * per-point scores.
     */
    public void calculateAndSetTotalScore() {
        double intraScore;
        double cumulativeScore = 0;
        int routeSize = this.getPoints().size();
        if (routeSize >= 2) {

            this.nonCPointScores = new ArrayList<>();
            for (int i = 0; i < routeSize; i++) {
                intraScore = this.getPoints().get(i).getScore();
                getNonCPointScores().add(i, intraScore);
                cumulativeScore += intraScore;
            }
        }
        this.totalScore = cumulativeScore;
    }

    /**
     * calculateAndSetContainsDuplicatePoints checks whether duplicate points exist with a route
     * setting containsDuplicatePoints accordingly.
     */
    public void calculateAndSetContainsDuplicatePoints() {
        this.containsDuplicatePoints =
            this.getPoints().stream().distinct().toArray().length != this.getPoints().size();
    }

    /**
     * containsDuplicatePoints getter.
     *
     * @return true if the Routes' points ArrayList contains duplicate Points, otherwise false.
     */
    public boolean getContainsDuplicatePoints() {
        return containsDuplicatePoints;
    }

    /**
     * nonCPointScores getter.
     *
     * @return ArrayList of scores where index 0 is the score associated with the Point at index 0
     * in points.
     */
    public ArrayList<Double> getNonCPointScores() {
        return nonCPointScores;
    }

    /**
     * getCumulativePointScore returns the cumulative score up to (inclusive) of a certain index
     * within points.
     *
     * @param pointIndex The index of the Point which you want to calculate the cumulative score up
     *                   to.
     * @return The cumulative score up to (inclusive) of pointIndex.
     * @throws IndexOutOfBoundsException Thrown to indicate that an index of some sort (such as to
     *                                   an array, to a string, or to a vector) is out of range.
     */
    public double getCumulativePointScore(int pointIndex) throws IndexOutOfBoundsException {
        if (pointIndex > getNonCPointScores().size() - 1 || pointIndex < 0) {
            throw new IndexOutOfBoundsException("pointIndex out of bounds");
        }

        double cumulativeScore = 0;
        for (int i = 0; i <= pointIndex; i++) {
            cumulativeScore += getNonCPointScores().get(i);
        }
        return cumulativeScore;
    }

    /**
     * nonCInterPointTravelDistances getter.
     *
     * @return An ArrayList of the non-cumulative inter-point travel distances e.g. the value at
     * index 1 is the travel distance between Point 0 to Point 1 and index 2 is the travel distance
     * between Point 1 to Point 2.
     */
    public ArrayList<Double> getNonCInterPointTravelDistances() {
        return nonCInterPointTravelDistances;
    }

    /**
     * getCumulativeTravelDistance returns the cumulative travel distance up to (inclusive) of a
     * certain index within points.
     *
     * @param pointIndex The index of the Point which you want to calculate the cumulative travel
     *                   distance up to.
     * @return The cumulative travel distance up to (inclusive) of pointIndex.
     * @throws IndexOutOfBoundsException Thrown to indicate that an index of some sort (such as to
     *                                   an array, to a string, or to a vector) is out of range.
     */
    public double getCumulativeTravelDistance(int pointIndex) throws IndexOutOfBoundsException {
        if (pointIndex > getNonCInterPointTravelDistances().size() - 1 || pointIndex < 0) {
            throw new IndexOutOfBoundsException("pointIndex out of bounds");
        }

        double cumulativeDistance = 0;
        for (int i = 0; i <= pointIndex; i++) {
            cumulativeDistance += getNonCInterPointTravelDistances().get(i);
        }
        return cumulativeDistance;
    }

    /**
     * ROUTE_DISTANCE_COMPARATOR is a Comparator that allows routes to be ordered based upon their
     * totalDistance.
     */
    public static Comparator<Route> ROUTE_DISTANCE_COMPARATOR = Comparator
        .comparing(Route::getTotalDistance);

    /**
     * ROUTE_SCORE_COMPARATOR is a Comparator that allows routes to be ordered based upon their
     * totalScore. If totalScores are identical the Route's totalDistance is then used to order.
     */
    public static Comparator<Route> ROUTE_SCORE_COMPARATOR = Comparator
        .comparing(Route::getTotalScore)
        .thenComparing(Route::getTotalDistance, Comparator.reverseOrder());
}
