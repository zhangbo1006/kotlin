val nullableNumberProperty: Number? = null

val stringProperty: String = ""
val nullableStringProperty: String? = null

val intProperty: Int = ""
val nullableIntProperty: Int? = null

val implicitNullableNothingProperty = null
val nullableNothingProperty: Nothing? = null

val anonymousTypeProperty = object {}

val nullableAnonymousTypeProperty = if (true) object {} else null

val nullableOut: Out<Int>? = null

val <T> T.propT get() = 10

val <T> T?.propNullableT get() = 10

val Any.propAny get() = 10

val Any?.propNullableAny get() = 10