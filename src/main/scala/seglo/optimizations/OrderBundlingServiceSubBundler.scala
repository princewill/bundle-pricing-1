package seglo.optimizations

import seglo._

/**
 * Optimization of OrderBundlingService
 * - Passes in a subset of bundles for each iteration
 * - No longer tail recursive because the recursive call is inside a flatMap
 * @param bundles
 */
class OrderBundlingServiceSubBundler(bundles: Set[Bundle])
  extends OrderBundlingService(bundles) {

  override def calculate(order: Order, bundles: Set[Bundle]) : Order = {
    val set = orderPermutations(order, bundles)

    if (set.size == 0)
      order
    else
      set.minBy(_.total)
  }

  override def orderPermutations(order: Order, bundles: Set[Bundle]): Set[Order] = {
    val bundledOrders = bundles.foldLeft((Set.empty[Order], Set.empty[Bundle])) {
      case ((ordersAcc, bundlesAcc), bundle) =>
      applyBundleToOrder(order, bundle) match {
        case Some(x) => (ordersAcc + x, bundlesAcc + bundle)
        case None => (ordersAcc, bundlesAcc)
      }
    }
    bundledOrders match {
      case (orders, subBundles) => orders.flatMap(order => orderPermutations(order, subBundles)) ++ orders
    }
  }
}
