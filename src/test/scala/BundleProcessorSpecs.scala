package seglo

class BundleProcessorSpecs extends org.specs2.mutable.Specification {
  val apple = CatalogItem("Apple", BigDecimal(1.50))
  val banana = CatalogItem("Banana", BigDecimal(1.00))
  val grapefruit = CatalogItem("Grapefruit", BigDecimal(3.00))

  val doubleAppleBundle = Bundle("Double Apple Bundle",
    Seq(apple, apple), BigDecimal(2.00))
  val appleBananaBundle = Bundle("Apple & Banana Bundle",
    Seq(apple, banana), BigDecimal(2.00))
  val threeGrapefruitBundle = Bundle("Three Grapefruit Bundle",
    Seq(grapefruit, grapefruit, grapefruit), BigDecimal(6.00))

  val allBundles = Set(doubleAppleBundle, appleBananaBundle, threeGrapefruitBundle)

  "Two bundles applied to order for total of $7.00" >> {
    val twoBundleOrder = Order(Seq(apple, apple, apple, banana, grapefruit))

    val order = BundleProcessor.calculate(twoBundleOrder, allBundles)

    order.total mustEqual BigDecimal(7.00)
    order.entries must containTheSameElementsAs(Seq(doubleAppleBundle, appleBananaBundle, grapefruit))
  }

  "No bundle can be applied, return original order total of $4.50" >> {
    val noBundleOrder = Order(Seq(apple, grapefruit))

    val order = BundleProcessor.calculate(noBundleOrder, allBundles)

    order.total mustEqual BigDecimal(4.50)
    order.entries must containTheSameElementsAs(Seq(apple, grapefruit))
  }

  "The same bundle is applied to an order twice" >> {
    val sameBundleOrder = Order(Seq(apple, apple, apple, apple))

    val order = BundleProcessor.calculate(sameBundleOrder, allBundles)

    order.total mustEqual BigDecimal(4.00)
    order.entries must containTheSameElementsAs(Seq(doubleAppleBundle, doubleAppleBundle))
  }

  "Return same order back when no bundles supplied" >> {
    val appleBananaOrder = Order(Seq(apple, banana))

    val order = BundleProcessor.calculate(appleBananaOrder, Set.empty[Bundle])

    order.total mustEqual BigDecimal(2.50)
    order.entries must containTheSameElementsAs(appleBananaOrder.entries)
  }

  "Mega fruit order finds lowest price with bundles and saves $6.50" >> {
    val megaFruitOrder = Order(Seq(apple, banana, grapefruit, apple, apple, banana, grapefruit, banana, apple, grapefruit, grapefruit, apple, banana, banana, banana, apple, grapefruit, banana, apple))

    val order = BundleProcessor.calculate(megaFruitOrder, allBundles)

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
