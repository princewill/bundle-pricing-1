package seglo

import org.joda.money.{CurrencyUnit, Money}

trait OrderEntry {
  def name: String
  def price: BigDecimal
}

case class CatalogItem(name: String, price: BigDecimal) extends OrderEntry
case class Bundle(name: String, items: Seq[CatalogItem], price: BigDecimal) extends OrderEntry

case class Order(entries: Seq[OrderEntry]) {
  val cad = CurrencyUnit.of("CAD")

  lazy val total: BigDecimal =
    entries.foldLeft(Money.of(cad, BigDecimal(0).bigDecimal)) {
      (total: Money, orderEntry) => total.plus(Money.of(cad, orderEntry.price.bigDecimal))
    }.getAmount
}
