package seglo

object TestData {
  val apple = CatalogItem("Apple", BigDecimal(1.50))
  val banana = CatalogItem("Banana", BigDecimal(1.00))
  val grapefruit = CatalogItem("Grapefruit", BigDecimal(3.00))

  val catalog = Set(apple, banana, grapefruit)

  val doubleAppleBundle = Bundle("Double Apple Bundle",
    Seq(apple, apple), BigDecimal(2.00))
  val appleBananaBundle = Bundle("Apple & Banana Bundle",
    Seq(apple, banana), BigDecimal(2.00))
  val threeGrapefruitBundle = Bundle("Three Grapefruit Bundle",
    Seq(grapefruit, grapefruit, grapefruit), BigDecimal(6.00))

  val bundles = Set(doubleAppleBundle, appleBananaBundle, threeGrapefruitBundle)

  val appleDto = CatalogItemDto("Apple")
  val bananaDto = CatalogItemDto("Banana")
  val grapefruitDto = CatalogItemDto("Grapefruit")

  val orderDto = OrderDto(Seq(appleDto, bananaDto, grapefruitDto))
}
