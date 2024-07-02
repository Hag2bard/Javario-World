package PokemonEditor;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.NoBenchmarksException;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

public abstract class AbstractBenchmarkHarnessForTests
{
   private final TimeUnit JMH_TIME_UNIT = TimeUnit.MICROSECONDS;
   private final Integer MEASUREMENT_ITERATIONS = 10; // minimum value is 1 // default value is 20
   private final Integer WARMUP_ITERATIONS = 5;      // minimum value is 0 // default value is 10 // Die Ergebnisse der Warmup Iterations werden weggeschmissen
   private final Integer FORKS = 5; //0 is for debugging or for Spring Context Tests
   private final Integer THREADS = 1; // Dem Prozessor anpassen!
   private final Mode BENCHMARK_MODE = Mode.AverageTime;
   private final String RESULT_FILE_PATH = "/home/dch/Dokumente/IntelliJ/Javario-World/jmh/";
   private Options options;

   public AbstractBenchmarkHarnessForTests()
   {
      options = createDefaultOptions();
   }

   /**
    * Die JUnit-Engine ruft @Test-Methode auf und übergibt ApplicationContext.
    * Dieser wird in SpringContext.class gespeichert und der JMH-Runner wird gestartet.
    * Der JMH-Runner ruft erbende Klassen auf, welche mit @State(Scope.Benchmark oder Scope.Group)
    * annotiert sind. In diesen Klassen called der JMH-Runner die mit @Setup annotierten Methoden
    * (Äquivalent zu @BeforeEach). Es gibt kein Äquivalent zu @BeforeAll!
    * Anschließend wird eine @Benchmark annotierte Methoden gebencht.
    * Zum Schluss werden @TearDown-annotierte Methoden aufgerufen
    *
    * @throws RunnerException Wenn keine Benchmark-Methoden gefunden wurden
    */
   @Test
   public void createOptionsAndRunBenchmark() throws RunnerException
   {
      try
      {
         //TODO DCH LOG
//         LOG.info(() -> "JMH Benchmark started!");
         new Runner(options).run();
      }
      catch(NoBenchmarksException e)
      {
//         LOG.error(() -> "Keine Benchmark-Methoden in erbender Klasse und Scope gefunden! " + e.getMessage());
      }
   }

   private Options createDefaultOptions()
   {
      return new OptionsBuilder()
         // set the class name regex for benchmarks to search for to the current class
         .include("\\." + this.getClass().getSimpleName() + "\\.")
         .warmupIterations(WARMUP_ITERATIONS)
         .measurementIterations(MEASUREMENT_ITERATIONS)
         .mode(BENCHMARK_MODE)
         .timeUnit(JMH_TIME_UNIT)
         // do not use forking or the benchmark methods will not see references stored within its class
         // Forks dienen dazu Varianzen von Lauf zu Lauf abzumildern, die durch verschiedene Einflussfaktoren zustande kommen, wie
         // z.B.: CPU-Thermal-Throttling, Paging, Hintergrundprozesse, CPU-Zuweisung durch OS
         // Benchmark-Methoden werden ab forks(1) in separater JVM gestartet, können dadurch aber nicht mehr auf den 'injected ApplicationContext',
         // welcher in der Klasse gespeichert ist, zugreifen.
         .forks(FORKS)
         // TODO DCH DB-Zugriffe Problematisch
         .threads(THREADS)
         // Garbage Collection zwischen den Measurements wird angestoßen
         // Should do GC between measurementIterations?
         .shouldDoGC(false)
         // Entscheidung ob der Junit-Runner abbricht bei Fehlschlag einer Benchmark-Methode
         .shouldFailOnError(true)
         .resultFormat(ResultFormatType.JSON)
         // Output filename to write the result to
         .result(getFileName(RESULT_FILE_PATH))
         // Zwei Varianten der VM, optimiert für Client- oder Server-Anwendungen. Es werden unterschiedliche Compiler genutzt
         // Client-Compiler (schnelle Startzeiten, geringer Speicherverbrauch); Server-Compiler (langfristige Leistungsoptimierung)
         .jvmArgs("-server")
         //.jvmArgs("-client")
         // Output filename to write the run log to
         //.output(getFileName(LOG_FILE_PATH))
         // Verbose Mode, Default = Normal
         .verbosity(VerboseMode.EXTRA)

         .build();
   }

   public void createNewOptions(
      Integer warmupIterations,
      Integer measurementIterations,
      Mode benchmarkMode,
      TimeUnit timeUnit,
      Integer forks,
      Integer threads)
   {
      options = new OptionsBuilder()
         .include("\\." + this.getClass().getSimpleName() + "\\.")
         .warmupIterations(warmupIterations)
         .measurementIterations(measurementIterations)
         .mode(benchmarkMode)
         .timeUnit(timeUnit)
         .forks(forks)
         .threads(threads)
         .shouldDoGC(true)
         .shouldFailOnError(true)
         .resultFormat(ResultFormatType.JSON)
         .result(getFileName(RESULT_FILE_PATH))
         .jvmArgs("-server")
         .verbosity(VerboseMode.EXTRA)
         .build();
   }

   public Options getOptions()
   {
      return options;
   }

   public void setOptions(Options options)
   {
      this.options = options;
   }

   private String getFileName(String logFilePathAsString)
   {
      return logFilePathAsString + this.getClass().getName() + ".json";
   }

}
