package org.elliotpartridge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * TopData parses and stores information found within TOP Format (https://www.mech.kuleuven.be/en/cib/op/instances/TOPformat/view)
 * compliant datasets.
 */
public class TopData {

    private final String fileName;
    private final int nPoints;
    private final int nRoutes;
    private final double tMax;
    private final ArrayList<Point> points;

    /**
     * TopData constructor.
     *
     * @param fileName Name of the TOP Format compliant file from which to produce TopData from.
     * @param nPoints  Number of Points within the file.
     * @param nRoutes  Number of Routes to be produced.
     * @param tMax     Maximum time budget per Route.
     * @param points   ArrayList of individual Points.
     */
    public TopData(String fileName, int nPoints, int nRoutes, double tMax,
        ArrayList<Point> points) {
        this.fileName = fileName;
        this.nPoints = nPoints;
        this.nRoutes = nRoutes;
        this.tMax = tMax;
        this.points = points;
    }

    /**
     * fileName getter.
     *
     * @return Name of the TOP Format compliant file from which the TopData was produced from.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * nPoints getter.
     *
     * @return Number of Points within the file.
     */
    public int getnPoints() {
        return nPoints;
    }

    /**
     * nRoutes getter.
     *
     * @return Number of Routes to be produced.
     */
    public int getnRoutes() {
        return nRoutes;
    }

    /**
     * tMax getter.
     *
     * @return Maximum time budget per Route.
     */
    public double gettMax() {
        return tMax;
    }

    /**
     * points getter.
     *
     * @return ArrayList of individual Points.
     */
    public ArrayList<Point> getPoints() {
        return points;
    }

    /**
     * generateDataFromTOPFile processes and creates TopData from a TOP Format compliant file.
     *
     * @param fileToProcess The TOP Format compliant (https://www.mech.kuleuven.be/en/cib/op/instances/TOPformat/view)
     *                      file to be processed.
     * @return TopData instance of fileToProcess.
     * @throws IllegalArgumentException Thrown to indicate that a method has been passed an illegal
     *                                  or inappropriate argument.
     * @throws IOException              Signals that an I/O exception of some sort has occurred.
     */
    public static TopData generateDataFromTOPFile(File fileToProcess)
        throws IllegalArgumentException, IOException {

        int nPoints = -1;
        int nRoutes = -1;
        double tMax = -1;
        ArrayList<Point> points = new ArrayList<>();

        String fileName = fileToProcess.getName();

        try (Scanner scanner = new Scanner(fileToProcess)) {

            String delimiters = "\\s+|\\t+"; // this splits on 1+ space(s) or 1+ tab(s)
            int lineCount = 1;
            int pointID = 1;

            String[] lineParts;
            while (scanner.hasNextLine()) {
                // parse the TOP Format parameters which should always be the first 3 lines of a compliant file
                if (lineCount == 1) {
                    lineParts = scanner.nextLine().split(delimiters);
                    if (!lineParts[0].equals("n")) {
                        throw new IllegalArgumentException("File is not in TOP format.");
                    }
                    nPoints = Integer.parseInt(lineParts[1]);
                    lineCount++;
                } else if (lineCount == 2) {
                    lineParts = scanner.nextLine().split(delimiters);
                    if (!lineParts[0].equals("m")) {
                        throw new IllegalArgumentException("File is not in TOP format.");
                    }
                    nRoutes = Integer.parseInt(lineParts[1]);
                    if (nRoutes == 0) {
                        throw new IllegalArgumentException(
                            "Number of routes (m) cannot be 0 in TOP format file.");
                    }
                    lineCount++;
                } else if (lineCount == 3) {
                    lineParts = scanner.nextLine().split(delimiters);
                    if (!lineParts[0].equals("tmax")) {
                        throw new IllegalArgumentException("File is not in TOP format.");
                    }
                    tMax = Double.parseDouble(lineParts[1]);
                    lineCount++;

                    // parsing individual the points
                } else {
                    lineParts = scanner.nextLine().split(delimiters);

                    double longitude = Double.parseDouble(lineParts[0]);
                    double latitude = Double.parseDouble(lineParts[1]);
                    double score = Double.parseDouble(lineParts[2]);

                    points.add(new Point(pointID, longitude, latitude, score));

                    pointID++;
                }
            }
        }

        return new TopData(fileName, nPoints, nRoutes, tMax, points);
    }

    /**
     * removeTOPStartingAndEndingPoints removes the starting and ending Points from the points
     * Array.
     */
    public void removeTOPStartingAndEndingPoints() {
        this.getPoints().remove(0);
        this.getPoints().remove(getPoints().size() - 1);
    }

}
