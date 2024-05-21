//package PokemonEditor.visualisation;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//import de.shd.kps.backend.common.util.JsonUtil;
//import lombok.CustomLog;
//import org.apache.commons.io.IOUtils;
//
///**
// * Dieses Util soll später zur Visualisierung der Benchmark-Ergebnisse dienen
// */
//public class VisualizationUtilForJMHResults
//{
//   public static List<JMHResultDto> getJMHResultDtoFromJsonFilePath(String jsonFilePath)
//   {
//      File jsonFile = new File(jsonFilePath);
//      try
//      {
//         JsonUtil.fromJsonAsList(jsonFile, JMHResultDto.class);
//         return JsonUtil.fromJsonAsList(jsonFile, JMHResultDto.class);
//      }
//      catch(IOException e)
//      {
//         throw new RuntimeException(e);
//      }
//   }
//
//   /**
//    * Lädt Dateien als Stream und wandelt sie zu einem String um
//    *
//    * @param jsonFileName Dateiname der Resource
//    * @return JsonString
//    * @throws IOException Unklar wann IOException geworfen wird
//    */
//   public String getJsonStringFromFileNameOutOfResources(String jsonFileName) throws IOException
//   {
//      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//      InputStream inputStream = classLoader.getResourceAsStream(jsonFileName);
//      try
//      {
//         if(inputStream == null)
//         {
//            throw new IOException("Cannot find file " + jsonFileName);
//         }
//      }
//      catch(IOException e)
//      {
//         LOG.error(e::getMessage);
//         System.exit(0);
//      }
//      return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//   }
//
//}
//
////JMHResultDto jmhResultDto = new JMHResultDto();
////jmhResultDto.setJmhVersion("1.35");
////jmhResultDto.setBenchmark("testKlasse");
////jmhResultDto.setMode("avgt");
////jmhResultDto.setThreads(1L);
////jmhResultDto.setForks(0L);
////jmhResultDto.setJvm("S:\\\\Entwicklung\\\\software\\\\jdk-17\\\\bin\\\\java.exe");
////jmhResultDto.setJvmArgs(new String[]{"-server"});
////jmhResultDto.setJdkVersion("17.0.7");
////jmhResultDto.setVmName("OpenJDK 64-Bit Server VM");
////jmhResultDto.setVmVersion("17.0.7.+7");
////jmhResultDto.setWarmupIterations(10L);
////jmhResultDto.setWarmupTime("10 s");
////jmhResultDto.setWarmupBatchSize(1L);
////jmhResultDto.setMeasurementIterations(20L);
////jmhResultDto.setMeasurementTime("10 s");
////jmhResultDto.setMeasurementBatchSize(1L);
////List<MetricsDto> metricsDtoList = new ArrayList<>();
////MetricsDto metricsDto = new MetricsDto();
////metricsDto.setScore(89336.02844279037);
////metricsDto.setScoreError(13918.28860287423);
////List<Double> scoreConfidence = new ArrayList<>();
////scoreConfidence.add(65432.739999999);
////scoreConfidence.add(12345.739999999);
////metricsDto.setScoreConfidence(scoreConfidence);
////ScorePercentiles scorePercentiles = new ScorePercentiles();
////scorePercentiles.setPercentage0_0(0123.0123);
////scorePercentiles.setPercentage50_0(0123.0123);
////scorePercentiles.setPercentage90_0(0123.0123);
////scorePercentiles.setPercentage95_0(0123.0123);
////scorePercentiles.setPercentage99_0(0123.0123);
////scorePercentiles.setPercentage99_9(0123.0123);
////scorePercentiles.setPercentage99_99(0123.0123);
////scorePercentiles.setPercentage99_999(0123.0123);
////scorePercentiles.setPercentage99_9999(0123.0123);
////scorePercentiles.setPercentage100_0(0123.0123);
////metricsDto.setScorePercentiles(scorePercentiles);
////metricsDto.setScoreUnit("us/op");
////List<Double> listOfDouble = new ArrayList<>();
////listOfDouble.add(1111.1111111111);
////listOfDouble.add(222.222222222);
////listOfDouble.add(33.3333333);
////listOfDouble.add(4.44444);
////List<List<Double>> listOfListOfDouble = new ArrayList<>();
////listOfListOfDouble.add(listOfDouble);
////metricsDto.setRawData(listOfListOfDouble);
////metricsDto.setIsPersisted(false);
////metricsDto.setIsDeleted(false);
////
//////metricsDtoList.add(metricsDto);
////jmhResultDto.setPrimaryMetric(metricsDto);
////jmhResultDto.setSecondaryMetrics(metricsDto);
////
////List<JMHResultDto> jmhResultDtoList = new ArrayList<>();
////jmhResultDtoList.add(jmhResultDto);
////jmhResultDtoList.add(jmhResultDto);
////
////final String jsonStringFromObject = JsonUtil.toJson(jmhResultDtoList);
////System.out.println(jsonStringFromObject);
