package io.github.diskria.poetesse

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class EagerDelegate<T>(
    private val builder: (name: String) -> T
) : PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> {

    override operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> {
        val value = builder(property.name)
        return ReadOnlyProperty { _, _ -> value }
    }
}
