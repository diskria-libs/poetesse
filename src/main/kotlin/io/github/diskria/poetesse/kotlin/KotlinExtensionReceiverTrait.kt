package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

sealed interface KotlinExtensionReceiverTrait : PoetesseKotlinScope

fun KotlinExtensionReceiverTrait.extensionReceiver(type: XTypeName) {
    container.append(type.interopToKotlin())
}

fun KotlinExtensionReceiverTrait.extensionReceiver(type: KClass<*>, nullable: Boolean = false) =
    extensionReceiver(xType(type, nullable = nullable))

inline fun <reified T> KotlinExtensionReceiverTrait.extensionReceiver(nullable: Boolean = true) =
    extensionReceiver(T::class, nullable)

inline fun <reified T : Any> KotlinExtensionReceiverTrait.extensionReceiver() =
    extensionReceiver<T>(nullable = false)

internal class KotlinExtensionReceiverContainer(val append: (extensionReceiver: KPTypeName) -> Unit)

private val KotlinExtensionReceiverTrait.container: KotlinExtensionReceiverContainer
    get() = when (this) {
        is KotlinPropertyScope -> extensionReceiverContainer
        is KotlinFunctionScope -> extensionReceiverContainer
    }
