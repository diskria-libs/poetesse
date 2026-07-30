package io.github.diskria.poetesse

import kotlin.reflect.KProperty

class LazyDelegate<T>(private val factory: (name: String) -> T) {

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Lazy<T> =
        lazy { factory(property.name) }
}
