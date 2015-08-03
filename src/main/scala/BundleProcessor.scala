package seglo

import org.joda.money.{CurrencyUnit, Money}

import scala.annotation.tailrec

trait OrderEntry

case class CatalogItem(name: String, price: Double) extends OrderEntry
case class Bundle(name: String, items: Seq[CatalogItem], price: Double) extends OrderEntry

case class Order(entries: Seq[OrderEntry]) {
  val cad = CurrencyUnit.of("CAD")

  lazy val price =
    entries.foldLeft(Money.of(cad, 0d)) { (total: Money, orderEntry) =>
      orderEntry match {
        case CatalogItem(name, price) => total.plus(Money.of(cad, price))
        case Bundle(name, items, price) => total.plus(Money.of(cad, price))
      }
    }.getAmount
}

object BundleProcessor {
  def calculate(order: Order, allBundles: Set[Bundle]): Order = {
    val orders = orderPermutations(order, allBundles)
    orders.minBy(_.price)
  }


  def orderPermutations(originalOrder: Order, allBundles: Set[Bundle]): Set[Order] = {

    @tailrec
    def go(orders: Set[Order], bundles: Set[Bundle], orderAcc: Set[Order]): Set[Order] = {
      val bundledOrders = bundles.foldLeft(Set.empty[Order]) { (orderAcc, bundle) =>
        orders.foldLeft(orderAcc) { (orderAcc, order) =>
          applyBundleToOrder(order, bundle) match {
            case Some(bundledOrder) => orderAcc + bundledOrder
            case None => orderAcc
          }
        }
      }
      if (bundledOrders.size > 0)
        go(bundledOrders, bundles, bundledOrders ++ orderAcc)
      else
        orderAcc
    }

    go(Set(originalOrder), allBundles, Set.empty[Order])
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
