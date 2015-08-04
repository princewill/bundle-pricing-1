case class CatalogItemDto(name: String)
case class OrderDto(entries: Seq[CatalogItemDto])

case class InvalidOrderException(message: String) extends Exception
