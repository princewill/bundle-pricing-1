package seglo

import org.joda.money.{CurrencyUnit, Money}

case class Order(items: Set[OrderItem])
case class OrderTotal(order: Order, bundles: Set[Bundle], price: Money)
case class OrderItem(catalogItem: CatalogItem, quantity: Int)
case class CatalogItem(itemName: String, price: Double)
case class Bundle(items: Set[BundleItem], price: Double)
case class BundleItem(catalogItem: CatalogItem, quantity: Int)

class OrderProcessor(catalog: Set[CatalogItem], bundles: Set[Bundle]) {
  val cad = CurrencyUnit.of("CAD")

  def total(order: Order): OrderTotal = {
    val orderTotal = order.items.foldLeft(Money.of(cad, 0d)) {
      (acc, item) => acc.plus(quantityPrice(item))
    }

    OrderTotal(order, Set.empty, orderTotal)
  }

  def applyBundle(order: Order, bundle: Bundle): OrderTotal = {

  }

  def quantityPrice(item: OrderItem) =
    Money.of(cad, item.catalogItem.price).multipliedBy(item.quantity)
}


