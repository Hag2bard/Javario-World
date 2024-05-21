//package PokemonEditor.visualisation;
//
//import de.shd.kps.backend.core.common.dto.BaseVersionedLongIDDto;
//import jakarta.validation.constraints.NotEmpty;
//import lombok.Data;
//
//@Data
//public class JMHResultDto extends BaseVersionedLongIDDto
//{
//   @NotEmpty
//   private String jmhVersion;
//   @NotEmpty
//   private String benchmark;
//   @NotEmpty
//   private String mode;
//   @NotEmpty
//   private Long threads;
//   @NotEmpty
//   private Long forks;
//   @NotEmpty
//   private String jvm;
//   @NotEmpty
//   private String[] jvmArgs;
//   @NotEmpty
//   private String jdkVersion;
//   @NotEmpty
//   private String vmName;
//   @NotEmpty
//   private String vmVersion;
//   @NotEmpty
//   private Long warmupIterations;
//   @NotEmpty
//   private String warmupTime;
//   @NotEmpty
//   private Long warmupBatchSize;
//   @NotEmpty
//   private Long measurementIterations;
//   @NotEmpty
//   private String measurementTime;
//   @NotEmpty
//   private Long measurementBatchSize;
//   @NotEmpty
//   private MetricsDto primaryMetric;
//   private MetricsDto secondaryMetrics;
//}
