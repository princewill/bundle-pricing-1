package seglo

import scala.concurrent.{ExecutionContext, Future, Promise}
import ExecutionContext.Implicits.global

/**
 * The OrderApi is used to checkout items.
 *
 * Assumptions:
 * 1) Each item in the catalog has a price defined.
 * 2) Bundles only contain items that exist in the catalog.
 *
 * @param catalog The full set of catalog items that can be checked out.
 * @param orderService The service implementation to use to calculate order totals.
 */
class OrderApi(catalog: Set[CatalogItem], orderService: OrderService) {
  /**
   * Checkout the order and receive the total amount.
   * @param orderDto The Order Data Transfer Object (DTO)
   * @return A future that successfully returns the order total or several different
   *         failure events as InvalidOrderException's.
   *
   *         Failure events include:
   *         1) An order contains items that do not have corresponding catalog items.
   *         2) An order with no items.
   */
  def checkout(orderDto: OrderDto): Future[BigDecimal] = {
    val p = Promise[BigDecimal]()
    Future {
      if (validate(orderDto, p)) {
        val processedOrder = orderService.calculate(orderDtoToOrder(orderDto))
        p.success(processedOrder.total)
      }
    }
    p.future
  }

  /**
   * Convert Order Data Transfer Object (DTO) to Order Domain Object.
   */
  private def orderDtoToOrder(orderDto: OrderDto): Order =
    Order(orderDto.entries.map { catalogItemDto =>
      catalog.find(_.name == catalogItemDto.name)
        // an exception should never happen when the order is validated first
        .getOrElse(throw new Exception(s"Couldn't find corresponding item in catalog: ${catalogItemDto.name}"))
    })

  /**
   * Order DTO validation.  Applies failure events to promise when validation fails.
   */
  private def validate(orderDto: OrderDto, checkoutPromise: Promise[BigDecimal]): Boolean = {
    // Find CatalogItemDto names that don't exist in our Catalog Set.
    val invalidItemDtos = orderDto.entries.map(_.name).diff(catalog.map(_.name).toSeq)

    if (invalidItemDtos.size > 0) {
      checkoutPromise.failure(InvalidOrderException(
        s"Your order contains catalog items do not exist: ${invalidItemDtos.mkString(", ")}."))
      false
    }
    else if (orderDto.entries.length == 0) {
      checkoutPromise.failure(InvalidOrderException("Your order contains no items."))
      false
    }
    else true
  }
}
