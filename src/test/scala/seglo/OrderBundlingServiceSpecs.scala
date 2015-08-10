package seglo

import seglo.optimizations._
import TestData._

class OrderBundlingServiceSpecs extends org.specs2.mutable.Specification {
  /**
   * Assert base and optimization implementations of the OrderBundlingService
   */
  Seq(
    new OrderBundlingService(bundles), /* base implementation */
    new OrderBundlingServiceSubBundler(bundles),
    //new OrderBundlingServiceLeaves(bundles),
    new OrderBundlingServiceImperative(bundles)) foreach { service =>

    service.getClass.getSimpleName >> {

      "Two bundles applied to order for total of $7.00" >> {
        val twoBundleOrder = Order(Seq(apple, apple, apple, banana, grapefruit))

        val order = service.calculate(twoBundleOrder, bundles)

        order.total mustEqual BigDecimal(7.00)
        order.entries must containTheSameElementsAs(Seq(doubleAppleBundle, appleBananaBundle, grapefruit))
      }

      "No bundle can be applied, return original order total of $4.50" >> {
        val noBundleOrder = Order(Seq(apple, grapefruit))

        val order = service.calculate(noBundleOrder, bundles)

        order.total mustEqual BigDecimal(4.50)
        order.entries must containTheSameElementsAs(Seq(apple, grapefruit))
      }

      "The same bundle is applied to an order twice" >> {
        val sameBundleOrder = Order(Seq(apple, apple, apple, apple))

        val order = service.calculate(sameBundleOrder, bundles)

        order.total mustEqual BigDecimal(4.00)
        order.entries must containTheSameElementsAs(Seq(doubleAppleBundle, doubleAppleBundle))
      }

      "Return same order back when no bundles supplied" >> {
        val appleBananaOrder = Order(Seq(apple, banana))

        val order = service.calculate(appleBananaOrder, Set.empty[Bundle])

        order.total mustEqual BigDecimal(2.50)
        order.entries must containTheSameElementsAs(appleBananaOrder.entries)
      }

      "Mega fruit order finds lowest price with bundles and save $6.50" >> {
        val order = service.calculate(megaFruitOrder, bundles)

        // save 6.50!
        megaFruitOrder.total mustEqual BigDecimal(32.50)
        order.total mustEqual BigDecimal(26.00)

// there are more than 1 order combinations at this price
//        order.entries must containTheSameElementsAs(
//          Seq(
//            grapefruit,
//            banana,
//            grapefruit,
//            banana,
//            appleBananaBundle,
//            appleBananaBundle,
//            threeGrapefruitBundle,
//            doubleAppleBundle,
//            appleBananaBundle,
//            appleBananaBundle,
//            appleBananaBundle))
      }

//      "Mega fruit order has n lowest priced orders" >> {
//        val orders = service.orderPermutations(megaFruitOrder, bundles)
//
//        val groupedOrders = orders.groupBy(_.total)
//        val lowestPriceOrders = groupedOrders(groupedOrders.keys.min)
//        println(service.getClass.getSimpleName + ": " + lowestPriceOrders.size)
//
//        success
//      }
    }
  }
}
