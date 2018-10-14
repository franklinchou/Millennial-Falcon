# Note on services

A service should always have return type of `Vertex`, `Option[Vertex]`, `Seq`/`List` or other
`Collection` type wrapping `Vertex`, `Boolean` or `Unit`. Marshalling to `Model` or `Resource`
should never be done in the `/services` package. `Future` or other concurrent/promise type
monads are also best reserved for the controller.