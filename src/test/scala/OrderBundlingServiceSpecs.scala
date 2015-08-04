package seglo

import TestData._
import org.specs2.execute.{Result, AsResult}
import org.specs2.specification.ForEach

/**
 * Injects new OrderBundlingService into each test case.
 */
trait OrderBundlingServiceContext extends ForEach[OrderBundlingService] {
  def foreach[R: AsResult](f: OrderBundlingService => R): Result = {
    AsResult(f(new OrderBundlingService(bundles)))
  }
}

class OrderBundlingServiceSpecs extends org.specs2.mutable.Specification with OrderBundlingServiceContext {
  "Two bundles applied to order for total of $7.00" >> { service: OrderBundlingService =>
    val twoBundleOrder = Order(Seq(apple, apple, apple, banana, grapefruit))

    val order = service.calculate(twoBundleOrder, bundles)

    order.total mustEqual BigDecimal(7.00)
    order.entries must containTheSameElementsAs(Seq(doubleAppleBundle, appleBananaBundle, grapefruit))
  }

  "No bundle can be applied, return original order total of $4.50" >> { service: OrderBundlingService =>
    val noBundleOrder = Order(Seq(apple, grapefruit))

    val order = service.calculate(noBundleOrder, bundles)

    order.total mustEqual BigDecimal(4.50)
    order.entries must containTheSameElementsAs(Seq(apple, grapefruit))
  }

  "The same bundle is applied to an order twice" >> { service: OrderBundlingService =>
    val sameBundleOrder = Order(Seq(apple, apple, apple, apple))

    val order = service.calculate(sameBundleOrder, bundles)

    order.total mustEqual BigDecimal(4.00)
    order.entries must containTheSameElementsAs(Seq(doubleAppleBundle, doubleAppleBundle))
  }

  "Return same order back when no bundles supplied" >> { service: OrderBundlingService =>
    val appleBananaOrder = Order(Seq(apple, banana))

    val order = service.calculate(appleBananaOrder, Set.empty[Bundle])

    order.total mustEqual BigDecimal(2.50)
    order.entries must containTheSameElementsAs(appleBananaOrder.entries)
  }

  "Mega fruit order finds lowest price with bundles and save $6.50" >> { service: OrderBundlingService =>
    val megaFruitOrder = Order(Seq(apple, banana, grapefruit, apple, apple, banana, grapefruit, banana, apple, grapefruit, grapefruit, apple, banana, banana, banana, apple, grapefruit, banana, apple))

    val order = service.calculate(megaFruitOrder, bundles)

    // save 6.50!
    megaFruitOrder.total mustEqual BigDecimal(32.50)
    order.total mustEqual BigDecimal(26.00)

    order.entries must containTheSameElementsAs(
      Seq(
        grapefruit,
        banana,
        grapefruit,
        banana,
        appleBananaBundle,
        appleBananaBundle,
        threeGrapefruitBundle,
        doubleAppleBundle,
        appleBananaBundle,
        appleBananaBundle,
        appleBananaBundle))
  }
}
