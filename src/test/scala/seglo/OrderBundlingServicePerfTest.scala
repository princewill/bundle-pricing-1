package seglo

import org.scalameter.api._
import TestData._

object OrderBundlingServicePerfTest extends PerformanceTest.Microbenchmark {
  val service = new OrderBundlingService(bundles)

  val sizes = Gen.range("Order Size")(10, 20, 5)

  val c = catalog.toSeq
  val cLength = c.length

  val orderRanges = for {
    size <- sizes
  } yield Order((1 to size).map(i => c(i % cLength)))

  performance of "OrderBundlingService" in {
    measure method "calculate" in {
      using(orderRanges) in { order: Order =>
        service.calculate(order)
      }
    }
  }
}
