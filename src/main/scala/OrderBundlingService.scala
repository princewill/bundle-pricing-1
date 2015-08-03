package seglo

import scala.annotation.tailrec

/**
 * The OrderBundlingService calculates all permutations of orders and identifies
 * the one with the lowest price.
 *
 * TODO:
 * - Make a class.  Trait `calculate` so other OrderServices can be used in the API
 */
object OrderBundlingService {
  /**
   * Find the lowest order total of all order bundle permutations
   * @param order The original order with no bundles.
   * @param allBundles A set of bundles to apply to the order.
   * @return The bundled order with the lowest total price.
   */
  def calculate(order: Order, allBundles: Set[Bundle]): Order =
    orderPermutations(order, allBundles).minBy(_.total)

  /**
   * Generate all order bundle permutations.
   * @return All possible bundle permutations
   */
  def orderPermutations(originalOrder: Order, allBundles: Set[Bundle]): Set[Order] = {

    /**
     * Recursively apply bundles to each order permutation.
     */
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
