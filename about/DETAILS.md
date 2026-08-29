Example of usage:

```kotlin
poetesse {
    val argsType = xType<String>().array()
    val message = "Hello, World!"
    java.file("com.example", "HelloJava") {
        class_(fileName) {
            public()
            method("main") {
                static()
                parameter("args", argsType)
                body {
                    line { "${T<System>()}.out.println(${S(message)})" }
                }
            }
        }
    }.writeTo(System.out)

    kotlin.file("com.example", "HelloKotlin") {
        function("main") {
            parameter("args", argsType)
            body {
                line { "println(${S(message)})" }
            }
        }
    }.writeTo(System.out)
}
```

Output:

```java
package com.example;

import java.lang.String;
import java.lang.System;

public class HelloJava {
    static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

```kotlin
package com.example

import kotlin.Array
import kotlin.String

public fun main(args: Array<String>) {
    println("Hello, World!")
}
```
