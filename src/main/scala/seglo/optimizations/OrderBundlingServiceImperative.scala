package seglo.optimizations

import seglo._

/**
* Optimization of OrderBundlingService
* - Only returns most bundled orders
* @param bundles
*/
class OrderBundlingServiceImperative(bundles: Set[Bundle])
  extends OrderBundlingService(bundles) {

  override def orderPermutations(order: Order, bundles: Set[Bundle]): Set[Order] = {
    var leafOrders = scala.collection.mutable.Set[Order]()
    var orders = scala.collection.mutable.Set[Order](order)
    var nextOrders: scala.collection.mutable.Set[Order] = null
    do {
      nextOrders = scala.collection.mutable.Set[Order]()
      orders.foreach { order =>
        var bundledOrders = scala.collection.mutable.Set[Order]()
        bundles.foreach { bundle =>
          val bundledOrder = applyBundleToOrder(order, bundle)
          if (bundledOrder.nonEmpty)
            bundledOrders += bundledOrder.get
        }
        if (bundledOrders.size == 0)
          leafOrders += order
        nextOrders ++= bundledOrders
      }
      if (nextOrders.size > 0)
        orders = nextOrders
    } while(nextOrders.size > 0)
    leafOrders.toSet
  }

  override def applyBundleToOrder(order: Order, bundle: Bundle): Option[Order] = {
    var bundleItems = scala.collection.mutable.ArrayBuffer(bundle.items: _*)
    var orderEntries = scala.collection.mutable.ArrayBuffer(order.entries: _*)

    var orderDec = order.entries // iterate over order entries by popping one each loop
    while(bundleItems.length > 0 && orderDec.length > 0) {
      val orderEntry = orderDec.head
      if (bundleItems.head == orderEntry) {
        orderEntries -= orderEntry
        bundleItems = bundleItems.tail
      }
      orderDec = orderDec.tail
    }
    if (bundleItems.length > 0)
      None
    else
      Some(Order(orderEntries += bundle))
  }
}
