package seglo

import org.joda.money.{CurrencyUnit, Money}

trait OrderEntry

case class CatalogItem(name: String, price: Double) extends OrderEntry
case class Bundle(name: String, items: Seq[CatalogItem], price: Double) extends OrderEntry

case class Order(entries: Seq[OrderEntry]) {
  val cad = CurrencyUnit.of("CAD")

  lazy val price =
    entries.foldLeft(Money.of(cad, 0d)) { (total: Money, orderEntry) =>
      orderEntry match {
        case CatalogItem(name, price) => addToTotal(total, price)
        case Bundle(name, items, price) => addToTotal(total, price)
      }
    }.getAmount

  private def addToTotal(total: Money, price: Double) =
    total.plus(Money.of(cad, price))
}

object BundleProcessor {
  def calculate(order: Order, allBundles: Set[Bundle]): Order = {
    val orders = orderPermutations(order, allBundles)
    orders.minBy(_.price)
  }

  def orderPermutations(order: Order, bundles: Set[Bundle]): Set[Order] = {
    val bundledOrders = bundles.foldLeft(Set.empty[Order]) { (orders, bundle) =>
      applyBundleToOrder(order, bundle) match {
        case Some(x) => orders + x
        case None => orders
      }
    }
    bundledOrders.flatMap(bundledOrder => orderPermutations(bundledOrder, bundles)) ++ bundledOrders
  }

  def applyBundleToOrder(order: Order, bundle: Bundle): Option[Order] = {
    val bundledEntries = order.entries.diff(bundle.items)
    // if all bundle items have been removed from the order then the bundle
    // was successfully applied.  return the updated order with the bundle
    if (bundledEntries.length == order.entries.length - bundle.items.length)
      Some(Order(bundledEntries :+ bundle))
    else
      None
  }
}
