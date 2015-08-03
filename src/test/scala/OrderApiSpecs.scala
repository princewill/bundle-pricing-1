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
}
