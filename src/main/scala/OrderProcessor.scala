//package seglo
//
//import org.joda.money.{CurrencyUnit, Money}
//
//case class Order(items: Set[OrderItem])
//case class OrderTotal(order: Order, bundles: Set[Bundle], price: Money)
//case class OrderItem(catalogItem: CatalogItem, quantity: Int)
//case class CatalogItem(itemName: String, price: Double)
//case class Bundle(items: Set[OrderItem], price: Double)
//
//class OrderProcessor(catalog: Set[CatalogItem], bundles: Set[Bundle]) {
//  val cad = CurrencyUnit.of("CAD")
//
//  def total(order: Order): OrderTotal = {
//    val orderTotal = order.items.foldLeft(Money.of(cad, 0d)) {
//      (acc, item) => acc.plus(quantityPrice(item))
//    }
//
//    OrderTotal(order, Set.empty, orderTotal)
//  }
//
//  def orderContainsBundle(order: Order, bundle: Bundle): Boolean = {
//    val builtBundle = order.items.foldLeft(Set[OrderItem]()) { (bundleBuilder, orderItem) =>
//      val bundleItem = bundle.items.find(bundleItem => bundleItem.catalogItem == orderItem.catalogItem)
//      bundleItem match {
//        case Some(x) => {
//          val bundleQuantity = orderItem.quantity / x.quantity
//          if (bundleQuantity >= 1) bundleBuilder ++ bundleItem
//          else bundleBuilder
//        }
//        case None => bundleBuilder
//      }
//    }
//    bundle.items == builtBundle
//  }
//
//  def quantityPrice(item: OrderItem) =
//    Money.of(cad, item.catalogItem.price).multipliedBy(item.quantity)
//}
//
//
