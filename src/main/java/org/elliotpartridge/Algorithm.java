package org.elliotpartridge;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Algorithm stores information about and provides functionality related to utilising an Algorithm
 * which solves TOP problems.
 * <p>
 * Note Algorithm utilises the external library, Guava (https://github.com/google/guava, Google,
 * 2011) which is made available under the Apache-2.0 license: http://www.apache.org/licenses/LICENSE-2.0.
 */
public class Algorithm {

    private final TopData dataset;
    private final int popSize;
    private final int tourTriesMax;
    private final double pCrossover;
    private final double pMutate;
    private final double elitistReplacementPercent;
    private final int maxGenerations;

    private final Point startingPoint;
    private final Point endingPoint;
    private final int nRoutes;
    private final double tMax;
    private final ArrayList<Route> finalRoutes;
    private ArrayList<Point> gHSortedPopRef;
    private List<List<Point>> distanceGroupedPoints;
    private int nRoutesCounter;
    private ArrayList<Route> routePopulation;
    private final Random rand;

    private int generationCounter;
    private ArrayList<Route> selectedParents;
    private ArrayList<Route> retainedChildren;
    private ArrayList<Route> childPopulation;

    /**
     * Algorithm constructor.
     *
     * @param dataset                   The dataset the algorithm will work on.
     * @param popSize                   The size of the population.
     * @param tourTriesMax              The maximum number of unsuccessful tries (per-route) to
     *                                  improve the route within the initialisePopulation phase.
     * @param pCrossover                The probability of crossover occurring.
     * @param pMutate                   The probability of mutation occurring.
     * @param elitistReplacementPercent The percentage of the population that is retained from the
     *                                  previous generation.
     * @param maxGenerations            The maximum number of generations to complete per route.
     */
    public Algorithm(TopData dataset, int popSize, int tourTriesMax,
        double pCrossover, double pMutate,
        double elitistReplacementPercent,
        int maxGenerations) {
        this.dataset = dataset;
        this.popSize = popSize;
        this.tourTriesMax = tourTriesMax;
        this.pCrossover = pCrossover;
        this.pMutate = pMutate;
        this.elitistReplacementPercent = elitistReplacementPercent;
        this.maxGenerations = maxGenerations;

        this.startingPoint = dataset.getPoints().get(0);
        this.endingPoint = dataset.getPoints().get(dataset.getPoints().size() - 1);
        this.nRoutes = dataset.getnRoutes();
        this.tMax = dataset.gettMax();
        this.finalRoutes = new ArrayList<>();

        // build ghSortedPopRef correctly
        this.getDataset().removeTOPStartingAndEndingPoints();
        ArrayList<Point> tmp = new ArrayList<>(getDataset().getPoints());
        tmp.sort(Point.GH_COMPARATOR);
        this.gHSortedPopRef = new ArrayList<>(tmp);

        this.distanceGroupedPoints = Lists
            .partition(getgHSortedPopRef(), getgHSortedPopRef().size() / 10);
        this.nRoutesCounter = 0;
        this.routePopulation = new ArrayList<>();
        this.rand = new Random();

        this.generationCounter = 0;
        this.selectedParents = new ArrayList<>();
        this.retainedChildren = new ArrayList<>();
        this.childPopulation = new ArrayList<>();
    }

    /**
     * dataset getter.
     *
     * @return The dataset that the Algorithm is set to work on.
     */
    public TopData getDataset() {
        return dataset;
    }

    /**
     * gHSortedPopRef getter.
     *
     * @return An ArrayList of Points from dataset sorted by Point.GH_COMPARATOR.
     */
    public ArrayList<Point> getgHSortedPopRef() {
        return gHSortedPopRef;
    }

    /**
     * gHSortedPopRef setter.
     *
     * @param gHSortedPopRef An ArrayList of Points.
     */
    public void setgHSortedPopRef(ArrayList<Point> gHSortedPopRef) {
        this.gHSortedPopRef = gHSortedPopRef;
    }

    /**
     * nRoutesCounter getter.
     *
     * @return The number of Routes that have been created when getnRoutesCounter is called.
     */
    public int getnRoutesCounter() {
        return nRoutesCounter;
    }

    /**
     * nRoutesCounter setter.
     *
     * @param nRoutesCounter The number of Routes that have been created.
     */
    public void setnRoutesCounter(int nRoutesCounter) {
        this.nRoutesCounter = nRoutesCounter;
    }

    /**
     * nRoutes getter.
     *
     * @return The number of Routes to be produced by Algorithm (as specified by TopData).
     */
    public int getnRoutes() {
        return nRoutes;
    }

    /**
     * popSize getter.
     *
     * @return The population (of Routes) size.
     */
    public int getPopSize() {
        return popSize;
    }

    /**
     * startingPoint getter.
     *
     * @return The starting point for the Routes to be generated by Algorithm (as specified by
     * TopData).
     */
    public Point getStartingPoint() {
        return startingPoint;
    }

    /**
     * endingPoint getter.
     *
     * @return The ending point for the Routes to be generated by Algorithm (as specified by
     * TopData).
     */
    public Point getEndingPoint() {
        return endingPoint;
    }

    /**
     * tMax getter.
     *
     * @return The maximum per-route travel time.
     */
    public double gettMax() {
        return tMax;
    }

    /**
     * tourTriesMax getter.
     *
     * @return The maximum number of unsuccessful tries (per-route) to improve the route within the
     * initialisePopulation phase.
     */
    public int getTourTriesMax() {
        return tourTriesMax;
    }

    /**
     * rand getter.
     *
     * @return The Random instance associated with the Algorithm.
     */
    public Random getRand() {
        return rand;
    }

    /**
     * getDistanceGroupedPoints getter.
     *
     * @return A List of Lists containing points grouped together based upon POINT.GH_COMPARATOR.
     */
    public List<List<Point>> getDistanceGroupedPoints() {
        return distanceGroupedPoints;
    }

    /**
     * getDistanceGroupedPoints setter.
     *
     * @param distanceGroupedPoints A List of Lists containing points grouped together based upon
     *                              POINT.GH_COMPARATOR.
     */
    public void setDistanceGroupedPoints(List<List<Point>> distanceGroupedPoints) {
        this.distanceGroupedPoints = distanceGroupedPoints;
    }

    /**
     * routePopulation getter.
     *
     * @return ArrayList of Routes that make up the population.
     */
    public ArrayList<Route> getRoutePopulation() {
        return routePopulation;
    }

    /**
     * routePopulation setter.
     *
     * @param routePopulation ArrayList of Routes that make up the population.
     */
    public void setRoutePopulation(ArrayList<Route> routePopulation) {
        this.routePopulation = routePopulation;
    }

    /**
     * maxGenerations getter.
     *
     * @return The maximum number of generations to complete per route.
     */
    public int getMaxGenerations() {
        return maxGenerations;
    }

    /**
     * generationCounter getter.
     *
     * @return The current generation number.
     */
    public int getGenerationCounter() {
        return generationCounter;
    }

    /**
     * generationCounter setter.
     *
     * @param generationCounter The number to set generationCounter to.
     */
    public void setGenerationCounter(int generationCounter) {
        this.generationCounter = generationCounter;
    }

    /**
     * retainedChildren getter.
     *
     * @return ArrayList of children (Routes).
     */
    public ArrayList<Route> getRetainedChildren() {
        return retainedChildren;
    }

    /**
     * retainedChildren setter.
     *
     * @param retainedChildren ArrayList of children (Routes).
     */
    public void setRetainedChildren(ArrayList<Route> retainedChildren) {
        this.retainedChildren = retainedChildren;
    }

    /**
     * selectedParents getter.
     *
     * @return ArrayList of Routes selected (for crossover).
     */
    public ArrayList<Route> getSelectedParents() {
        return selectedParents;
    }

    /**
     * selectedParents setter.
     *
     * @param selectedParents ArrayList of Routes selected (for crossover).
     */
    public void setSelectedParents(ArrayList<Route> selectedParents) {
        this.selectedParents = selectedParents;
    }

    /**
     * childPopulation getter.
     *
     * @return ArrayList of Routes produced from crossover.
     */
    public ArrayList<Route> getChildPopulation() {
        return childPopulation;
    }

    /**
     * childPopulation setter.
     *
     * @param childPopulation ArrayList of Routes produced from crossover.
     */
    public void setChildPopulation(ArrayList<Route> childPopulation) {
        this.childPopulation = childPopulation;
    }

    /**
     * pCrossover getter.
     *
     * @return The probability of crossover occurring.
     */
    public double getpCrossover() {
        return pCrossover;
    }

    /**
     * pMutate getter.
     *
     * @return The probability of mutation occurring.
     */
    public double getpMutate() {
        return pMutate;
    }

    /**
     * elitistReplacementPercent getter.
     *
     * @return The percentage of the population that is retained from the previous generation.
     */
    public double getElitistReplacementPercent() {
        return elitistReplacementPercent;
    }

    /**
     * finalRoutes getter.
     *
     * @return The final, optimised routes generated by Algorithm.
     */
    public ArrayList<Route> getFinalRoutes() {
        return finalRoutes;
    }

    /**
     * lowestAdditionalDistanceIndex calculates at which index a point can be added to a Route
     * whilst minimising the additional distance added. If points already contains the Point or if
     * the Route is invalid (< 2 Points (no starting/ending)) then a -1 is returned.
     *
     * @param pointToBeInserted the Point to be added to points.
     * @param points            The ArrayList of Points which pointToBeInserted is to be added to.
     * @return Index where pointToBeInserted should be placed in points to minimise additional Route
     * distance. -1 If points already contains the Point or if the Route is invalid.
     */
    public int lowestAdditionalDistanceIndex(Point pointToBeInserted, ArrayList<Point> points) {
        if (points.contains(pointToBeInserted) || points.size() < 2) {
            return -1;
        } else if (points.size() == 2) {
            return 1;
        } else {
            int addIndex = 1;
            double smallestMultiPointDistance = Double.MAX_VALUE;
            // loop through array testing where it can fit that isn't start and end
            for (int i = 0, j = i + 1; j < points.size(); i++, j++) {
                double multiPointDistance = Util
                    .euclideanDistance(points.get(i), pointToBeInserted);
                multiPointDistance += Util.euclideanDistance(pointToBeInserted, points.get(j));

                if (multiPointDistance < smallestMultiPointDistance) {
                    smallestMultiPointDistance = multiPointDistance;
                    addIndex = i + 1;
                }
            }
            return addIndex;
        }
    }

    /**
     * initialisePopulation generates the initial population to be used as part of the
     * generateRoutesFromTOPFile method.
     */
    public void initialisePopulation() {

        ArrayList<Point> popRef = getDataset().getPoints();
        popRef.remove(getStartingPoint());
        popRef.remove(getEndingPoint());

        // remove unreachable points
        popRef.removeIf(p -> (Util.euclideanDistance(getStartingPoint(), p) > gettMax()));

        // partition Points into groups that are close together based upon Point.GH_COMPARATOR
        setgHSortedPopRef(new ArrayList<>(popRef));
        getgHSortedPopRef().sort(Point.GH_COMPARATOR);
        setDistanceGroupedPoints(Lists.partition(getgHSortedPopRef(),
            getgHSortedPopRef().size() / Math.min(10, getgHSortedPopRef().size())));

        // sorting possible points in descending order based upon score & take top 10 points
        ArrayList<Point> topScorePoints = new ArrayList<>(popRef);
        topScorePoints.sort(Collections.reverseOrder(Point.SCORE_COMPARATOR));
        topScorePoints.subList(Math.min(10, topScorePoints.size()), topScorePoints.size()).clear();

        setRoutePopulation(new ArrayList<>());
        int popCounter = 0;

        while (popCounter < getPopSize()) {
            Route route = new Route(new ArrayList<>() {{
                add(getStartingPoint());
                add(getEndingPoint());
            }});
            int tourTries = 0;
            while (route.getTotalDistance() < gettMax() && tourTries < getTourTriesMax()) {
                reselectPoint:
                // 20% chance the Point that will be tried to be added is a top-scoring Point
                if (getRand().nextDouble() > 0.2) {
                    List<Point> selectedPointGroup = getDistanceGroupedPoints()
                        .get(getRand().nextInt(getDistanceGroupedPoints().size()));
                    Point selectedPoint = selectedPointGroup
                        .get(getRand().nextInt(selectedPointGroup.size()));
                    if (route.getPoints().contains(selectedPoint)) {
                        tourTries++;
                        break reselectPoint;
                    } else {
                        ArrayList<Point> newPoints = new ArrayList<>(route.getPoints());
                        newPoints.add(lowestAdditionalDistanceIndex(selectedPoint, newPoints),
                            selectedPoint);
                        Route potentialRoute = new Route(newPoints);
                        if (potentialRoute.getTotalDistance() > gettMax()) {
                            tourTries++;
                        } else {
                            route.setPoints(newPoints);
                            tourTries = 0;
                        }
                    }
                } else {
                    Point selectedTopScorePoint = topScorePoints
                        .get(getRand().nextInt(topScorePoints.size()));
                    if (route.getPoints().contains(selectedTopScorePoint)) {
                        tourTries++;
                        break reselectPoint;
                    } else {
                        ArrayList<Point> newPoints = new ArrayList<>(route.getPoints());
                        newPoints
                            .add(lowestAdditionalDistanceIndex(selectedTopScorePoint, newPoints),
                                selectedTopScorePoint);
                        Route potentialRoute = new Route(newPoints);
                        if (potentialRoute.getTotalDistance() > gettMax()) {
                            tourTries++;
                        } else {
                            route.setPoints(newPoints);
                            tourTries = 0;
                        }
                    }
                }
            }
            getRoutePopulation().add(route);
            popCounter++;
        }
    }

    /**
     * tournamentSelection generates a parent population through a tournament selection process
     * whereby the strongest parent of a randomly selected group is added to the parent population
     * until the popSize is met.
     */
    public void tournamentSelection() {
        int nParentsCounter = 0;
        while (nParentsCounter < getPopSize()) {
            ArrayList<Route> selectedForTournament = new ArrayList<>();
            while (selectedForTournament.size() < 2) {
                if (getGenerationCounter() == 0) {
                    selectedForTournament.add(
                        getRoutePopulation().get(getRand().nextInt(getRoutePopulation().size())));
                } else {
                    selectedForTournament.add(
                        getRetainedChildren().get(getRand().nextInt(getRetainedChildren().size())));
                }
            }
            selectedForTournament.sort(Collections.reverseOrder(Route.ROUTE_SCORE_COMPARATOR));
            getSelectedParents().add(selectedForTournament.get(0));
            nParentsCounter++;
        }
        getRetainedChildren().clear();
    }

    /**
     * removeWorstDuplicatePoints removes all instances of duplicate Points from a Route by removing
     * the single instance of the duplicate Point that reduces the totalDistance the least.
     *
     * @param route The Route which to remove duplicate Points from.
     */
    public void removeWorstDuplicatePoints(Route route) {

        // duplicates code taken from: (https://stackoverflow.com/a/31341963, RobAu, 2015)
        List<Point> duplicates = route.getPoints().stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream().filter(p -> p.getValue() > 1)
            .map(Entry::getKey)
            .collect(Collectors.toList());

        // while the route contains duplicates remove the instance of the duplicate Point that reduces
        // the Route distance the least.
        while (route.getContainsDuplicatePoints()) {
            for (Point point : duplicates) {
                int firstIndex = route.getPoints().indexOf(point);
                int secondIndex = route.getPoints().lastIndexOf(point);

                ArrayList<Point> firstRoutePoints = new ArrayList<>(route.getPoints());
                firstRoutePoints.remove(firstIndex);
                Route firstRoute = new Route(firstRoutePoints);

                ArrayList<Point> secondRoutePoints = new ArrayList<>(route.getPoints());
                secondRoutePoints.remove(secondIndex);
                Route secondRoute = new Route(secondRoutePoints);

                if (firstRoute.getTotalDistance() < secondRoute.getTotalDistance()) {
                    route.setPoints(firstRoute.getPoints());
                } else if (secondRoute.getTotalDistance() < firstRoute.getTotalDistance()) {
                    route.setPoints(secondRoute.getPoints());
                } else {
                    route.setPoints(firstRoute.getPoints());
                }
            }
        }
    }

    /**
     * If two parents are selected, singlePointCrossover will look for the first Point that exists
     * in both Route's points and swap the heads and tails of these routes to produce offspring. If
     * these new children are valid they are added to child population. If not their parent is added
     * instead.
     */
    public void singlePointCrossover() {
        ArrayList<Route> selectedParentsForRecombination = new ArrayList<>();
        setChildPopulation(new ArrayList<>());
        // loop through all parents
        for (Route route : getSelectedParents()) {
            if (getRand().nextDouble() <= getpCrossover()) {
                selectedParentsForRecombination.add(route);
                if (selectedParentsForRecombination.size() == 2) {
                    Route parent1 = selectedParentsForRecombination.get(0);
                    Route parent2 = selectedParentsForRecombination.get(1);
                    for (int i = 1; i < parent1.getPoints().size() - 1; i++) {
                        if (parent2.getPoints().contains(parent1.getPoints().get(i))) {
                            int parent2CommonGeneIndex = parent2.getPoints()
                                .indexOf(parent1.getPoints().get(i));

                            List<Point> parent1Head = new ArrayList<>(
                                parent1.getPoints().subList(0, i));
                            List<Point> parent1Tail = new ArrayList<>(
                                parent1.getPoints().subList(i, parent1.getPoints().size()));
                            List<Point> parent2Head = new ArrayList<>(
                                parent2.getPoints().subList(0, parent2CommonGeneIndex));
                            List<Point> parent2Tail = new ArrayList<>(parent2.getPoints()
                                .subList(parent2CommonGeneIndex, parent2.getPoints().size()));

                            ArrayList<Point> child1Points = new ArrayList<>();
                            child1Points.addAll(parent1Head);
                            child1Points.addAll(parent2Tail);

                            ArrayList<Point> child2Points = new ArrayList<>();
                            child2Points.addAll(parent2Head);
                            child2Points.addAll(parent1Tail);

                            Route child1 = new Route(child1Points);
                            Route child2 = new Route(child2Points);

                            if (child1.getContainsDuplicatePoints()) {
                                removeWorstDuplicatePoints(child1);
                            }

                            if (child2.getContainsDuplicatePoints()) {
                                removeWorstDuplicatePoints(child2);
                            }

                            if (child1.getTotalDistance() <= gettMax()) {
                                getChildPopulation().add(child1);
                            } else {
                                getChildPopulation().add(parent1);
                            }

                            if (child2.getTotalDistance() <= gettMax()) {
                                getChildPopulation().add(child2);
                            } else {
                                getChildPopulation().add(parent2);
                            }
                            selectedParentsForRecombination.clear();
                            break;
                        } else if (i == parent1.getPoints().size() - 2 && !parent2.getPoints()
                            .contains(parent1.getPoints().get(i))) {
                            getChildPopulation().add(parent1);
                            getChildPopulation().add(parent2);
                            selectedParentsForRecombination.clear();
                        }
                    }
                }
            } else {
                getChildPopulation().add(route);
            }
        }
        if (!selectedParentsForRecombination.isEmpty()
            && getChildPopulation().size() != getPopSize()) {
            int numberOfChildrenToAdd = getPopSize() - getChildPopulation().size();
            if (selectedParentsForRecombination.size() > numberOfChildrenToAdd) {
                for (int i = 0; i < numberOfChildrenToAdd; i++) {
                    getChildPopulation().add(selectedParentsForRecombination.get(i));
                }
            } else {
                getChildPopulation().addAll(selectedParentsForRecombination);
            }
        }
        selectedParentsForRecombination.clear();
    }

    /**
     * dropWorstTravelScoreRatioPoint removes the Point from the Route that has the worst score to
     * travel ratio provided that it is valid to do so e.g. not removing a starting/ending Point.
     *
     * @param route The Route from which to remove the worst score to travel ratio point.
     */
    public void dropWorstTravelScoreRatioPoint(Route route) {
        // check route does not just consist of starting & ending nodes since they cannot be deleted
        if (route.getPoints().size() > 2) {
            double travelScoreRatio = Double.MAX_VALUE;
            int pointToDropIndex = 0;
            // i = 1 & < size()-1 prevents the starting and ending Points from being considered
            for (int i = 1; i < route.getPoints().size() - 1; i++) {
                if (i != route.getPoints().size() - 1) {
                    double tmpTravelScoreRatio = route.getPoints().get(i).getScore() / route
                        .getNonCInterPointTravelDistances().get(i);
                    if (tmpTravelScoreRatio < travelScoreRatio) {
                        travelScoreRatio = tmpTravelScoreRatio;
                        pointToDropIndex = i;
                    }
                }
            }
            if (pointToDropIndex != 0 && pointToDropIndex != route.getPoints().size() - 1) {
                route.getPoints().remove(pointToDropIndex);
                route.setPoints(route.getPoints());
            }
        }
    }

    /**
     * addOrReplace tries to add a randomly selected Point that isn't already in the Route to the
     * Route. If this is not possible i.e. this causes the Route's totalDistance to exceed tMax the
     * worst travel-score-ratio points are removed whilst the addition of the new point remains net
     * positive for totalScore and possible in terms of totalDistance. If this is not possible the
     * Route remains unchanged.
     *
     * @param route The route to try and add a Point to.
     */
    public void addOrReplace(Route route) {
        // try to find point that isn't in route
        int notInRoutePointTryCounter = 0;
        while (notInRoutePointTryCounter < 10) {
            List<Point> selectedGroup = getDistanceGroupedPoints()
                .get(getRand().nextInt(getDistanceGroupedPoints().size()));
            Point potentialPoint = selectedGroup.get(getRand().nextInt(selectedGroup.size()));

            if (route.getPoints().contains(potentialPoint)) {
                notInRoutePointTryCounter++;
            } else {
                // check if point can be added without removal
                ArrayList<Point> potentialPoints = new ArrayList<>(route.getPoints());
                potentialPoints
                    .add(lowestAdditionalDistanceIndex(potentialPoint, route.getPoints()),
                        potentialPoint);
                Route potentialRoute = new Route(new ArrayList<>(potentialPoints));
                if (potentialRoute.getTotalDistance() <= gettMax()) {
                    route.setPoints(potentialPoints);
                    // if not remove nodes until enough space exists.
                } else {
                    potentialRoute.setPoints(new ArrayList<>(route.getPoints()));
                    double potAddtScore = potentialPoint.getScore();
                    double previousScore = route.getTotalScore();
                    while (potentialRoute.getTotalScore() + potAddtScore >= previousScore
                        && !potentialRoute.getPoints().contains(potentialPoint)
                        && potentialRoute.getPoints().size() > 2) {
                        dropWorstTravelScoreRatioPoint(potentialRoute);
                        ArrayList<Point> pointsWithoutPotentialPoint = new ArrayList<>(
                            potentialRoute.getPoints());
                        ArrayList<Point> pointsWithPotentialPoint = new ArrayList<>(
                            potentialRoute.getPoints());
                        pointsWithPotentialPoint.add(
                            lowestAdditionalDistanceIndex(potentialPoint, pointsWithPotentialPoint),
                            potentialPoint);
                        potentialRoute.setPoints(pointsWithPotentialPoint);
                        if (potentialRoute.getTotalDistance() > gettMax()) {
                            potentialRoute.setPoints(pointsWithoutPotentialPoint);
                        }
                    }

                    // check if better
                    if (potentialRoute.getTotalScore() > route.getTotalScore() ||
                        (potentialRoute.getTotalScore() == route.getTotalScore()
                            && potentialRoute.getTotalDistance() < route.getTotalDistance())) {
                        route.setPoints(new ArrayList<>(potentialRoute.getPoints()));
                    }
                    notInRoutePointTryCounter = 10;
                }
            }
        }
    }

    /**
     * addRandomPointMinAddtDistLocIfValid tries to add a randomly selected Point to the Route in
     * the position that adds the least additional travel time provided that it's valid to do so.
     *
     * @param route The Route to add the randomly selected Point to.
     */
    public void addRandomPointMinAddtDistLocIfValid(Route route) {
        List<Point> selectedGroup = getDistanceGroupedPoints()
            .get(getRand().nextInt(getDistanceGroupedPoints().size()));
        Point potentialPoint = selectedGroup.get(getRand().nextInt(selectedGroup.size()));

        if (!route.getPoints().contains(potentialPoint)) {
            ArrayList<Point> newPoints = new ArrayList<>();
            if (route.getPoints().size() > 2) {

                int addIndex = 0;
                double smallestMultiPointDistance = Double.MAX_VALUE;
                // loop through array testing where it can lie that isn't start and end
                for (int i = 0, j = i + 1; j < route.getPoints().size(); i++, j++) {
                    double multiPointDistance = Util
                        .euclideanDistance(route.getPoint(i), potentialPoint);
                    multiPointDistance += Util.euclideanDistance(potentialPoint, route.getPoint(j));

                    if (multiPointDistance < smallestMultiPointDistance) {
                        smallestMultiPointDistance = multiPointDistance;
                        addIndex = i + 1;
                    }
                }
                newPoints.addAll(route.getPoints());
                newPoints.add(addIndex, potentialPoint);
            } else {
                newPoints.add(route.getPoint(0));
                newPoints.add(potentialPoint);
                newPoints.add(route.getPoint(route.getPoints().size() - 1));
            }
            Route potentialRoute = new Route(newPoints);

            if (potentialRoute.getTotalDistance() <= gettMax() && !potentialRoute
                .getContainsDuplicatePoints()) {
                route.setPoints(new ArrayList<>(newPoints));
            }
        }
    }

    /**
     * iterativeLocalSearch tries to alter the route by replacing a random number of Points by
     * searching for better, close points within the GeoHash sorted Array of points.
     *
     * @param route           The Route which to perform iterative local search on.
     * @param localSearchIter The maximum number of Points to check either side of the original
     *                        Point.
     */
    public void iterativeLocalSearch(Route route, int localSearchIter) {
        if (localSearchIter < 1) {
            throw new IllegalArgumentException("localSearchIter cannot be less than 1.");
        }

        if (route.getPoints().size() > 2) {
            // select random number of points to try and change
            int numPointsToChange = getRand().nextInt(route.getPoints().size() - 1);
            if (numPointsToChange == 0) {
                while (numPointsToChange == 0) {
                    numPointsToChange = getRand().nextInt(route.getPoints().size() - 1);
                }
            }

            int numPointsToChangeCounter = 0;
            Set<Integer> changedPointIndexes = new HashSet<>();
            while (numPointsToChangeCounter < numPointsToChange) {
                int pointChangeIndex = getRand().nextInt(route.getPoints().size() - 1);
                if (pointChangeIndex != 0 && pointChangeIndex != route.getPoints().size() - 1
                    && !changedPointIndexes.contains(pointChangeIndex)) {
                    changedPointIndexes.add(pointChangeIndex);

                    Point prevPoint = route.getPoint(pointChangeIndex);
                    int prevPointGhSortedPopRefIndex = getgHSortedPopRef().indexOf(prevPoint);

                    localSearchIter = -localSearchIter;
                    int localSearchIterUpperBound = Math.abs(localSearchIter);
                    ArrayList<Route> potentialRoutes = new ArrayList<>();

                    while (localSearchIter < localSearchIterUpperBound) {
                        if (prevPointGhSortedPopRefIndex + localSearchIter >= 0
                            && prevPointGhSortedPopRefIndex + localSearchIter < getgHSortedPopRef()
                            .size() && localSearchIter != 0) {
                            Point potentialPoint = getgHSortedPopRef()
                                .get(prevPointGhSortedPopRefIndex + localSearchIter);
                            ArrayList<Point> newPoints = new ArrayList<>(route.getPoints());
                            newPoints.set(pointChangeIndex, potentialPoint);
                            Route potentialRoute = new Route(newPoints);

                            if ((potentialRoute.getTotalScore() > route.getTotalScore()
                                && potentialRoute.getTotalDistance() <= gettMax() && !potentialRoute
                                .getContainsDuplicatePoints())
                                || (potentialRoute.getTotalScore() == route.getTotalScore()
                                && potentialRoute.getTotalDistance() <= gettMax() && !potentialRoute
                                .getContainsDuplicatePoints()
                                && potentialRoute.getTotalDistance() < route.getTotalDistance())) {
                                potentialRoutes.add(potentialRoute);
                            }
                        }
                        localSearchIter++;
                    }
                    if (!potentialRoutes.isEmpty()) {
                        potentialRoutes
                            .sort(Collections.reverseOrder(Route.ROUTE_SCORE_COMPARATOR));
                        route.setPoints(potentialRoutes.get(0).getPoints());
                    }
                    numPointsToChangeCounter++;
                }
            }
        }
    }

    /**
     * SingularTwoOpt takes a Route and alters the ordering of Points within the Route to prevent
     * the Route from crossing over itself. This method is used within completeTwoOpt.
     * <p>
     * Note, code adapted from: (https://en.wikipedia.org/wiki/2-opt, Wikipedia, n.d.).
     *
     * @param route The Route to perform singularTwoOpt upon.
     * @param i     The index from which to start the reveredPoints list from.
     * @param j     The (inclusive) index from which to end the reveredPoints list at.
     * @return The Route with the 2-Opt performed.
     */
    public Route singularTwoOpt(Route route, int i, int j) {
        ArrayList<Point> initialRetainedPoints = new ArrayList<>(route.getPoints().subList(0, i));
        ArrayList<Point> reversedPoints = new ArrayList<>(route.getPoints().subList(i, j + 1));
        Collections.reverse(reversedPoints);
        ArrayList<Point> endRetainedPoints = new ArrayList<>(
            route.getPoints().subList(j + 1, route.getPoints().size()));

        ArrayList<Point> newPoints = new ArrayList<>();
        newPoints.addAll(initialRetainedPoints);
        newPoints.addAll(reversedPoints);
        newPoints.addAll(endRetainedPoints);

        return new Route(newPoints);
    }

    /**
     * completeTwoOpt takes a Route and alters the ordering of all Points within the Route to
     * prevent the Route from crossing over itself.
     * <p>
     * Note, code adapted from: (https://en.wikipedia.org/wiki/2-opt, Wikipedia, n.d.).
     *
     * @param route The Route to perform completeTwoOpt upon.
     */
    public void completeTwoOpt(Route route) {
        double routeSize = route.getPoints().size();
        double bestDistance = route.getTotalDistance();
        for (int i = 1; i <= routeSize - 3; i++) {
            for (int j = i + 1; j <= routeSize - 2; j++) {
                Route newRoute = singularTwoOpt(route, i, j);
                double newDistance = newRoute.getTotalDistance();
                if (newDistance < bestDistance) {
                    route.setPoints(newRoute.getPoints());
                    bestDistance = newDistance;
                }
            }
        }
    }

    /**
     * elitistReplacement replaces tpgPercent of the worst scored child population Routes with the
     * best of the parent Routes.
     *
     * @param tpgPercent The percentage of children to replace e.g. 3% as 0.03.
     */
    public void elitistReplacement(double tpgPercent) {
        getSelectedParents().sort(Collections.reverseOrder(Route.ROUTE_SCORE_COMPARATOR));
        getChildPopulation().sort(Route.ROUTE_SCORE_COMPARATOR);

        int numOfSelectedParentsToRetain = (int) Math.floor(getPopSize() * tpgPercent);
        int numOfSelectedParentsToRetainCounter = 0;
        while (numOfSelectedParentsToRetainCounter < numOfSelectedParentsToRetain) {
            getChildPopulation().set(numOfSelectedParentsToRetainCounter,
                getSelectedParents().get(numOfSelectedParentsToRetainCounter));
            numOfSelectedParentsToRetainCounter++;
        }

        getSelectedParents().clear();
        getRetainedChildren().addAll(getChildPopulation());
    }

    /**
     * rearrange looks at the Points within each Route in routes and moves them to another Route if
     * they reduce the overall total distance travelled.
     *
     * @param routes The ArrayList of Routes which to apply rearrange to.
     */
    public void rearrange(ArrayList<Route> routes) {

        // loop through all routes
        for (int i = 0; i < routes.size(); i++) {
            for (int j = 0; j < routes.size(); j++) {

                // check routes aren't the same route
                if (!routes.get(i).equals(routes.get(j))) {

                    // route information
                    Route route1 = new Route(new ArrayList<>(routes.get(i).getPoints()));
                    ArrayList<Point> route1Points = new ArrayList<>(routes.get(i).getPoints());
                    Route route2 = new Route(new ArrayList<>(routes.get(j).getPoints()));
                    ArrayList<Point> route2Points = new ArrayList<>(routes.get(j).getPoints());

                    // loop through points in first route
                    for (int k = 1; k < routes.get(i).getPoints().size() - 1; k++) {
                        Point route1Point = routes.get(i).getPoints().get(k);
                        route1Points.remove(k);
                        Route tmpRoute1 = new Route(new ArrayList<>(route1Points));

                        // this is needed to prevent errors on valid TOP files with insufficient tMax to achieve set no. of routes
                        int bestValidIndex = lowestAdditionalDistanceIndex(route1Point,
                            route2Points);
                        if (bestValidIndex != -1) {
                            route2Points.add(bestValidIndex, route1Point);
                        }
                        Route tmpRoute2 = new Route(new ArrayList<>(route2Points));

                        boolean validRoute = tmpRoute2.getTotalDistance() <= gettMax();
                        boolean distanceReduced =
                            tmpRoute1.getTotalDistance() + tmpRoute2.getTotalDistance()
                                < route1.getTotalDistance() + route2.getTotalDistance();
                        boolean sameNoOfPoints =
                            (route1.getPoints().size() + route2.getPoints().size()) == (
                                tmpRoute1.getPoints().size() + tmpRoute2.getPoints().size());
                        if (validRoute && sameNoOfPoints && distanceReduced) {
                            routes.get(i).setPoints(new ArrayList<>(route1Points));
                            route1.setPoints(new ArrayList<>(route1Points));
                            routes.get(j).setPoints(new ArrayList<>(route2Points));
                            route2.setPoints(new ArrayList<>(route2Points));
                        } else {
                            route1Points = new ArrayList<>(routes.get(i).getPoints());
                            route2Points = new ArrayList<>(routes.get(j).getPoints());
                        }
                    }
                }
            }
        }
    }

    /**
     * addMaximumPoints tries to add all close (see radiusSearchLimit) non-included Points to
     * routes. Preference is given to the Points with the highest score.
     * <p>
     * Note, possiblePoints section code adapted from: (https://stackoverflow.com/a/22683665/,
     * Marco13, 2014).
     *
     * @param routes The ArrayList of Routes which to apply addMaximumPoints to.
     */
    public void addMaximumPoints(ArrayList<Route> routes) {
        // ensuring all points that are already part of a Route are removed as possibilities to be added
        ArrayList<Point> toBeRemoved = new ArrayList<>();
        for (Route finalRoute : routes) {
            toBeRemoved.addAll(finalRoute.getPoints());
        }
        getgHSortedPopRef().removeAll(toBeRemoved);

        for (Route finalRoute : routes) {
            double radiusSearchLimit = gettMax() / 3;
            HashSet<Point> possiblePoints = new HashSet<>();

            // get remaining points and sort relative to distance away from each point in the Route
            for (Point point : finalRoute.getPoints()) {
                getgHSortedPopRef().sort(Point.createComparator(point));

                // create new subList of viable points that are within the radiusSearchLimit
                int index = 0;
                for (Point sortedPoint : getgHSortedPopRef()) {

                    if (Util.euclideanDistance(point, sortedPoint) > radiusSearchLimit) {
                        break;
                    }
                    index++;
                }
                possiblePoints.addAll(new ArrayList<>(getgHSortedPopRef().subList(0, index)));
            }
            ArrayList<Point> possiblePointsList = new ArrayList<>(possiblePoints);
            possiblePointsList.sort(Collections.reverseOrder(Point.SCORE_COMPARATOR));

            // try and add all points to Route starting with the highest scoring point
            int i = 0;
            while (i < possiblePointsList.size()) {
                Point newPoint = possiblePointsList.get(0);
                ArrayList<Point> newPoints = new ArrayList<>(finalRoute.getPoints());
                int bestValidIndex = lowestAdditionalDistanceIndex(newPoint, newPoints);
                if (bestValidIndex != -1) {
                    newPoints.add(bestValidIndex, newPoint);
                }
                Route tmpRoute = new Route(newPoints);

                // check if Route is valid
                if (tmpRoute.getTotalDistance() <= gettMax()) {
                    finalRoute.setPoints(newPoints);
                    getgHSortedPopRef().remove(newPoint);
                }
                possiblePointsList.remove(0);
            }
        }
    }

    /**
     * printMetricsPerXGenerations can be used within any process that utilises
     * generateRoutesFromTOPFile to print score and distance information for the best Route and the
     * number of unique 'genes' (Points) available across all routes of given generation.
     *
     * @param perXGenerations Per number of generations to display the score, distance & gene
     *                        information.
     */
    public void printMetricsPerXGenerations(int perXGenerations) {
        if (getGenerationCounter() % perXGenerations == 0) {
            Route topInGen = Collections.max(getChildPopulation(), Route.ROUTE_SCORE_COMPARATOR);
            Set<Point> uniqueGenes = new HashSet<>();
            for (Route r : getChildPopulation()) {
                uniqueGenes.addAll(r.getPoints());
            }
            System.out.println("Generation: " + getGenerationCounter()
                + ". Score = " + topInGen.getTotalScore()
                + ". Distance = " + topInGen.getTotalDistance()
                + ". Num. Unique Genes = " + uniqueGenes.size()
            );
        }
    }

    /**
     * benchmark enables easy benchmarking across multiple test instances. benchmark, for all files
     * within the testDataCompleteFilepath directory outputs the name of the file which the
     * algorithm was tested on, the best score, the average score, the average time taken in seconds
     * and point/score information for the best-scoring Route combination across numRuns.
     *
     * @param testDataCompleteFilepath  The directory of test instances to benchmark upon. A
     *                                  filepath for single file can also be used.
     * @param numRuns                   The number of runs per-instance to benchmark against.
     * @param popSize                   The size of the population.
     * @param tourTriesMax              The maximum number of unsuccessful tries (per-route) to
     *                                  improve the route within the initialisePopulation phase.
     * @param pCrossover                The probability of crossover occurring.
     * @param pMutate                   The probability of mutation occurring.
     * @param elitistReplacementPercent The percentage of the population that is retained from the
     *                                  previous generation.
     * @param maxGenerations            The maximum number of generations to complete per route.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public static void benchmark(String testDataCompleteFilepath, int numRuns, int popSize,
        int tourTriesMax, double pCrossover, double pMutate, double elitistReplacementPercent,
        int maxGenerations) throws IOException {

        Path testDataPath = Path.of(testDataCompleteFilepath);
        File testDataCompleteFile = new File(String.valueOf(testDataPath));

        // loop through test
        File[] files = new File[]{testDataCompleteFile};
        if (testDataCompleteFile.isDirectory()) {
            files = testDataCompleteFile.listFiles(pathname -> !pathname.isHidden());
        }

        assert files != null;
        Arrays.sort(files);
        for (File file : files) {

            String fileName = file.getName();
            if (fileName.contains(".txt")) {
                fileName = fileName.replace(".txt", "");
            }

            ArrayList<Double> times = new ArrayList<>();
            ArrayList<Double> scores = new ArrayList<>();
            ArrayList<ArrayList<Route>> routes = new ArrayList<>();
            for (int i = 0; i < numRuns; i++) {

                // get result
                Algorithm testAlgorithm = new Algorithm(TopData.generateDataFromTOPFile(file),
                    popSize, tourTriesMax,
                    pCrossover, pMutate, elitistReplacementPercent, maxGenerations);

                // CPU time measurement
                Instant start = Instant.now();
                Result testResult = testAlgorithm.generateRoutesFromTOPFile();
                Instant end = Instant.now();
                double cpuSeconds =
                    (double) Duration.between(start, end).toNanos() / 1_000_000_000.0;

                times.add(cpuSeconds);
                scores.add(testResult.getRoutesGeneratedCombinedScore());
                routes.add(testResult.getRoutesGenerated());
            }

            int bestScoreIndex = scores.indexOf(Collections.max(scores));
            double bestScore = scores.get(bestScoreIndex);
            double avgScore = scores.stream().mapToDouble(s -> s).average().orElseThrow(
                () -> new IllegalArgumentException(
                    "scores ArrayList could not return an average."));
            double avgTimeSecsDuration = times.stream().mapToDouble(s -> s).average().orElseThrow(
                () -> new IllegalArgumentException("times ArrayList could not return an average."));

            StringBuilder bestRouteString = new StringBuilder();
            for (Route route : routes.get(bestScoreIndex)) {
                for (Point point : route.getPoints()) {
                    bestRouteString.append(point.getId()).append(" ");
                }
                bestRouteString.append("| ").append(route.getTotalScore()).append(", ");
            }

            System.out
                .println(fileName + ", " + bestScore + ", " + avgScore + ", " + avgTimeSecsDuration
                    + ", " + bestRouteString);
        }
    }

    /**
     * The generateRoutesFromTOPFile algorithm utilises: initialisePopulation, tournamentSelection,
     * singlePointCrossover, completeTwoOpt, addRandomPointMinAddtDistLocIfValid,
     * iterativeLocalSearch, addOrReplace, elitistReplacement, rearrange and addMaximumPoints
     * amongst other methods to produce solutions to TOP problem instances which are returned in the
     * form of a Result.
     *
     * @return The Result of the generateRoutesFromTOPFile method.
     */
    public Result generateRoutesFromTOPFile() {

        while (getnRoutesCounter() < getnRoutes()) {
            /*---- 1. Initialise Population Section ----*/
            initialisePopulation();
            /*---- 2. Repeat (until termination condition(s) are met) ----*/
            setGenerationCounter(0);
            setSelectedParents(new ArrayList<>());
            setRetainedChildren(new ArrayList<>());
            double sameScore = 0;
            int sameScoreCounter = 0;
            long sameScoreStopCount = Math.round(getMaxGenerations() * 0.25);
            Route bestRoute = new Route(new ArrayList<>());
            bestRoute.getPoints().add(new Point(0, -1, -1, 0));
            while (getGenerationCounter() <= getMaxGenerations()
                && sameScoreCounter < sameScoreStopCount) {
                /*---- 2.1 Select Parents via Tournament Selection ----*/
                tournamentSelection();
                /*---- 2.2 Recombine Parents ----*/
                singlePointCrossover();
                /*---- 2.3 Mutate Offspring ----*/
                for (Route child : getChildPopulation()) {
                    if (getRand().nextDouble() <= getpMutate()) {
                        /*---- 2.3.1: 2-Opt Swap ----*/
                        if (getRand().nextDouble() < 0.5) {
                            completeTwoOpt(child);
                        }
                        /*---- 2.3.2: Add Random Node Min Additional Distance ----*/
                        if (getRand().nextDouble() < 0.5) {
                            addRandomPointMinAddtDistLocIfValid(child);
                        }
                        /*---- 2.3.3: Iterative Local Search ----*/
                        if (getRand().nextDouble() < 0.5) {
                            iterativeLocalSearch(child, 3);
                        }
                        /*---- 2.3.4: Add or Replace ----*/
                        if (getRand().nextDouble() < 0.5) {
                            addOrReplace(child);
                        }
                    }
                }
                /*---- 3. Select Survivors ----*/
                elitistReplacement(getElitistReplacementPercent());
                Route bestInterGenRoute = Collections
                    .max(getRetainedChildren(), Route.ROUTE_SCORE_COMPARATOR);

                if (bestInterGenRoute.getTotalScore() > bestRoute.getTotalScore()) {
                    bestRoute.setPoints(new ArrayList<>(bestInterGenRoute.getPoints()));
                } else if (bestInterGenRoute.getTotalScore() == bestRoute.getTotalScore()
                    && bestInterGenRoute.getTotalDistance() < bestRoute.getTotalDistance()) {
                    bestRoute.setPoints(new ArrayList<>(bestInterGenRoute.getPoints()));
                }

                // early route-score-stagnation stop check
                if (bestInterGenRoute.getTotalScore() == sameScore) {
                    sameScoreCounter++;
                } else {
                    sameScore = bestInterGenRoute.getTotalScore();
                    sameScoreCounter = 0;
                }
                // printMetricsPerXGenerations(1); // uncomment for debugging

                /*---- 4. Add Best Route at final perRouteGeneration to finalRoutes ----*/
                if (getGenerationCounter() == getMaxGenerations()
                    || sameScoreCounter >= sameScoreStopCount) {
                    getFinalRoutes().add(bestRoute);
                    // remove finalRoute points from potential population
                    getDataset().getPoints().removeAll(bestRoute.getPoints());
                    getgHSortedPopRef().removeAll(bestRoute.getPoints());
                } else {
                    Collections.shuffle(getRetainedChildren());
                }
                setGenerationCounter(getGenerationCounter() + 1);
            }
            setnRoutesCounter(getnRoutesCounter() + 1);
        }
        /*---- 5. Creating Space & Adding Additional Nodes if Possible to Final Routes ----*/
        rearrange(getFinalRoutes());
        addMaximumPoints(getFinalRoutes());

        Result algorithmResult = new Result(getDataset(), getPopSize(), getTourTriesMax(),
            getpCrossover(), getpMutate(),
            getElitistReplacementPercent(), getMaxGenerations(), getFinalRoutes());

        // algorithmResult.printFinalRoutesAndScores(); // uncomment for debugging
        return algorithmResult;
    }

}