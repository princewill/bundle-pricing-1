package seglo

import org.joda.money.{CurrencyUnit, Money}

case class CatalogItem(name: String, price: Double)

case class Bundle(name: String, items: Set[OrderItem], price: Double)

trait OrderEntry

case class OrderItem(item: CatalogItem, quantity: Int) extends OrderEntry

case class BundleItem(bundle: Bundle, quantity: Int) extends OrderEntry

case class Order(entries: Set[OrderEntry]) {
  val cad = CurrencyUnit.of("CAD")

  lazy val price =
    entries.foldLeft(Money.of(cad, 0d)) { (total: Money, orderEntry) =>
      orderEntry match {
        case OrderItem(CatalogItem(name, price), quantity) => addToTotal(total, price, quantity)
        case BundleItem(Bundle(name, items, price), quantity) => addToTotal(total, price, quantity)
      }
    }.getAmount

  private def addToTotal(total: Money, price: Double, quantity: Int) =
    total.plus(Money.of(cad, price).multipliedBy(quantity))
}

object BundleProcessor {
  def calculate(order: Order, allBundles: Set[Bundle]): Order = {
    val orders = orderPermutations(order, allBundles)
    orders.minBy(_.price)
  }


  def orderPermutations(originalOrder: Order, bundles: Set[Bundle]): Set[Order] = {

    def go(order: Order, bundles: Set[Bundle], possibleOrders: Set[Order]): Set[Order] = {
      val subBundles = possibleBundles(order, bundles)
      if (subBundles.size == 0) possibleOrders
      else {
        val bundledOrders = subBundles.map((bundle) => applyBundleToOrder(order, bundle))
        val nestedOrders = bundledOrders.flatMap(order2 => go(order2, subBundles, bundledOrders))
        nestedOrders ++ possibleOrders
      }
    }

    go(originalOrder, bundles, Set(originalOrder))
  }

  /*
   * Return bundles that exist in the order
   */
  def possibleBundles(order: Order, bundles: Set[Bundle]): Set[Bundle] =
    bundles.filter(orderContainsBundle(order, _))

  /*
   * Return true when an order contains a bundle
   */
  def orderContainsBundle(order: Order, bundle: Bundle): Boolean = {
    order.entries.foldLeft(Set.empty[OrderItem]) { (bundleBuilder, orderItem) =>
      orderItem match {
        case OrderItem(catalogItem, quantity) => {
          val bundleItem = bundle.items.find(_.item == catalogItem)
          bundleItem match {
            case Some(OrderItem(_, bundleQuantity)) if quantity / bundleQuantity > 0 =>
              bundleBuilder ++ bundleItem
            case _ => bundleBuilder
          }
        }
        // skip other OrderEntry's (BundleItem's)
        case _ => bundleBuilder
      }
    } == bundle.items
  }

  def applyBundleToOrder(order: Order, bundle: Bundle): Order = {
    val orderEntries = order.entries.foldLeft(Set.empty[OrderEntry]) { (newOrderEntries, orderEntry) =>
      orderEntry match {
        case OrderItem(catalogItem, quantity) => {
          val bundleItem = bundle.items.find(_.item == catalogItem)
          bundleItem match {
            case Some(OrderItem(item, bundleQuantity)) if quantity / bundleQuantity > 0 =>
              newOrderEntries + OrderItem(item, quantity - bundleQuantity)
            case None => newOrderEntries + orderEntry
          }
        }
        // re-add all other OrderEntry's (BundleItem) already found in order
        case x: OrderEntry => newOrderEntries + x
      }
    }
    val matchedBundled = order.entries.find { orderEntry =>
      orderEntry match {
        case BundleItem(b, _) => b == bundle
        case _ => false
      }
    }

    matchedBundled match {
      case Some(BundleItem(b, quantity)) => Order(orderEntries + BundleItem(bundle, quantity + 1))
      case _ => Order(orderEntries + BundleItem(bundle, 1))
    }
  }
}
