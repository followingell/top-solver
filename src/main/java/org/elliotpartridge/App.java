package org.elliotpartridge;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * App is the JavaFX-based frontend (note this is launched via AppLauncher).
 */
public class App extends Application {

    // vars to ensure standard sizing etc.
    final int PADDING = 10;
    final int OFFSET = 100;
    final int SCENE_WIDTH = OFFSET * 8;
    final int SCENE_HEIGHT = OFFSET * 8;

    final NumberAxis scatterXAxis = new NumberAxis();
    final NumberAxis scatterYAxis = new NumberAxis();
    final ScatterChart<Number, Number> scatterChart = new ScatterChart<>(scatterXAxis,
        scatterYAxis);
    final XYChart.Series<Number, Number> allPointsSeries = new XYChart.Series<>();

    final NumberAxis lineXAxis = new NumberAxis();
    final NumberAxis lineYAxis = new NumberAxis();
    final LineChart<Number, Number> lineChart = new LineChart<>(lineXAxis, lineYAxis);

    Spinner<Integer> popSizeSpinner;
    Spinner<Integer> tourTriesSpinner;
    Spinner<Integer> pCrossoverSpinner;
    Spinner<Integer> pMutateSpinner;
    Spinner<Integer> elitistReplacementSpinner;
    Spinner<Integer> maxGenerationsSpinner;
    Text dataErrorText;
    Text dataInfoText;
    Text resultText;

    static TopData dataset;
    static File datasetFile;

    /**
     * The start method that is called when the application is started. This initialises and
     * populates the initial GUI elements.
     * <p>
     * Note, scatterChart and LineChart styling code taken from: (https://stackoverflow.com/q/17374647,
     * sarcan, 2013) & (https://stackoverflow.com/a/62371221, daniel, 2014).
     *
     * @param primaryStage The applications primary window.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        // initial gui layout & elements
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: ghostwhite;");

        // center
        // vBox for points, routes and title
        VBox centerVBox = new VBox();
        HBox centerHBox = new HBox();
        centerHBox.setAlignment(Pos.CENTER);
        centerHBox.setPadding(new Insets(PADDING));
        centerVBox.setStyle("-fx-border-color: darkgray;");

        Text title = new Text("TOP Solver");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        centerHBox.getChildren().add(title);
        centerVBox.getChildren().add(centerHBox);

        StackPane centerStackPane = new StackPane();
        centerVBox.getChildren().add(centerStackPane);
        borderPane.setCenter(centerVBox);

        // left
        // control panel
        VBox inputVBox = new VBox();
        inputVBox.setPadding(new Insets(PADDING));
        inputVBox.setAlignment(Pos.CENTER);
        inputVBox.setStyle("-fx-border-style: solid;" + "-fx-border-color: darkgray;"
            + "-fx-background-color: lightgray");
        inputVBox.setSpacing(9);

        Text dataTitle = new Text("Data");
        dataTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Button processData = new Button("Select TOP File");
        dataErrorText = new Text("");
        dataErrorText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        dataErrorText.setFill(Color.DARKRED);
        Hyperlink topFileInfoHyperlink = new Hyperlink("Click here for TOP format details");
        topFileInfoHyperlink
            .setFont(Font.font("Arial", FontPosture.ITALIC, processData.getFont().getSize() - 2));
        topFileInfoHyperlink.setBorder(Border.EMPTY);
        Separator dataInfoSeparator = new Separator();
        dataInfoSeparator.setStyle("-fx-background-color: darkgray;");
        VBox dataInfoVbox = new VBox();
        HBox dataInfoHBox = new HBox();
        dataInfoHBox.setAlignment(Pos.CENTER);
        Text dataInfoHeading = new Text(
            "File Name: \nNumber of Points: \nNumber of Routes: \nMax Distance per Route: ");
        dataInfoHeading.setFont(Font.font("Arial"));
        dataInfoHeading.setFill(Color.DIMGRAY);
        dataInfoText = new Text("");
        dataInfoText.setFont(dataInfoHeading.getFont());
        dataInfoText.setFill(dataInfoHeading.getFill());
        dataInfoHBox.getChildren().addAll(dataInfoHeading, dataInfoText);
        dataInfoVbox.getChildren().add(dataInfoHBox);

        Separator parameterSeparator = new Separator();
        parameterSeparator.setStyle("-fx-background-color: darkgray;");
        Text parameterTitle = new Text("Algorithm Parameters");
        parameterTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Text popSizeTitle = new Text("Population Size");
        popSizeTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 12.5));
        popSizeSpinner = new Spinner<>(10, 3000, 300, 10);
        popSizeSpinner.setEditable(true);

        Text tourTriesTitle = new Text("Max Failed Tries to Improve Initial Pop.");
        tourTriesTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 12.5));
        tourTriesSpinner = new Spinner<>(5, 200, 30, 5);
        tourTriesSpinner.setEditable(true);

        Text pCrossoverTitle = new Text("Crossover Probability %");
        pCrossoverTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 12.5));
        pCrossoverSpinner = new Spinner<>(10, 100, 75, 1);
        pCrossoverSpinner.setEditable(true);

        Text pMutateTitle = new Text("Mutation Probability %");
        pMutateTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 12.5));
        pMutateSpinner = new Spinner<>(10, 100, 25, 1);
        pMutateSpinner.setEditable(true);

        Text elitistReplacementTitle = new Text("Previous Generation Retention %");
        elitistReplacementTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 12.5));
        elitistReplacementSpinner = new Spinner<>(0, 100, 3, 1);
        elitistReplacementSpinner.setEditable(true);

        Text maxGenerationsTitle = new Text("Max No. of Generations per Route");
        maxGenerationsTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 12.5));
        maxGenerationsSpinner = new Spinner<>(1, 500, 200, 10);
        maxGenerationsSpinner.setEditable(true);

        Button runAlgorithm = new Button("Run Algorithm");

        Separator resultsSeparator = new Separator();
        resultsSeparator.setStyle("-fx-background-color: darkgray;");
        Text resultTitle = new Text("Results");
        resultTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        VBox resultVbox = new VBox();
        HBox resultHBox = new HBox();
        resultHBox.setAlignment(Pos.CENTER);
        Text resultHeading = new Text("Score: ");
        resultHeading.setFont(Font.font("Arial"));
        resultHeading.setFill(Color.DIMGRAY);
        resultText = new Text("    ");
        resultText.setFont(dataInfoHeading.getFont());
        resultText.setFill(dataInfoHeading.getFill());
        resultHBox.getChildren().addAll(resultHeading, resultText);
        resultVbox.getChildren().addAll(resultTitle, resultHBox);

        inputVBox.getChildren()
            .addAll(dataTitle, processData, topFileInfoHyperlink, dataErrorText, dataInfoSeparator,
                dataInfoVbox, parameterSeparator,
                parameterTitle, popSizeTitle, popSizeSpinner, tourTriesTitle, tourTriesSpinner,
                pCrossoverTitle, pCrossoverSpinner, pMutateTitle, pMutateSpinner,
                elitistReplacementTitle,
                elitistReplacementSpinner, maxGenerationsTitle, maxGenerationsSpinner, runAlgorithm,
                resultsSeparator, resultTitle,
                resultVbox);
        borderPane.setLeft(inputVBox);

        // formatting ScatterChart
        scatterChart.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);
        scatterChart.setHorizontalGridLinesVisible(false);
        scatterChart.setVerticalGridLinesVisible(false);
        scatterChart.setHorizontalZeroLineVisible(false);
        scatterChart.setVerticalZeroLineVisible(false);
        scatterChart.getXAxis().setTickLabelsVisible(false);
        scatterChart.getXAxis().setTickMarkVisible(false);
        scatterChart.getXAxis().lookup(".axis-minor-tick-mark").setVisible(false);
        scatterChart.getXAxis().setOpacity(0);
        scatterChart.getYAxis().setTickLabelsVisible(false);
        scatterChart.getYAxis().setTickMarkVisible(false);
        scatterChart.getYAxis().lookup(".axis-minor-tick-mark").setVisible(false); //
        scatterChart.getYAxis().setOpacity(0);
        scatterChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        scatterChart.setLegendVisible(false);
        scatterChart.setOpacity(0.5);
        scatterChart.setAnimated(false);

        // format LineChart
        lineChart.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.setHorizontalZeroLineVisible(false);
        lineChart.setVerticalZeroLineVisible(false);
        lineChart.getXAxis().setTickLabelsVisible(false);
        lineChart.getXAxis().setTickMarkVisible(false);
        lineChart.getXAxis().lookup(".axis-minor-tick-mark").setVisible(false);
        lineChart.getXAxis().setOpacity(0);
        lineChart.getYAxis().setTickLabelsVisible(false);
        lineChart.getYAxis().setTickMarkVisible(false);
        lineChart.getYAxis().lookup(".axis-minor-tick-mark").setVisible(false);
        lineChart.getYAxis().setOpacity(0);
        lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: ghostwhite;");
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        // remove the sorting policy of the LineChart to enable same start/end point paths. Code taken from: https://stackoverflow.com/a/33486295/2667225
        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);

        centerStackPane.getChildren().addAll(lineChart, scatterChart);

        // create a scene
        Scene scene = new Scene(borderPane);

        // set the stage dimensions
        primaryStage.setWidth(SCENE_WIDTH);
        primaryStage.setHeight(SCENE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        //// gui interactivity
        // click topFileInfoHyperlink
        topFileInfoHyperlink.setOnAction((ActionEvent e) -> {
            getHostServices().showDocument("https://www.mech.kuleuven.be/en/cib/op/instances/TOPformat/view");
        });

        // add points to ScatterChart
        processData.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select TOP File");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                datasetFile = selectedFile;
                allPointsSeries.getData().clear();
                clearCharts();
                dataErrorText.setText("");
                try {
                    setScatterChartData(datasetFile);
                } catch (IOException | IllegalArgumentException io) {
                    dataErrorText.setText("Invalid File!");
                    io.printStackTrace();
                }
            }
        });

        // run Algorithm
        runAlgorithm.setOnAction((ActionEvent e) -> {
            clearCharts();
            dataInfoText.setText(
                dataset.getFileName() + "\n" + dataset.getnPoints() + "\n" + dataset.getnRoutes()
                    + "\n" + dataset.gettMax());
            try {
                setScatterChartData(datasetFile);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            runAlgorithm();
        });
    }

    /**
     * setScatterChartData displays information related to fileToProcess on the GUI. If
     * fileToProcess is a valid TOP Format file it plots the points on the GUI, otherwise an error
     * message is displayed.
     *
     * @param fileToProcess The TOP Format file to process and display on the GUI.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    private void setScatterChartData(File fileToProcess) throws IOException {
        clearCharts();

        datasetFile = fileToProcess;
        dataset = TopData.generateDataFromTOPFile(datasetFile);
        dataInfoText.setText(
            dataset.getFileName() + "\n" + dataset.getnPoints() + "\n" + dataset.getnRoutes() + "\n"
                + dataset.gettMax());
        allPointsSeries.setName("All Points");
        for (Point p : dataset.getPoints()) {
            // create TopData from dataset's Points (3rd value is accessible via getExtraValue method)
            allPointsSeries.getData()
                .add(new XYChart.Data<>(p.getLongitude(), p.getLatitude(), p.getId()));
        }
        scatterChart.setData(FXCollections.observableArrayList(allPointsSeries));

        double minScore = (dataset.getPoints().stream().min(Comparator.comparing(Point::getScore))
            .orElseThrow(IllegalArgumentException::new)).getScore();
        double maxScore = (dataset.getPoints().stream().max(Comparator.comparing(Point::getScore))
            .orElseThrow(IllegalArgumentException::new)).getScore();
        int endingPointIndex = dataset.getPoints().size() - 1;

        // iterate through ScatterChart Data and add Tooltip for each with detail from Point
        for (XYChart.Series<Number, Number> series : scatterChart.getData()) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                double tempDouble = ((Number) data.getExtraValue()).doubleValue();
                int datasetPointIndex = (int) tempDouble - 1;
                Point pointForTooltip = dataset.getPoints().get(datasetPointIndex);
                String stringForTooltip =
                    "ID: " + pointForTooltip.getId() + "\nScore: " + pointForTooltip.getScore()
                        + "\nLongitude: " + pointForTooltip.getLongitude() + "\nLatitude: "
                        + pointForTooltip.getLatitude();
                Tooltip tooltip = new Tooltip(stringForTooltip);
                tooltip.setShowDelay(Duration.millis(0));
                Tooltip.install(data.getNode(), new Tooltip(stringForTooltip));

                // edit size of nodes based upon score
                double scoreScale = Util
                    .normaliseBetweenRange(pointForTooltip.getScore(), minScore, maxScore, 0.5,
                        1.5);
                data.getNode().setScaleX(scoreScale);
                data.getNode().setScaleY(scoreScale);

                // style starting or ending point nodes
                if (datasetPointIndex == 0 || datasetPointIndex == endingPointIndex) {
                    data.getNode().setStyle("-fx-background-color: deeppink");
                    data.getNode().setScaleX(1);
                    data.getNode().setScaleY(1);
                } else {
                    data.getNode().setStyle("-fx-background-color: grey");
                }
            }
        }
    }

    /**
     * clearCharts removes all information related to a dataset from the GUI.
     */
    private void clearCharts() {
        if (!lineChart.getData().isEmpty()) {
            for (int i = 0; i < lineChart.getData().size(); i++) {
                lineChart.getData().remove(i);
                i--;
            }
        }
        if (!scatterChart.getData().isEmpty()) {
            for (int i = 0; i < scatterChart.getData().size(); i++) {
                scatterChart.getData().remove(i);
                i--;
            }
        }

        dataInfoText.setText("     ");
        resultText.setText("    ");
    }

    /**
     * runAlgorithm is the GUI-specific equivalent of generateRoutesFromTOPFile generating a Result
     * as before and ensuring that this is displayed on the GUI correctly.
     * <p>
     * Note, hide Symbols code taken from: (https://stackoverflow.com/a/39674496, BadVegan, 2016)
     */
    private void runAlgorithm() {

        Algorithm algorithm = new Algorithm(dataset, popSizeSpinner.getValue(),
            tourTriesSpinner.getValue(),
            pCrossoverSpinner.getValue() / 100., pMutateSpinner.getValue() / 100.,
            elitistReplacementSpinner.getValue() / 100., maxGenerationsSpinner.getValue());
        dataInfoText.setText(
            dataset.getFileName() + "\n" + dataset.getnPoints() + "\n" + dataset.getnRoutes() + "\n"
                + dataset.gettMax());
        Result result = algorithm.generateRoutesFromTOPFile();
        resultText.setText(String.valueOf(result.getRoutesGeneratedCombinedScore()));

        // loop through routes created and create Series from them
        for (int i = 0; i < result.getRoutesGenerated().size(); i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            int routeNum = i + 1;
            series.setName("Route " + routeNum);
            for (Point p : result.getRoutesGenerated().get(i).getPoints()) {
                XYChart.Data<Number, Number> pointAsData = new XYChart.Data<>(p.getLongitude(),
                    p.getLatitude(), p.getScore());
                series.getData().add(pointAsData);
            }
            lineChart.getData().add(series);
            series.getNode().setOpacity(0.75);
        }

        // adding ScatterChart data to LineChart and hiding to ensure scaling matches exactly
        XYChart.Series<Number, Number> maintainScaleSeries = new XYChart.Series<>();
        maintainScaleSeries.setName("maintainScale");
        for (XYChart.Data<Number, Number> data : allPointsSeries.getData()) {
            XYChart.Data<Number, Number> pointAsData = new XYChart.Data<>(data.getXValue(),
                data.getYValue());
            maintainScaleSeries.getData().add(pointAsData);
        }
        lineChart.getData().add(maintainScaleSeries);

        // formatting to ensure that maintainScale Series is hidden
        maintainScaleSeries.getNode().setStyle(
            "-fx-background-color: transparent;" + "-fx-stroke: transparent;" + "-fx-padding: 0;"
                + "-fx-background-radius: 0;");

        // hide Symbols
        for (XYChart.Series<Number, Number> series : lineChart.getData()) {
            if (series.getName().equals("maintainScale")) {
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    StackPane stackPane = (StackPane) data.getNode();
                    stackPane.setVisible(false);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}