import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.hsr.geohash.GeoHash;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.assertj.core.api.Assertions;
import org.elliotpartridge.Algorithm;
import org.elliotpartridge.Point;
import org.elliotpartridge.Result;
import org.elliotpartridge.Route;
import org.elliotpartridge.TopData;
import org.elliotpartridge.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

/**
 * Test contains tests for each class in separate nested classes for clarity.
 */
public class Test {

    @Nested
    class TopDataTests {

        @org.junit.jupiter.api.Test
        @DisplayName("org.elliotpartridge.TopData object generated from TOP file correctly")
        void generateDataFromTOPFile() {
            File file = new File("src/test/resources/valid-top-file.txt");
            assertThatCode(() -> TopData.generateDataFromTOPFile(file)).doesNotThrowAnyException();
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Exception thrown for invalid parameters in top file")
        public void generateDataFromTOPFileParametersIllegalArgumentException() {
            File file = new File("src/test/resources/invalid-top-file-parameters.txt");
            assertThatThrownBy(() -> TopData.generateDataFromTOPFile(file)).isInstanceOf(IllegalArgumentException.class);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("caught Exception for invalid point in top file")
        public void generateDataFromTOPFilePointIllegalArgumentException() {
            File file = new File("src/test/resources/invalid-top-file-point.txt");
            assertThatThrownBy(() -> TopData.generateDataFromTOPFile(file)).isInstanceOf(IllegalArgumentException.class);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("fileName getter correctly returns String")
        void getFileName() {
            TopData testTopData = new TopData("test", 32, 2, 2.5, new ArrayList<>());

            assertThat(testTopData.getFileName()).isEqualTo("test");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("nPoints getter correctly returns value")
        void getnPoints() {
            TopData testTopData = new TopData("test", 32, 2, 2.5, new ArrayList<>());

            assertThat(testTopData.getnPoints()).isEqualTo(32);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("nRoutes getter correctly returns value")
        void getnRoutes() {
            TopData testTopData = new TopData("test", 32, 2, 2.5, new ArrayList<>());

            assertThat(testTopData.getnRoutes()).isEqualTo(2);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("tMax getter correctly returns value")
        void gettMax() {
            TopData testTopData = new TopData("test", 32, 2, 2.5, new ArrayList<>());

            assertThat(testTopData.gettMax()).isEqualTo(2.5);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("points getter correctly returns value")
        void getPoints() {
            TopData testTopData = new TopData("test", 32, 2, 2.5, new ArrayList<>());

            assertThat(testTopData.getPoints()).isEqualTo(new ArrayList<>());
        }

        @org.junit.jupiter.api.Test
        @DisplayName("removeTOPStartingAndEndingPoints correctly removes starting and ending points")
        void removeTOPStartingAndEndingPoints() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);

            assertThat(testTopData.getPoints().get(0).getId()).isEqualTo(1);
            assertThat(testTopData.getPoints().get(testTopData.getPoints().size() - 1).getId()).isEqualTo(32);

            testTopData.removeTOPStartingAndEndingPoints();

            assertThat(testTopData.getPoints().get(0).getId()).isNotEqualTo(1);
            assertThat(testTopData.getPoints().get(testTopData.getPoints().size() - 1).getId()).isNotEqualTo(32);
        }
    }

    @Nested
    class PointTests {

        @org.junit.jupiter.api.Test
        @DisplayName("getId getter correctly returns value")
        void getId() {
            Point testPoint = new Point(1, 10.500, 14.400, 0);

            assertThat(testPoint.getId()).isEqualTo(1);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getLongitude getter correctly returns value")
        void getLongitude() {
            Point testPoint = new Point(1, 10.500, 14.400, 0);

            assertThat(testPoint.getLongitude()).isEqualTo(10.500);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getLatitude getter correctly returns value")
        void getLatitude() {
            Point testPoint = new Point(1, 10.500, 14.400, 0);

            assertThat(testPoint.getLatitude()).isEqualTo(14.400);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getScore getter correctly returns value")
        void getScore() {
            Point testPoint = new Point(1, 10.500, 14.400, 0);

            assertThat(testPoint.getScore()).isEqualTo(0);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getGhRep getter correctly returns a GeoHash representation")
        void getGhRep() {
            Point testPoint = new Point(1, 10.500, 14.400, 0);

            assertThat(testPoint.getGhRep()).isInstanceOf(GeoHash.class);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("POINT_GH_COMPARATOR correctly sorts points by GeoHash location")
        void pointGeoHashComparatorCorrectOrder1() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 16.500, 14.500, 0);
            Point testPoint3 = new Point(3, 11.500, 13.400, 0);

            ArrayList<Point> testPoints = new ArrayList<>();
            testPoints.add(testPoint1);
            testPoints.add(testPoint2);
            testPoints.add(testPoint3);

            assertThat(testPoints).containsExactly(testPoint1, testPoint2, testPoint3);

            testPoints.sort(Point.GH_COMPARATOR);

            assertThat(testPoints).containsExactly(testPoint1, testPoint3, testPoint2);
        }
    }

    @Nested
    class RouteTests {

        @org.junit.jupiter.api.Test
        @DisplayName("getPoints getter correctly returns an ArrayList<Point>")
        void getPoints() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            assertThat(testRoute.getPoints()).isInstanceOf(ArrayList.class);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("setPoints correctly sets Route points")
        void setPoints() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            assertThat(testRoute.getPoints().get(0).getLongitude()).isEqualTo(10.500);
            assertThat(testRoute.getPoints().get(1).getLongitude()).isEqualTo(18.000);

            Point testPoint4 = new Point(1, 18.300, 13.300, 10);
            Point testPoint5 = new Point(2, 16.500, 9.300, 10);

            testRoute.setPoints(new ArrayList<>() {{
                add(testPoint4);
                add(testPoint5);
            }});

            assertThat(testRoute.getPoints().get(0).getLongitude()).isEqualTo(18.300);
            assertThat(testRoute.getPoints().get(1).getLongitude()).isEqualTo(16.500);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("setPoints correctly changes totalDistance via calculateAndSetTotalDistance method")
        void setPointsCalculateAndSetTotalDistance() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            double previousTotalDistance = testRoute.getTotalDistance();

            Point testPoint4 = new Point(1, 18.300, 13.300, 10);
            Point testPoint5 = new Point(2, 16.500, 9.300, 10);

            testRoute.setPoints(new ArrayList<>() {{
                add(testPoint4);
                add(testPoint5);
            }});

            assertThat(testRoute.getTotalDistance()).isNotEqualTo(previousTotalDistance);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("setPoints correctly changes totalScore via calculateAndSetTotalScore method")
        void setPointsCalculateAndSetTotalScore() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            double previousTotalScore = testRoute.getTotalScore();

            Point testPoint4 = new Point(1, 18.300, 13.300, 10);
            Point testPoint5 = new Point(2, 16.500, 9.300, 10);

            testRoute.setPoints(new ArrayList<>() {{
                add(testPoint4);
                add(testPoint5);
            }});

            assertThat(testRoute.getTotalScore()).isNotEqualTo(previousTotalScore);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("setPoints correctly changes containsDuplicatePoints via calculateAndSetContainsDuplicatePoints method")
        void setPointsCalculateAndSetContainsDuplicatePoints() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            boolean previousContainsDuplicatePoints = testRoute.getContainsDuplicatePoints();

            testRoute.setPoints(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint1);
            }});

            assertThat(testRoute.getContainsDuplicatePoints()).isNotEqualTo(previousContainsDuplicatePoints);
            assertThat(testRoute.getContainsDuplicatePoints()).isTrue();
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getPoint getter correctly returns a Point")
        void getPoint() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
            }});

            assertThat(testRoute.getPoint(0)).isInstanceOf(Point.class);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("setPoint correctly sets Point")
        void setPoint() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
            }});

            testRoute.setPoint(0, testPoint2);

            assertThat(testRoute.getPoint(0)).isEqualTo(testPoint2);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getTotalDistance correctly returns totalDistance")
        void getTotalDistance() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            assertThat(testRoute.getTotalDistance()).isCloseTo(14.68273, Assertions.offset(0.00001d));
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getTotalScore getter correctly returns a totalScore")
        void getTotalScore() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            assertThat(testRoute.getTotalScore()).isEqualTo(10.);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getNonCPointScores getter correctly returns a ArrayList<Double>")
        void getNonCPointScores() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            assertThat(testRoute.getNonCPointScores()).isInstanceOf(ArrayList.class);
            assertThat(testRoute.getNonCPointScores()).asList().contains(0.);
            assertThat(testRoute.getNonCPointScores()).asList().contains(10.);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getCumulativePointScore getter correctly returns a score")
        void getCumulativePointScore() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(3, 18.300, 13.300, 10);
            Point testPoint4 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
                add(testPoint4);
            }});

            assertThat(testRoute.getCumulativePointScore(1)).isEqualTo(10.);
            assertThat(testRoute.getCumulativePointScore(1)).isNotEqualTo(20.);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getNonCInterPointTravelDistances getter correctly returns a ArrayList<Double>")
        void getNonCInterPointTravelDistances() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            assertThat(testRoute.getNonCInterPointTravelDistances()).isInstanceOf(ArrayList.class);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("getCumulativeTravelDistance getter returns correct value")
        void getCumulativeTravelDistance() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            Route testRoute = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2);
                add(testPoint3);
            }});

            assertThat(testRoute.getCumulativeTravelDistance(0)).isEqualTo(0.);
            assertThat(testRoute.getCumulativeTravelDistance(1)).isCloseTo(7.64852927, Assertions.offset(0.00001d));
            assertThat(testRoute.getCumulativeTravelDistance(2)).isCloseTo(14.682731, Assertions.offset(0.00001d));
        }

        @org.junit.jupiter.api.Test
        @DisplayName("ROUTE_DISTANCE_COMPARATOR correctly sorts points by totalDistance")
        void routeDistanceComparatorCorrectOrder() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2a = new Point(2, 18.000, 15.900, 10);
            Point testPoint2b = new Point(2, 10.500, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            // 14.6
            Route testRoute1 = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2a);
                add(testPoint3);
            }});

            // 3.4
            Route testRoute2 = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2b);
                add(testPoint3);
            }});

            ArrayList<Route> testRoutes = new ArrayList<>();
            testRoutes.add(testRoute1);
            testRoutes.add(testRoute2);

            assertThat(testRoutes.get(0)).isEqualTo(testRoute1);
            assertThat(testRoutes.get(1)).isEqualTo(testRoute2);

            testRoutes.sort(Route.ROUTE_DISTANCE_COMPARATOR);

            assertThat(testRoutes.get(0)).isEqualTo(testRoute2);
            assertThat(testRoutes.get(1)).isEqualTo(testRoute1);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("ROUTE_SCORE_COMPARATOR correctly sorts points by score and then distance")
        void routeScoreComparatorCorrectOrder() {
            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2a = new Point(2, 18.000, 15.900, 10);
            Point testPoint2b = new Point(2, 10.500, 15.900, 10);
            Point testPoint3 = new Point(32, 11.200, 14.100, 0);

            // 14.6
            Route testRoute1 = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2a);
                add(testPoint3);
            }});

            // 3.4
            Route testRoute2 = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint2b);
                add(testPoint3);
            }});

            Route testRoute3 = new Route(new ArrayList<>() {{
                add(testPoint1);
                add(testPoint3);
            }});

            ArrayList<Route> testRoutes = new ArrayList<>();
            testRoutes.add(testRoute1);
            testRoutes.add(testRoute2);
            testRoutes.add(testRoute3);

            assertThat(testRoutes.get(0)).isEqualTo(testRoute1);
            assertThat(testRoutes.get(1)).isEqualTo(testRoute2);
            assertThat(testRoutes.get(2)).isEqualTo(testRoute3);

            testRoutes.sort(Route.ROUTE_SCORE_COMPARATOR);

            assertThat(testRoutes.get(0)).isEqualTo(testRoute3);
            assertThat(testRoutes.get(1)).isEqualTo(testRoute1);
            assertThat(testRoutes.get(2)).isEqualTo(testRoute2);

            testRoutes.sort(Collections.reverseOrder(Route.ROUTE_SCORE_COMPARATOR));

            assertThat(testRoutes.get(0)).isEqualTo(testRoute2);
            assertThat(testRoutes.get(1)).isEqualTo(testRoute1);
            assertThat(testRoutes.get(2)).isEqualTo(testRoute3);
        }
    }

    @Nested
    class UtilTests {

        @org.junit.jupiter.api.Test
        @DisplayName("euclideanDistance correctly returns expected value")
        void euclideanDistance() {
            Point testPoint1 = new Point(0, 10.500, 14.400, 0);
            Point testPoint2 = new Point(1, 18.000, 15.900, 10);

            assertThat(Util.euclideanDistance(testPoint1, testPoint2)).isCloseTo(7.648529, Assertions.offset(0.00001d));
        }

        @org.junit.jupiter.api.Test
        @DisplayName("normaliseBetweenRange correctly converts all values to be within range")
        void normaliseBetweenRange() {
            ArrayList<Double> numArray = new ArrayList<>();
            Random rand = new Random();
            for (int i = 0; i < 50; i++) {
                numArray.add(rand.nextDouble());
            }
            assertThat(numArray).allSatisfy(n -> assertThat(n >= 0. && n < 1));

            double minVal = numArray.stream().mapToDouble(n -> n).min().orElseThrow(NoSuchElementException::new);
            double maxVal = numArray.stream().mapToDouble(n -> n).max().orElseThrow(NoSuchElementException::new);
            for (int i = 0; i < numArray.size(); i++) {
                numArray.set(i, Util.normaliseBetweenRange(numArray.get(i), minVal, maxVal, 0.5, 1.));
            }
            assertThat(numArray).allSatisfy(n -> assertThat(n >= 0.5 && n <= 1.));
        }
    }

    @Nested
    class AlgorithmTests {

        @org.junit.jupiter.api.Test
        @DisplayName("org.elliotpartridge.Algorithm constructor gHSortedPopRef has correct size")
        void algorithmConstructorgHSortedPopRefCorrectSize() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 200, 50, 0.75, 0.15, 0.25, 200);

            assertThat(testAlgorithm.getgHSortedPopRef()).hasSize(30);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("org.elliotpartridge.Algorithm constructor gHSortedPopRef is sorted correctly")
        void algorithmConstructorSortsgHSortedPopRefCorrectly() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 200, 50, 0.75, 0.15, 0.25, 200);

            assertThat(testAlgorithm.getgHSortedPopRef().get(0).getGhRep()).isLessThanOrEqualTo(testAlgorithm.getgHSortedPopRef().get(1).getGhRep());
            assertThat(testAlgorithm.getgHSortedPopRef().get(0).getGhRep()).isLessThan(testAlgorithm.getgHSortedPopRef().get(10).getGhRep());
        }

        @org.junit.jupiter.api.Test
        @DisplayName("org.elliotpartridge.Algorithm constructor correctly removes starting and ending points from dataset points")
        void algorithmConstructorRemovesStartingAndEndingPoints() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 200, 50, 0.75, 0.15, 0.25, 200);

            assertThat(testAlgorithm.getDataset().getPoints()).doesNotContain(testAlgorithm.getStartingPoint(), testAlgorithm.getEndingPoint());
        }

        @org.junit.jupiter.api.Test
        @DisplayName("initialisePopulation correctly produces the correct amount of routes")
        void algorithmInitialisePopulationCorrectSize() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);

            testAlgorithm.initialisePopulation();

            assertThat(testAlgorithm.getRoutePopulation()).hasSize(30);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("initialisePopulation correctly produces only valid routes")
        void algorithmInitialisePopulationValidRoutes() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);

            testAlgorithm.initialisePopulation();
            ArrayList<Route> testRoutePopulation = testAlgorithm.getRoutePopulation();

            for (Route route : testRoutePopulation) {
                assertThat(route.getPoint(0)).isEqualTo(testAlgorithm.getStartingPoint());
                assertThat(route.getPoint(route.getPoints().size() - 1)).isEqualTo(testAlgorithm.getEndingPoint());
                assertThat(route.getContainsDuplicatePoints()).isNotEqualTo(true);
                assertThat(route.getTotalDistance()).isLessThanOrEqualTo(testAlgorithm.gettMax());
            }
        }

        @org.junit.jupiter.api.Test
        @DisplayName("tournamentSelection correctly generates new list of routes that differs from original list of routes")
        void algorithmTournamentSelectedParentsDiffersFromRoutePopulation() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);
            testAlgorithm.initialisePopulation();

            testAlgorithm.tournamentSelection();

            assertThat(testAlgorithm.getSelectedParents()).isNotEqualTo(testAlgorithm.getRoutePopulation());
        }

        @org.junit.jupiter.api.Test
        @DisplayName("tournamentSelection correctly generates the correct number of parents")
        void algorithmTournamentSelectionCorrectNoOfParents() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);
            testAlgorithm.initialisePopulation();

            testAlgorithm.tournamentSelection();

            assertThat(testAlgorithm.getSelectedParents()).hasSize(30);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("generateRoutesFromTOPFile correctly generates the right amount of routes")
        void generateRoutesFromTOPFileCorrectNoOfRoutes() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);
            Result testResult = testAlgorithm.generateRoutesFromTOPFile();

            assertThat(testResult.getRoutesGenerated().size()).isEqualTo(2);
            assertThat(testResult.getRoutesGenerated().get(0)).isInstanceOf(Route.class);
            assertThat(testResult.getRoutesGenerated().get(1)).isInstanceOf(Route.class);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("generateRoutesFromTOPFile generates routes with the correct starting and ending points")
        void generateRoutesFromTOPFileCorrectStartingEndingPoints() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);
            Result testResult = testAlgorithm.generateRoutesFromTOPFile();

            assertThat(testResult.getRoutesGenerated().get(0).getPoint(0).getId()).isEqualTo(1);
            assertThat(testResult.getRoutesGenerated().get(0).getPoint(testResult.getRoutesGenerated().get(0).getPoints().size() - 1).getId()).isEqualTo(32);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("generateRoutesFromTOPFile generates routes with no duplicate points excluding starting and ending points")
        void generateRoutesFromTOPFileNoDuplicatePoints() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);
            Result testResult = testAlgorithm.generateRoutesFromTOPFile();

            List<Point> route1ExcludingStartingEndingPoints = testResult.getRoutesGenerated().get(0).getPoints().subList(1, testResult.getRoutesGenerated().get(0).getPoints().size() - 1);
            List<Point> route2ExcludingStartingEndingPoints = testResult.getRoutesGenerated().get(1).getPoints().subList(1, testResult.getRoutesGenerated().get(1).getPoints().size() - 1);

            for (Point testPoint: route1ExcludingStartingEndingPoints) {
                assertThat(route2ExcludingStartingEndingPoints).doesNotContain(testPoint);
            }
            for (Point testPoint: route2ExcludingStartingEndingPoints) {
                assertThat(route1ExcludingStartingEndingPoints).doesNotContain(testPoint);
            }
        }

        @org.junit.jupiter.api.Test
        @DisplayName("generateRoutesFromTOPFile generates routes with no duplicate points excluding starting and ending points (large solution check)")
        void generateRoutesFromTOPFileNoDuplicatePointsLarge() throws IOException {
            File file = new File("src/test/resources/large-valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 200, 30, 0.75, 0.25, 0.03, 200);
            Result testResult = testAlgorithm.generateRoutesFromTOPFile();

            List<Point> route1ExcludingStartingEndingPoints = testResult.getRoutesGenerated().get(0).getPoints().subList(1, testResult.getRoutesGenerated().get(0).getPoints().size() - 1);
            List<Point> route2ExcludingStartingEndingPoints = testResult.getRoutesGenerated().get(1).getPoints().subList(1, testResult.getRoutesGenerated().get(1).getPoints().size() - 1);
            List<Point> route3ExcludingStartingEndingPoints = testResult.getRoutesGenerated().get(2).getPoints().subList(1, testResult.getRoutesGenerated().get(2).getPoints().size() - 1);
            List<Point> route4ExcludingStartingEndingPoints = testResult.getRoutesGenerated().get(3).getPoints().subList(1, testResult.getRoutesGenerated().get(3).getPoints().size() - 1);

            for (Point testPoint: route1ExcludingStartingEndingPoints) {
                assertThat(route2ExcludingStartingEndingPoints).doesNotContain(testPoint);
                assertThat(route3ExcludingStartingEndingPoints).doesNotContain(testPoint);
                assertThat(route4ExcludingStartingEndingPoints).doesNotContain(testPoint);
            }
            for (Point testPoint: route2ExcludingStartingEndingPoints) {
                assertThat(route1ExcludingStartingEndingPoints).doesNotContain(testPoint);
                assertThat(route3ExcludingStartingEndingPoints).doesNotContain(testPoint);
                assertThat(route4ExcludingStartingEndingPoints).doesNotContain(testPoint);
            }
            for (Point testPoint: route3ExcludingStartingEndingPoints) {
                assertThat(route1ExcludingStartingEndingPoints).doesNotContain(testPoint);
                assertThat(route2ExcludingStartingEndingPoints).doesNotContain(testPoint);
                assertThat(route4ExcludingStartingEndingPoints).doesNotContain(testPoint);
            }
            for (Point testPoint: route4ExcludingStartingEndingPoints) {
                assertThat(route1ExcludingStartingEndingPoints).doesNotContain(testPoint);
                assertThat(route2ExcludingStartingEndingPoints).doesNotContain(testPoint);
                assertThat(route3ExcludingStartingEndingPoints).doesNotContain(testPoint);
            }
        }

        @org.junit.jupiter.api.Test
        @DisplayName("generateRoutesFromTOPFile generates routes whose score is correct")
        void generateRoutesFromTOPFileCorrectScore() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);
            Result testResult = testAlgorithm.generateRoutesFromTOPFile();

            assertThat(testResult.getRoutesGenerated().get(0).getTotalScore()).isCloseTo(testResult.getRoutesGenerated().get(0).getPoints().stream().mapToDouble(Point::getScore).sum(), Assertions.offset(0.00001d));
            assertThat(testResult.getRoutesGenerated().get(1).getTotalScore()).isCloseTo(testResult.getRoutesGenerated().get(1).getPoints().stream().mapToDouble(Point::getScore).sum(), Assertions.offset(0.00001d));
        }

        @org.junit.jupiter.api.Test
        @DisplayName("generateRoutesFromTOPFile generates routes whose score is correct (large solution check)")
        void generateRoutesFromTOPFileCorrectScoreLarge() throws IOException {
            File file = new File("src/test/resources/large-valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 200, 30, 0.75, 0.25, 0.03, 200);
            Result testResult = testAlgorithm.generateRoutesFromTOPFile();

            assertThat(testResult.getRoutesGenerated().get(0).getTotalScore()).isCloseTo(testResult.getRoutesGenerated().get(0).getPoints().stream().mapToDouble(Point::getScore).sum(), Assertions.offset(0.00001d));
            assertThat(testResult.getRoutesGenerated().get(1).getTotalScore()).isCloseTo(testResult.getRoutesGenerated().get(1).getPoints().stream().mapToDouble(Point::getScore).sum(), Assertions.offset(0.00001d));
            assertThat(testResult.getRoutesGenerated().get(2).getTotalScore()).isCloseTo(testResult.getRoutesGenerated().get(2).getPoints().stream().mapToDouble(Point::getScore).sum(), Assertions.offset(0.00001d));
            assertThat(testResult.getRoutesGenerated().get(3).getTotalScore()).isCloseTo(testResult.getRoutesGenerated().get(3).getPoints().stream().mapToDouble(Point::getScore).sum(), Assertions.offset(0.00001d));
        }

        @org.junit.jupiter.api.Test
        @DisplayName("generateRoutesFromTOPFile generates routes whose distance is correct")
        void generateRoutesFromTOPFileCorrectDistance() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);
            Result testResult = testAlgorithm.generateRoutesFromTOPFile();

            assertThat(testResult.getRoutesGenerated().get(0).getTotalDistance()).isCloseTo(testResult.getRoutesGenerated().get(0).getNonCInterPointTravelDistances().stream().mapToDouble(p -> p).sum(), Assertions.offset(0.00001d));
            assertThat(testResult.getRoutesGenerated().get(1).getTotalDistance()).isCloseTo(testResult.getRoutesGenerated().get(1).getNonCInterPointTravelDistances().stream().mapToDouble(p -> p).sum(), Assertions.offset(0.00001d));
        }

        @org.junit.jupiter.api.Test
        @DisplayName("generateRoutesFromTOPFile generates routes whose distance is correct (large solution check)")
        void generateRoutesFromTOPFileCorrectDistanceLarge() throws IOException {
            File file = new File("src/test/resources/large-valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 200, 30, 0.75, 0.25, 0.03, 200);
            Result testResult = testAlgorithm.generateRoutesFromTOPFile();

            assertThat(testResult.getRoutesGenerated().get(0).getTotalDistance()).isCloseTo(testResult.getRoutesGenerated().get(0).getNonCInterPointTravelDistances().stream().mapToDouble(p -> p).sum(), Assertions.offset(0.00001d));
            assertThat(testResult.getRoutesGenerated().get(1).getTotalDistance()).isCloseTo(testResult.getRoutesGenerated().get(1).getNonCInterPointTravelDistances().stream().mapToDouble(p -> p).sum(), Assertions.offset(0.00001d));
            assertThat(testResult.getRoutesGenerated().get(2).getTotalDistance()).isCloseTo(testResult.getRoutesGenerated().get(2).getNonCInterPointTravelDistances().stream().mapToDouble(p -> p).sum(), Assertions.offset(0.00001d));
            assertThat(testResult.getRoutesGenerated().get(3).getTotalDistance()).isCloseTo(testResult.getRoutesGenerated().get(3).getNonCInterPointTravelDistances().stream().mapToDouble(p -> p).sum(), Assertions.offset(0.00001d));
        }

        @org.junit.jupiter.api.Test
        @DisplayName("lowestAdditionalDistanceIndex correctly returns -1 when route has less than 2 points")
        void lowestAdditionalDistanceIndexReturnsMinus1LessThan2() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);

            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            ArrayList<Point> testPoints = new ArrayList<>();
            testPoints.add(testPoint1);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);

            assertThat(testAlgorithm.lowestAdditionalDistanceIndex(testPoint2, testPoints)).isEqualTo(-1);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("lowestAdditionalDistanceIndex correctly returns -1 when route already contains point")
        void lowestAdditionalDistanceIndexReturnsMinus1ContainingPoint() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);

            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint2 = new Point(2, 18.000, 15.900, 10);
            Point testPoint3 = new Point(32, 18.500, 14.500, 0);
            ArrayList<Point> testPoints = new ArrayList<>();
            testPoints.add(testPoint1);
            testPoints.add(testPoint2);
            testPoints.add(testPoint3);

            assertThat(testAlgorithm.lowestAdditionalDistanceIndex(testPoint2, testPoints)).isEqualTo(-1);
        }

        @org.junit.jupiter.api.Test
        @DisplayName("lowestAdditionalDistanceIndex correctly returns 1 when route has 2 points")
        void lowestAdditionalDistanceIndexReturns1() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);

            Point testPoint1 = new Point(1, 10.500, 14.400, 0);
            Point testPoint3 = new Point(32, 18.500, 14.500, 0);
            ArrayList<Point> testPoints = new ArrayList<>();
            testPoints.add(testPoint1);
            testPoints.add(testPoint3);

            Point testPoint2 = new Point(2, 18.600, 14.900, 10);

            assertThat(testAlgorithm.lowestAdditionalDistanceIndex(testPoint2, testPoints)).isEqualTo(1);
        }

    }

    @Nested
    class ResultTests {

        @org.junit.jupiter.api.Test
        @DisplayName("getRoutesGeneratedCombinedScore correctly adds both route scores")
        void generateRoutesFromTOPFileCorrectNoOfRoutesNS() throws IOException {
            File file = new File("src/test/resources/valid-top-file.txt");
            TopData testTopData = TopData.generateDataFromTOPFile(file);
            Algorithm testAlgorithm = new Algorithm(testTopData, 30, 50, 0.75, 0.15, 0.25, 200);
            Result testResult = testAlgorithm.generateRoutesFromTOPFile();

            assertThat(testResult.getRoutesGenerated().size()).isEqualTo(2);
            assertThat(testResult.getRoutesGenerated().get(0)).isInstanceOf(Route.class);
            assertThat(testResult.getRoutesGenerated().get(1)).isInstanceOf(Route.class);

            assertThat(testResult.getRoutesGenerated().get(0).getTotalScore() + testResult.getRoutesGenerated().get(1).getTotalScore()).isEqualTo(testResult.getRoutesGeneratedCombinedScore());
        }

    }
}
