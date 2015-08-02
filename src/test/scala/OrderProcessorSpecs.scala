package seglo

import org.joda.money.{Money, CurrencyUnit}
import org.specs2._

class OrderProcessorSpecs extends org.specs2.mutable.Specification {
  val cad = CurrencyUnit.of("CAD")

  val toothBrush = CatalogItem("Toothbrush", 2.00d)
  val toothPaste = CatalogItem("Toothpaste", 4.00d)
  val dentalFloss = CatalogItem("Dental Floss", 2.50d)

  val catalog = Set(toothBrush, toothPaste, dentalFloss)

  val dentalHygieneBundle = Bundle(Set(
    OrderItem(toothBrush, 1),
    OrderItem(toothPaste, 1),
    OrderItem(dentalFloss, 1)), 6.00d)

  "Generate an order" >> {
    val order = Order(Set(OrderItem(toothBrush, 2)))
    order.items.size mustEqual 1
  }

  "Calculate quantity price of of an order item" >> {
    val twoToothbrushes = OrderItem(toothBrush, 2)

    val orderProcessor = new OrderProcessor(Set.empty, Set.empty)
    orderProcessor.quantityPrice(twoToothbrushes) mustEqual Money.of(cad, 4.00d)
  }

  "Calculate order total with no bundles and one of each item" >> {
    val order = Order(Set(OrderItem(toothBrush, 1), OrderItem(toothPaste, 1)))

    val orderProcessor = new OrderProcessor(catalog, Set.empty)
    orderProcessor.total(order).price mustEqual Money.of(cad, 6.00d)
  }

  "Calculate order total with no bundles items with quantity greater than 1" >> {
    val order = Order(Set(OrderItem(toothBrush, 3), OrderItem(toothPaste, 2)))

    val orderProcessor = new OrderProcessor(catalog, Set.empty)
    orderProcessor.total(order).price mustEqual Money.of(cad, 14.00d)
  }

  "Order contains a bundle" >> {
    val order = Order(Set(
      OrderItem(toothBrush, 2),
      OrderItem(toothPaste, 1),
      OrderItem(dentalFloss, 1)))

    val orderProcessor = new OrderProcessor(catalog, Set(dentalHygieneBundle))
    orderProcessor.orderContainsBundle(order, dentalHygieneBundle) must beTrue
  }
}
