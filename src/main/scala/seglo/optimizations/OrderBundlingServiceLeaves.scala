//package seglo.optimizations
//
//import seglo._
//
//import scala.annotation.tailrec
//
///**
// * Optimization of OrderBundlingService
// * - Only returns most bundled orders, the "Leaves".
// */
//class OrderBundlingServiceLeaves(bundles: Set[Bundle])
//  extends OrderBundlingService(bundles) {
//
//  override def calculate(order: Order, bundles: Set[Bundle]) : Order = {
//    val set = orderPermutations(order, bundles)
//
//    if (set.size == 0)
//      order
//    else
//      set.minBy(_.total)
//  }
//
//  override def orderPermutations(originalOrder: Order, allBundles: Set[Bundle]): Set[Order] = {
//
//    /**
//     * Recursively apply bundles to each order permutation until
//     * bundles can no longer be applied.
//     */
//    @tailrec
//    def go(orders: Set[Order], bundles: Set[Bundle], orderAcc: Set[Order]): Set[Order] = {
//      val newOrders = orders.foldLeft((Set.empty[Order], orderAcc)) {
//        case ((bundledOrders, orderAcc), order) =>
//          val newBundledOrders = bundles.foldLeft(Set.empty[Order]) { (bundledOrders, bundle) =>
//            applyBundleToOrder(order, bundle) match {
//              case Some(bundledOrder) => bundledOrders + bundledOrder
//              case None => bundledOrders
//            }
//          }
//
//          if (newBundledOrders.size == 0)
//            (bundledOrders, orderAcc + order)
//          else
//            (bundledOrders ++ newBundledOrders, orderAcc)
//      }
//      newOrders match {
//        case (bundledOrders, orderAcc) =>
//          if (bundledOrders.size > 0)
//            go(bundledOrders, bundles, orderAcc)
//          else
//            orderAcc
//      }
//
//    }
//
//    go(Set(originalOrder), allBundles, Set(originalOrder))
//  }
//}
