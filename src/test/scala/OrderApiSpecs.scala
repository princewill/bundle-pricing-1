package seglo

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import TestData._

class OrderApiSpecs extends org.specs2.mutable.Specification {
  // TODO:
  // add a beforeEach to instantiate Api
  // put common test data somewhere else?

  "Checkout an order with an applicable bundle and return its total successfully" >> {
    val orderApi = new OrderApi(catalog, bundles)

    val checkoutFuture = orderApi.checkout(orderDto)

    Await.result(checkoutFuture, Duration.Inf) mustEqual BigDecimal(5.00)
  }

  "Checkout an order with no catalog items" >> {
    val orderApi = new OrderApi(catalog, bundles)

    val checkoutFuture = orderApi.checkout(OrderDto(Nil))

    Await.result(checkoutFuture.failed, Duration.Inf) mustEqual
      InvalidOrderException("Your order contains no items.")
  }

  "Checkout an order with invalid catalog items: Guava & Pamplemousse" >> {
    val orderApi = new OrderApi(catalog, bundles)

    val checkoutFuture = orderApi.checkout(OrderDto(Seq(
      CatalogItemDto("Guava"),
      CatalogItemDto("Pamplemousse"))))

    Await.result(checkoutFuture.failed, Duration.Inf) mustEqual
      InvalidOrderException("Your order contains catalog items do not exist: Guava, Pamplemousse.")
  }

  "Example usage from README" >> {
    val appleDto = CatalogItemDto("Apple")
    val bananaDto = CatalogItemDto("Banana")
    val grapefruitDto = CatalogItemDto("Grapefruit")

    val orderDto = OrderDto(Seq(appleDto, bananaDto, grapefruitDto))

    val apple = CatalogItem("Apple", BigDecimal(1.50))
    val banana = CatalogItem("Banana", BigDecimal(1.00))
    val grapefruit = CatalogItem("Grapefruit", BigDecimal(3.00))

    val catalog = Set(apple, banana, grapefruit)

    val appleBananaBundle = Bundle("Apple & Banana Bundle",
      Seq(apple, banana), BigDecimal(2.00))

    val orderApi = new OrderApi(catalog, Set(appleBananaBundle))

    val checkoutFuture = orderApi.checkout(orderDto)

    Await.result(checkoutFuture, Duration.Inf) mustEqual BigDecimal(5.00)
  }
}
