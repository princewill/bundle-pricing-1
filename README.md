# bundle-pricing

[![Build Status](https://travis-ci.org/seglo/bundle-pricing.svg?branch=master)](https://travis-ci.org/seglo/bundle-pricing)

> Example API to find the lowest possible order total using bundle pricing

## Run tests

```bash
sbt test
```

## Example

This example has a catalog of 3 fruit and a bundle deal on 1 Apple and 1 Banana.  Given an order of 1 Apple, 1 Banana, and 1 Grapefruit the OrderApi applies one bundle for a savings of $0.50.

```scala
val appleDto = CatalogItemDto("Apple")
val bananaDto = CatalogItemDto("Banana")
val grapefruitDto = CatalogItemDto("Grapefruit")

val orderDto = OrderDto(Seq(appleDto, bananaDto, grapefruitDto))

val apple = CatalogItem("Apple", BigDecimal(1.50))
val banana = CatalogItem("Banana", BigDecimal(1.00))
val grapefruit = CatalogItem("Grapefruit", BigDecimal(3.00))

val catalog = Set(apple, banana, grapefruit)

val appleBananaBundle = Bundle("Apple & Banana Bundle",
    Seq(apple, banana), BigDecimal(2.00))

val orderApi = new OrderApi(catalog, Set(appleBananaBundle))

val checkoutFuture = orderApi.checkout(orderDto)

Await.result(checkoutFuture, Duration.Inf) == BigDecimal(5.00)
```

## Assumptions

* Order entries do not contain quantities, instead repeated order entries can be included in an order.  I found that simplifying the model in this way reduced the complexity of my implementation.
* 3rd party Libraries are allowed.  I used [joda-money](http://www.joda.org/joda-money/) to aggregate prices using the CAD currency unit.

## Possible Optimizations

* Only use subset of bundles when recursing into order permutations.  Only the bundles that were successfully applied to the order.
* Use imperative programming techniques to mutate state instead of FP & recursive implementation.

## Bundle Pricing Resources
* https://en.wikipedia.org/wiki/Product_bundling
* http://www.businessdictionary.com/definition/product-bundle-pricing.html
