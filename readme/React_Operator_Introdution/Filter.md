### Filter

Observable에서 원하는 데이터만 걸러내는 역할을 한다.

즉, 필요 없는 데이터는 제거하고 오직 관심 있는 데이터만 filter 함수를 통과하게 된다.</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/filter.png?raw=true" width = "550" height = "250"  /> </br>



위 다이어그램을 보면 원 만 통과시키는 것을 볼 수 있다.</br>



**입력**

```kotlin
import io.reactivex.Observable

class FilterExample {
    fun marbleDiagram() {
        val objs = arrayOf("1 CIRCLE", "2 DIAMOND", "3 TRIANGLE", "4 DIAMOND", "5 CIRCLE", "6 HEXAGON")
        val source = Observable.fromArray(*objs)
            .filter { obj -> obj.endsWith("CIRCLE") }
        source.subscribe { data -> println(data) }
    }
}

fun main() {
    val demo = FilterExample()
    demo.marbleDiagram()
}
```

**출력**

```
1 CIRCLE
5 CIRCLE
```



filter 함수에는 boolean 값을 리턴 하는 함수형 인터페이스인 Predicate를 인자로 넣는다.

</br>



아래는 filter 함수를 이용한 짝수 필터링이다.</br>



**입력**

```kotlin
import io.reactivex.Observable

class FilterExample {
    fun evenNumbers() {
        val data = arrayOf(100, 34, 27, 99, 50)
        val source = Observable.fromArray(*data)
            .filter { number -> number % 2 == 0 }
        source.subscribe { data -> println(data) }
    }
}

fun main() {
    val demo = FilterExample()
    demo.evenNumbers()
}
```

**출력**

```
100
34
50
```



아래는 filter와 비슷한 함수들을 정리한 것이다.</br></br>



**first(default) 함수** - Observable의 첫 번째 값을 필터 함. 만약 값 없이 완료되면 기본 값을 리턴 한다.

**last (default) 함수** - Observable의 마지막 값을 필터 함. 만약 값 없이 완료되면 기본 값을 리턴 한다.

**take (N) 함수** - 최초 N개 값만 가져온다.

**takeLast (N) 함수** - 마지막 N개 값만 필터 한다.

**skip (N) 함수** - 최초 N개 값을 건너 뛴다.

**skipLast (N) 함수** - 마지막 N개 값을 건너뛴다.</br></br>



아래는 위의 함수들을 활용한 코드이다.</br>



**입력**

```kotlin
import io.reactivex.Observable
import io.reactivex.Single

class FilterExample {fun otherFilters() {
        val numbers = arrayOf(100, 200, 300, 400, 500)
        var single: Single<Int>
        var source: Observable<Int>

        // 1. first
        single = Observable.fromArray(*numbers).first(-1)
        single.subscribe { data -> println("first() value = $data") }

        // 2. last
        single = Observable.fromArray(*numbers).last(999)
        single.subscribe { data -> println("last() value = $data") }

        // 3. take(N)
        source = Observable.fromArray(*numbers).take(3)
        source.subscribe { data -> println("takeLast(3) value = $data") }
        
        // 4. takeLast(N)
        source = Observable.fromArray(*numbers).takeLast(3)
        source.subscribe { data -> println("takeLast(3) value = $data") }

        // 5. skip(N)
        source = Observable.fromArray(*numbers).skip(2)
        source.subscribe { data -> println("skip(2) value = $data") }
        
        // 6. skipLast(N)
        source = Observable.fromArray(*numbers).skipLast(2)
        source.subscribe { data -> println("skipLast(2) value = $data") }
    }
}

fun main() {
    val demo = FilterExample()
    demo.otherFilters()
}
```

**출력**

```
first() value = 100
last() value = 500
takeLast(3) value = 100
takeLast(3) value = 200
takeLast(3) value = 300
takeLast(3) value = 300
takeLast(3) value = 400
takeLast(3) value = 500
skip(2) value = 300
skip(2) value = 400
skip(2) value = 500
skipLast(2) value = 100
skipLast(2) value = 200
skipLast(2) value = 300
```



