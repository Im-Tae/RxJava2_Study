### Single 클래스

Observable 클래스는 데이터를 무한하게 발행할 수 있지만, Single 클래스는 오직 1 개의 데이터만 발행한다.

API를 호출할 때 유용하게 사용할 수 있다.

</br></br>



#### just 함수



**입력**

```kotlin
import io.reactivex.Single

class SingleExample {
    fun just() {
        val source = Single.just("Hello Single")
        source.subscribe { x -> println(x) }
    }
}

fun main() {
    val demo = SingleExample()
    demo.just()
}
```

**출력**

```
Hello Single
```

</br></br>



#### Obserable에서 Single 클래스 사용

Single은 Observable의 특수한 형태이므로 Observable에서 변환할 수 있다.



**입력**

```kotlin
import common.Order
import io.reactivex.Observable
import io.reactivex.Single

class SingleExample {
    fun fromObservable() {

        // Observable에서 Single 객체로 변환
        val source: Observable<String> = Observable.just("Hello Single")
        Single.fromObservable<Any>(source)
            .subscribe { x -> println(x) }


        // single 함수 호출
        Observable.just("Hello Single")
            .single("default item")
            .subscribe { x -> println(x) }


        // first 함수 호출
        val colors = arrayOf("Red", "Blue", "Gold")
        Observable.fromArray(*colors)
            .first("default value")
            .subscribe { x -> println(x) }

        // empty Observable
        Observable.empty<String>()
            .single("default value")
            .subscribe { x -> println(x) }

        // take 함수
        Observable.just(Order("ORD-1"), Order("ORD-2"))
            .take(1)
            .single(Order("default order"))
            .subscribe { x -> println(x) }
    }
}

fun main() {
    val demo = SingleExample()
    demo.fromObservable()
}
```

**출력**

```
Hello Single
Hello Single
Red
default value
Order ID: ORD-1
```

</br></br>



#### Single 클래스 올바르게 사용

Single 객체를 생성할 때 데이터 하나만 발행하도록 보장했다. 

이유는 just 함수에 여러 개의 값을 넣은 아래 코드로 확인이 가능하다.



**입력**

```kotlin
import io.reactivex.Observable

class SingleExample {
    fun errorCase() {
        val source = Observable.just("Hello Single", "Error").single("default item")
        source.subscribe { x -> println(x) }
    }
}

fun main() {
    val demo = SingleExample()
    demo.errorCase()
}
```

**출력**

```
Error
```

</br></br>