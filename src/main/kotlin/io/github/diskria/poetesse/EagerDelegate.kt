package io.github.diskria.poetesse

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class EagerDelegate<T>(private val builder: (name: String) -> T) {

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> {
        val value = builder(property.name)
        return ReadOnlyProperty { _, _ -> value }
    }
}
