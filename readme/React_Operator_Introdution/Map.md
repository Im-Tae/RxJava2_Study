### Map

map 함수는 입력값을 어떤 함수에 넣어서 원하는 값으로 변환하는 함수이다.</br>





<img src="" width = "550" height = "300"  /> </br>



map은 원을 입력받아서 다이아몬드로 반환하는 것을 볼 수 있다.

아래는 마블 다이어그램을 코드로 표현한 것이다.

</br>



**입력**

```kotlin
import io.reactivex.Observable

class MapExample {
    fun marbleDiagram() {
        val balls = arrayOf("1", "2", "3", "5")
        val source = Observable.fromArray(*balls)
            .map { ball -> "$ball◇" }
        source.subscribe { data -> println(data) }
    }
}

fun main() {
    val demo = MapExample()
    demo.marbleDiagram()
}
```

**출력**

```
1◇
2◇
3◇
5◇
```

</br>



아래는 Function 인터페이스를 적용한 map 함수이다.

</br>



**입력**

```kotlin
import io.reactivex.Observable
import io.reactivex.functions.Function

class MapExample {
    fun mapFunction() {
        val getDiamond = Function<String, String> { ball -> "$ball◇" }

        val balls = arrayOf("1", "2", "3", "5")
        val source = Observable.fromArray(*balls)
            .map(getDiamond)
        source.subscribe { data -> println(data) }
    }
}

fun main() {
    val demo = MapExample()
    demo.mapFunction()
}
```

**출력**

```
1◇
2◇
3◇
5◇
```

</br>