### FlatMap

FlatMap 함수는 Map 함수를 발전시킨 함수이다. FlatMap 함수는 결과가 Observable로 나온다.

Map 함수가 일대일 함수라면 FlatMap 함수는 일대다 혹은 일대일 Observable 함수이다.</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/flatMap.png?raw=true" width = "550" height = "300"  /> </br>



빨간색 원을 넣으면 첫 번째 빨간색 다이아몬드, 두 번째 빨간색 다이아몬드가 나온다. 

초록색 원을 넣으면 같은 방식으로 첫 번째 초록색 다이아몬드, 두 번째 초록색 다이아몬드가 나온다.



위와 같이 FlatMap 함수는 결과값이 Observable이므로 여러 개의 데이터를 발행할 수 있다.

</br>



**입력**

```kotlin
import io.reactivex.Observable

class FlatMapExample {
    fun marbleDiagram() {
        val getDoubleDiamonds = Function<String, Observable<String>> { 
            ball -> Observable.just("$ball◇", "$ball◇") 
       }

        val balls = arrayOf("1", "3", "5")
        val source = Observable.fromArray(*balls)
            .flatMap(getDoubleDiamonds)
        source.subscribe { data -> println(data) }
    }
}

fun main() {
    val demo = FlatMapExample()
    demo.marbleDiagram()
}
```

**출력**

```
1◇
1◇
3◇
3◇
5◇
5◇
```

</br>



아래는 람다 표현식으로 작성한 코드이다.</br>



**입력**

```kotlin
import io.reactivex.Observable

class FlatMapExample {fun flatMapLamda() {
        val balls = arrayOf("1", "3", "5")
        val source = Observable.fromArray(*balls)
            .flatMap { ball -> Observable.just("$ball◇", "$ball◇") }
        source.subscribe { data -> println(data) }
    }
}

fun main() {
    val demo = FlatMapExample()
    demo.flatMapLamda()
}
```

**출력**

```
1◇
1◇
3◇
3◇
5◇
5◇
```

</br>



#### 구구단 예제

먼저 일반적인 구구단 예제이다.</br>



**입력**

```kotlin
import java.util.*

class Gugudan {
    fun plainKotlin() {
        val scanner = Scanner(System.`in`)
        print("Gugudan Input : ")
        val dan = scanner.nextInt()

        for (row in 1..9) println("$dan * $row = ${dan * row}")
        
        scanner.close()
    }
}

fun main() {
    val demo = Gugudan()
    demo.plainKotlin()
}
```

</br>



아래 코드는 for문을 Observable로 변환한 것이다.</br>



**입력**

```kotlin
import io.reactivex.Observable
import java.util.*

class Gugudan {
    fun reactiveV1() {
        val scanner = Scanner(System.`in`)
        print("Gugudan Input : ")
        val dan = scanner.nextInt()

        val source = Observable.range(1, 9)
        source.subscribe { row -> println("$dan * $row = ${dan * row}") }

        scanner.close()
    }
}

fun main() {
    val demo = Gugudan()
    demo.reactiveV1()
}
```

</br>



아래 코드는 flatMap 함수를 활용한 것이다.</br>



**입력**

```kotlin

import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.*

class Gugudan {
    val scanner = Scanner(System.`in`)
        print("Gugudan Input : ")
        val dan = scanner.nextInt()

        val gugudan = Function<Int, Observable<String>> { num ->
            Observable.range(1, 9)
                .map { row: Int -> "$num * $row = ${num * row}" }
        }

        val source = Observable.just(dan).flatMap(gugudan)
        source.subscribe { data -> println(data) }

        scanner.close()
    }
}

fun main() {
    val demo = Gugudan()
    demo.reactiveV2()
}
```

</br>



아래는 FlatMap 함수 안에 gugudan 함수를 넣은 코드이다.</br>



**입력**

```kotlin
import io.reactivex.Observable
import java.util.*

class Gugudan {
    fun reactiveV3() {
        val scanner = Scanner(System.`in`)
        print("Gugudan Input : ")
        val dan = scanner.nextInt()

        val source = Observable.just(dan)
            .flatMap { num -> Observable.range(1, 9) }
            .map { row -> println("$dan * $row = ${dan * row}") }

        source.subscribe()
        scanner.close()
    }
}

fun main() {
    val demo = Gugudan()
    demo.reactiveV3()
}
```

</br>



지금까지 호출한 FlatMap 함수의 원형은 아래와 같다.

```java
@SchedulerSupport(SchedulerSupport.NONE)
    public final <R> Observable<R> flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper) {
        return flatMap(mapper, false);
    }
```

</br>



SchedulerSupport.NONE은 간단히 현재 스레드에서 실행한다고 보면 된다.

ObservableSource은 Observable처럼 데이터를 발행할 수 있는 개체를 포괄해서 지칭한다.

Single 클래스에는 SingleSource라는 별도 인터페이스가 존재한다.

</br>



아래는 FlatMap 함수의 원형과 조금 다른 형태인 resultSelector를 사용하도록 변경한 FlatMap 코드이다.</br>



**입력**

```kotlin
import io.reactivex.Observable
import java.util.*

class Gugudan {
    fun usingResultSelector() {
        val scanner = Scanner(System.`in`)
        print("Gugudan Input : ")
        val dan = scanner.nextInt()

        val source = Observable.just(dan)
            .flatMap({ gugu -> Observable.range(1, 9) }) { gugu, i ->
                "$gugu * $i = ${gugu * i}"
            }

        source.subscribe{ data -> println(data) }
    }
}

fun main() {
    val demo = Gugudan()
    demo.usingResultSelector()
}
```

</br>