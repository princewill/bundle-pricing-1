import scala.annotation.tailrec

trait OrderService {
  def calculate(order: Order): Order
}

/**
 * The OrderBundlingService calculates all permutations of orders and identifies
 * the one with the lowest price.
 */
class OrderBundlingService(bundles: Set[Bundle]) extends OrderService {

  /**
   * Pass on to calculate with bundle support.
   */
  def calculate(order: Order) = calculate(order, bundles)

  /**
   * Find the lowest order total of all order bundle permutations
   * @param order The original order with no bundles.
   * @param bundles A set of bundles to apply to the order.
   * @return The bundled order with the lowest total price.
   */
  def calculate(order: Order, bundles: Set[Bundle]) : Order =
    orderPermutations(order, bundles).minBy(_.total)

  /**
   * Generate all order bundle permutations.
   * @return All possible bundle permutations
   */
  def orderPermutations(originalOrder: Order, allBundles: Set[Bundle]): Set[Order] = {

    /**
     * Recursively apply bundles to each order permutation until
     * bundles can no longer be applied.
     */
    @tailrec
    def go(orders: Set[Order], bundles: Set[Bundle], orderAcc: Set[Order]): Set[Order] = {
      val bundledOrders = bundles.foldLeft(Set.empty[Order]) { (bundledOrders, bundle) =>
        orders.foldLeft(bundledOrders) { (bundledOrders, order) =>
          applyBundleToOrder(order, bundle) match {
            case Some(bundledOrder) => bundledOrders + bundledOrder
            case None => bundledOrders
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

  /**
   * Apply a bundle to an order.  When it can't be applied return None.
   * @param order The order.
   * @param bundle The bundle to apply to the order.
   * @return A successfully applied bundle or None.
   */
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
