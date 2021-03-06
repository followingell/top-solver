package org.elliotpartridge;

import java.util.ArrayList;

/**
 * Result stores information about and provides functionality related to Results generated by
 * Algorithm methods.
 */
public class Result {

    TopData dataset;
    int popSize;
    int tourTriesMax;
    double pCrossover;
    double pMutate;
    double tpgPercent;
    int maxGenerations;
    ArrayList<Route> routesGenerated;

    /**
     * Result constructor.
     *
     * @param dataset         The dataset the Result was produced from.
     * @param popSize         The size of the population parameter that was used to generate the
     *                        Result.
     * @param tourTriesMax    The maximum number of unsuccessful tries (per-route) to improve the
     *                        route within the initialisePopulation phase parameter that was used to
     *                        generate the Result.
     * @param pCrossover      The probability of crossover occurring parameter that was used to
     *                        generate the Result.
     * @param pMutate         The probability of mutation occurring parameter that was used to
     *                        generate the Result.
     * @param tpgPercent      The percentage of children to replace via the elitistReplacement
     *                        method parameter that was used to generate the Result.
     * @param maxGenerations  The maximum number of generations to complete per route parameter that
     *                        was used to generate the Result.
     * @param routesGenerated The final Routes generated by Algorithm.
     */
    public Result(TopData dataset, int popSize, int tourTriesMax, double pCrossover, double pMutate,
        double tpgPercent, int maxGenerations,
        ArrayList<Route> routesGenerated) {
        this.dataset = dataset;
        this.popSize = popSize;
        this.tourTriesMax = tourTriesMax;
        this.pCrossover = pCrossover;
        this.pMutate = pMutate;
        this.tpgPercent = tpgPercent;
        this.maxGenerations = maxGenerations;
        this.routesGenerated = routesGenerated;
    }

    /**
     * routesGenerated getter.
     *
     * @return The final Routes generated by Algorithm.
     */
    public ArrayList<Route> getRoutesGenerated() {
        return routesGenerated;
    }

    /**
     * getRoutesGeneratedCombinedScore returns the totalScore across all routesGenerated.
     *
     * @return The sum total of totalScore across all routesGenerated.
     */
    public double getRoutesGeneratedCombinedScore() {
        double combinedScore = 0;
        for (Route route : getRoutesGenerated()) {
            combinedScore += route.getTotalScore();
        }
        return combinedScore;
    }

    /**
     * printFinalRoutesAndScores prints the constituent Points and score of each Route within
     * routesGenerated.
     */
    public void printFinalRoutesAndScores() {
        for (Route route : getRoutesGenerated()) {
            for (Point point : route.getPoints()) {
                System.out.print(point.getId() + " ");
            }
            System.out
                .print(" | " + route.getTotalScore() + " | " + route.getTotalDistance() + "\n");
        }
        System.out.print("Total Score: " + getRoutesGeneratedCombinedScore() + "\n");
    }
}
