//package PokemonEditor.visualisation;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javafx.application.Application;
//import javafx.event.EventHandler;
//import javafx.scene.Scene;
//import javafx.scene.chart.LineChart;
//import javafx.scene.chart.NumberAxis;
//import javafx.scene.chart.XYChart;
//import javafx.scene.input.MouseEvent;
//import javafx.stage.Stage;
//
//public class LineChartSample extends Application
//{
//   public static final String JSON_FILE_PATH = "C:/Entwicklung/jmh/" +
//                                               "Testklasse.json";
//   private Scene scene;
//   private int indexOfMethod = 0;
//
//   public static void main(String[] args)
//   {
//      launch(args);
//   }
//
//   @Override
//   public void start(Stage stage)
//   {
//      stage.setTitle("JMH Ergebnisse Methode 1");
//      final List<JMHResultDto> jmhResultDto = VisualizationUtilForJMHResults.getJMHResultDtoFromJsonFilePath(JSON_FILE_PATH);
//      List<LineChart<Number, Number>> lineChartList = new ArrayList<>();
//      //Creating the mouse event handler
//      EventHandler<MouseEvent> eventHandlerChangeMethod = e ->
//      {
//         if(indexOfMethod == jmhResultDto.size() - 1)
//         {
//            indexOfMethod = 0;
//         }
//         else
//         {
//            indexOfMethod++;
//         }
//         System.out.println("Changed method");
//         scene.setRoot(lineChartList.get(indexOfMethod));
//      };
//
//      //Adding event Filter to every lineChart
//      for(int i = 0; i < jmhResultDto.size(); i++)
//      {
//         lineChartList.add(createNewLineChart(i));
//         lineChartList.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandlerChangeMethod);
//      }
//      //indexOfMethod ist hier immer 0, aber wer mag schon MagicNumbers?
//      //Die Scene wird später durch gewechselt.
//      scene = new Scene(lineChartList.get(indexOfMethod), 800, 600);
//      stage.setScene(scene);
//      stage.show();
//   }
//
//   private LineChart<Number, Number> createNewLineChart(int indexOfMethod)
//   {
//      final List<JMHResultDto> jmhResultDto = VisualizationUtilForJMHResults.getJMHResultDtoFromJsonFilePath(JSON_FILE_PATH);
//      if(jmhResultDto.size() < indexOfMethod)
//      {
//         throw new RuntimeException("Es gibt nur " +
//                                    jmhResultDto.size() +
//                                    " Methoden die getestetet wurden. Ein Zugriff " +
//                                    "auf die " +
//                                    indexOfMethod +
//                                    ". Methode ist dementsprechend nicht möglich!");
//      }
//      //defining the axes
//      final NumberAxis xAxis = new NumberAxis();
//      final NumberAxis yAxis = new NumberAxis();
//      xAxis.setLabel("Iteration");
//      yAxis.setLabel(jmhResultDto.get(indexOfMethod).getPrimaryMetric().getScoreUnit());
//      //defining a series //TODO EINE FÜR JEDE METHODE!!!!!!!
//      XYChart.Series<Number, Number> series = new XYChart.Series<>();
//      final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
//      // Hier wird der Methodenname gezogen
//      final String benchmark = jmhResultDto.get(indexOfMethod).getBenchmark();
//      final String[] split = benchmark.split("\\.");
//      final String lastPart = split[(split.length - 1)];
//      final String nextToLastPart = split[(split.length - 2)];
//      lineChart.setTitle("Klasse: " + nextToLastPart);
//      // Diagramm-Linie wird erstellt mit den RawData (für jede Methode eine Diagramm-Linie)
//      //Größe der Raw Data Liste = Anzahl der Iterationen | x = Iteration | y = Messwert
//      //Füge RawData als Koordinaten zur Linie hinzu
//      for(int j = 0; j < jmhResultDto.get(indexOfMethod).getPrimaryMetric().getRawData().get(0).size(); j++)
//      {
//         series.getData().add(new XYChart.Data<>(j + 1, jmhResultDto.get(indexOfMethod).getPrimaryMetric().getRawData().get(0).get(j)));
//      }
//      series.setName("Methode: " + lastPart);
//      // Hinzufügen der fertig konfigurierten Diagramm-Linien zum Diagramm
//      lineChart.getData().add(series);
//      return lineChart;
//   }
//}