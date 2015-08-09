package seglo

import org.scalameter.Key
import org.scalameter.api._
import seglo.optimizations._
import TestData._

object OrderBundlingServicePerfTest extends PerformanceTest.Microbenchmark {

  //  lazy val executor = LocalExecutor(
  //    new Executor.Warmer.Default,
  //    Aggregator.min,
  //    new Measurer.Default)
  //  lazy val reporter = new LoggingReporter
  //  lazy val persistor = Persistor.None

  val service = new OrderBundlingService(bundles)
  //val service = new OrderBundlingServiceSubBundler(bundles)
  //val service = new OrderBundlingServiceLeaves(bundles)
  //val service = new OrderBundlingServiceImperative(bundles)

  // Sean's laptop:
  //  cores: 4
  //  hostname: bit
  //  jvm-name: Java HotSpot(TM) 64-Bit Server VM
  //  jvm-vendor: Oracle Corporation
  //    jvm-version: 25.45-b02
  //  os-arch: amd64
  //  os-name: Linux

  val sizes = Gen.range("Order Size")(10, 20, 5)

  val c = catalog.toSeq
  val cLength = c.length

  val orderRanges = for {
    size <- sizes
  } yield Order((1 to size).map(i => c(i % cLength)))

  performance of "Bundling Service Implementations" config (Key.verbose -> false) in {
    measure method service.getClass.getSimpleName + ": calculate" in {
      using(orderRanges) in { order: Order =>
        service.calculate(order)
      }
    }
    measure method service.getClass.getSimpleName + ": orderPermutations" in {
      using(orderRanges) in { order: Order =>
        service.orderPermutations(order, bundles)
      }
    }
  }

  //OrderBundlingService - permutations
  //Parameters(Order Size -> 10): 0.143954
  //Parameters(Order Size -> 15): 0.534757
  //Parameters(Order Size -> 20): 3.605756

  //OrderBundlingService - calculate
  //Parameters(Order Size -> 10): 0.169821
  //Parameters(Order Size -> 15): 0.652358
  //Parameters(Order Size -> 20): 4.103961

  //OrderBundlingServiceSubBundler - permutations
  //Parameters(Order Size -> 10): 0.080211
  //Parameters(Order Size -> 15): 0.544333
  //Parameters(Order Size -> 20): 4.003476

  //OrderBundlingServiceLeaves - permutations
  //Parameters(Order Size -> 10): 0.097688
  //Parameters(Order Size -> 15): 0.437974
  //Parameters(Order Size -> 20): 3.992958

  //OrderBundlingServiceImperative - calculate
  //Parameters(Order Size -> 10): 0.057408
  //Parameters(Order Size -> 15): 0.264279
  //Parameters(Order Size -> 20): 1.971949

  //OrderBundlingServiceImperative - permuations
  //Parameters(Order Size -> 10): 0.071707
  //Parameters(Order Size -> 15): 0.254889
  //Parameters(Order Size -> 20): 1.506086
}
