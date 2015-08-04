import org.specs2.execute.{Result, AsResult}
import org.specs2.specification.ForEach

import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration.Duration
import ExecutionContext.Implicits.global

import TestData._

/**
 * Injects new OrderApi into each test case.
 */
trait OrderApiContext extends ForEach[OrderApi] {
  def foreach[R: AsResult](f: OrderApi => R): Result = {
    AsResult(f(new OrderApi(catalog, new OrderBundlingService(bundles))))
  }
}

class OrderApiSpecs extends org.specs2.mutable.Specification with OrderApiContext {
  "Checkout an order with an applicable bundle and return its total successfully" >> { api: OrderApi =>
    val checkoutFuture = api.checkout(orderDto)

    Await.result(checkoutFuture, Duration.Inf) mustEqual BigDecimal(5.00)
  }

  "Checkout an order with no catalog items" >> { api: OrderApi =>
    val checkoutFuture = api.checkout(OrderDto(Nil))

    Await.result(checkoutFuture.failed, Duration.Inf) mustEqual
      InvalidOrderException("Your order contains no items.")
  }

  "Checkout an order with invalid catalog items: Guava & Pamplemousse" >> { api: OrderApi =>
    val checkoutFuture = api.checkout(OrderDto(Seq(
      CatalogItemDto("Guava"),
      CatalogItemDto("Pamplemousse"))))

    Await.result(checkoutFuture.failed, Duration.Inf) mustEqual
      InvalidOrderException("Your order contains catalog items do not exist: Guava, Pamplemousse.")
  }

  "Submit 1000 identical orders successfully" >> { api: OrderApi =>
    val futures = (1 to 1000).map(i => api.checkout(orderDto))

    val orderTotals = Await.result(Future.sequence(futures), Duration.Inf)

    orderTotals.foreach(_ mustEqual BigDecimal(5.00))
    success
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

    val api = new OrderApi(catalog, new OrderBundlingService(Set(appleBananaBundle)))

    val checkoutFuture = api.checkout(orderDto)

    Await.result(checkoutFuture, Duration.Inf) mustEqual BigDecimal(5.00)
  }
}
