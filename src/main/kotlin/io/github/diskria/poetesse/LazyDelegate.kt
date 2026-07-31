package io.github.diskria.poetesse

import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KProperty

class LazyDelegate<T>(
    private val factory: (name: String) -> T
) : PropertyDelegateProvider<Any?, Lazy<T>> {

    override operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Lazy<T> =
        lazy { factory(property.name) }
}
