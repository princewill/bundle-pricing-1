package seglo

import scala.concurrent.{ExecutionContext, Future, Promise}
import ExecutionContext.Implicits.global

class OrderApi(catalog: Set[CatalogItem], bundles: Set[Bundle]) {
  // TODO:
  // inject OrderBundlingService

  def checkout(orderDto: OrderDto): Future[BigDecimal] = {
    val p = Promise[BigDecimal]()
    Future {
      if (validate(orderDto, p)) {
        val bundledOrder = OrderBundlingService.calculate(orderDtoToOrder(orderDto), bundles)
        p.success(bundledOrder.total)
      }
    }
    p.future
  }

  private def orderDtoToOrder(orderDto: OrderDto): Order =
    Order(orderDto.entries.map { catalogItemDto =>
      catalog.find(_.name == catalogItemDto.name)
        .getOrElse(throw new Exception(s"Couldn't find corresponding item in catalog: ${catalogItemDto.name}"))
    })

  private def validate(orderDto: OrderDto, checkoutPromise: Promise[BigDecimal]): Boolean = {
    // Find CatalogItemDto names that don't exist in our Catalog Set.
    val invalidItemDtos = orderDto.entries.map(_.name).diff(catalog.map(_.name).toSeq)

    if (invalidItemDtos.size > 0) {
      checkoutPromise.failure(InvalidOrderException(
        s"These catalog items do not exist: ${invalidItemDtos.mkString(", ")}."))
      false
    }
    else if (orderDto.entries.length == 0) {
      checkoutPromise.failure(InvalidOrderException("Your order contains no items."))
      false
    }
    else true
  }
}
