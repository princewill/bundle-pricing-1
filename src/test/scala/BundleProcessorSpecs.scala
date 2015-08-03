package seglo

import org.joda.money.Money
import org.specs2._

class BundleProcessorSpecs extends org.specs2.mutable.Specification {
  val apple = CatalogItem("Apple", 1.50d)
  val banana = CatalogItem("Banana", 1.00d)
  val grapefruit = CatalogItem("Grapefruit", 3.00d)

  val doubleAppleBundle = Bundle("Double Apple Bundle", Seq(apple, apple), 2.00d)
  val appleBananaBundle = Bundle(
    "Apple & Banana Bundle",
    Seq(apple, banana),
    2.00d)
  val threeGrapefruitBundle = Bundle("Three Grapefruit Bundle",
    Seq(grapefruit, grapefruit, grapefruit), 6.00d)

  val allBundles = Set(doubleAppleBundle, appleBananaBundle, threeGrapefruitBundle)

  val twoBundleOrder = Order(Seq(apple, apple, apple, banana, grapefruit))

//  "OrderProcessor" >> {

//    "An order contains a bundle" >> {
//      BundleProcessor.orderContainsBundle(twoBundleOrder, doubleAppleBundle) must beTrue
//    }
//
//    "Get 2/3 possible bundles" >> {
//      val bundles = BundleProcessor.possibleBundles(twoBundleOrder, allBundles)
//      bundles.size mustEqual 2
//    }

    "Get smallest order" >> {
      val order = BundleProcessor.calculate(twoBundleOrder, allBundles)
      order.price mustEqual Money.of(order.cad, 7.00d).getAmount
    }
//  }
}
