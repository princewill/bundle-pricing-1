package seglo

import scala.annotation.tailrec

object OrderBundlingService {
  def calculate(order: Order, allBundles: Set[Bundle]): Order = {
    val orders = orderPermutations(order, allBundles)
    orders.minBy(_.total)
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

    go(Set(originalOrder), allBundles, Set(originalOrder))
  }

  def applyBundleToOrder(order: Order, bundle: Bundle): Option[Order] = {
    val bundledEntries = order.entries.diff(bundle.items)
    // If all bundle items have been removed from the order by using diff then the
    // bundle was successfully applied.  Return the updated order with the bundle.
    if (bundledEntries.length == order.entries.length - bundle.items.length)
      Some(Order(bundledEntries :+ bundle))
    else
      None
  }
}
