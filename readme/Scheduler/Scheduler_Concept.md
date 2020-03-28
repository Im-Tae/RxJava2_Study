### 스케줄러 개념

</br>

**입력**

```kotlin
import common.Log
import io.reactivex.Observable

class HelloRxJava2V2 {
    fun emit() {
        Observable.just("Hello", "RxJava2!!")
            .subscribe { data -> Log.it(data) }
    }
}

fun main() {
    val demo = HelloRxJava2V2()
    demo.emit()
}
```

**출력**

```
main | value = Hello
main | value = RxJava2!!
```



위에는 just를 사용한 간단한 예제이다.

지금까지 배운 예제의 공통점은 대부분의 동작이 메인 스레드에서 이루어진다는 것이다.

요구 사항에 맞게 비동기로 동작할 수 있도록 바꿔야 될 때가 있다. 이때 스케줄러를 사용한다.</br>



스케줄러는 스레드를 지정할 수 있게 해준다.</br>



스케줄러의 개념에 대해 알아보기 위해 마블 다이어그램을 살펴보겠다.

<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/flipMarbleDiagram.png?raw=true" width = "550" height = "290"  /> </br>



아래는 위 마블 다이어그램의 코드이다.

</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import common.Shape
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class FlipExample {
    fun marbleDiagram() {
        val objs = arrayOf("1-S", "2-T", "3-P")

        val source = Observable.fromArray(*objs)
            .doOnNext { data -> Log.it("Original data = $data") }
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .map(Shape()::flip)

        source.subscribe { data -> Log.it(data) }
        CommonUtils.sleep(500)
    }
}

fun main() {
    val demo = FlipExample()
    demo.marbleDiagram()
}
```

**출력**

```
RxNewThreadScheduler-1 | value = Original data = 1-S
RxNewThreadScheduler-1 | value = Original data = 2-T
RxNewThreadScheduler-1 | value = Original data = 3-P
RxNewThreadScheduler-2 | value = (flipped)1-S
RxNewThreadScheduler-2 | value = (flipped)2-T
RxNewThreadScheduler-2 | value = (flipped)3-P
```



objs에는 마블 다이어그램의 도형이 담겨 있다.

map 함수에 flip 함수를 호출하여 도형을 뒤집는다.

flip함수는 아래와 같다. </br>



**입력**

```kotlin
class Shape {

    
    val FLIPPED = "(flipped)";

    fun flip(item: String): String {
        if (item.startsWith(FLIPPED)) return item.replace(FLIPPED, "")

        return FLIPPED + item
    }
}
```

</br>



doOnNext 함수를 통해 Observable에서 onNext 이벤트가 발생하면 원래의 데이터 값을 확인할 수 있다.

그리고 subscribeOn 함수를 통해 구독자가 Observable에서 구독할 때 실행되는 스레드를 지정한다.

마지막으로 observeOn을 통해 Observable에서 생성한 데이터 흐름이 여기저기 함수를 거치며 처리될 때 동작이 어느 스레드에서 일어나는지 지정할 수 있다.</br>



subscribeOn과 observeOn 함수에 인자로 Schedulers.newThread()를 넘겼는데, 이는 새로운 스레드를 생성한다는 의미이다.

</br>



출력 결과를 보면 값을 처리하는 스레드가 다른 것을 볼 수 있다.

이처럼 스케줄러를 활용하는 비동기 프로그래밍의 핵심은 데이터 흐름이 발생하는 스레드와 처리된 결과를 구독자에게 전달하는 슬레드를 분리할 수 있다는 것이다.</br>



아래는 위에 코드에서 observeOn 호출 부분을 제거한 코드이다.

</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import common.Shape
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class FlipExample {
    fun observeOnRemoved() {
        val objs = arrayOf("1-S", "2-T", "3-P")

        val source = Observable.fromArray(*objs)
            .doOnNext { data -> Log.it("Original data = $data") }
            .subscribeOn(Schedulers.newThread())
            //.observeOn(Schedulers.newThread())
            .map(Shape()::flip)

        source.subscribe { data -> Log.it(data) }
        CommonUtils.sleep(500)
    }
}

fun main() {
    val demo = FlipExample()
    demo.observeOnRemoved()
}
```

**출력**

```
RxNewThreadScheduler-1 | value = Original data = 1-S
RxNewThreadScheduler-1 | value = (flipped)1-S
RxNewThreadScheduler-1 | value = Original data = 2-T
RxNewThreadScheduler-1 | value = (flipped)2-T
RxNewThreadScheduler-1 | value = Original data = 3-P
RxNewThreadScheduler-1 | value = (flipped)3-P
```



출력 결과를 보면 하나의 스레드에서 모든 처리를 하는 것을 볼 수 있다.

즉, observeOn 함수를 지정하지 않으면 subscribeOn 함수로 지정한 스레드에서 모든 로직을 실행한다.

</br></br>



지금까지의 설명을 간단하게 정리하면 아래와 같다.



1. 스케줄러는 RxJava 코드를 어느 스레드에서 실행할지 지정할 수 있다.
2. subscribeOn 함수와 observeOn 함수를 모두 지정하면 Observable에서 데이터 흐름이 발생하는 스레드와 처리된 결과를 구독자에게 발행하는 스레드를 분리할 수 있다.
3. subscribeOn 함수만 호출하면 Observable의 동일한 스레드에서 실행된다.
4. 스케줄러를 별도로 지정하지 않으면 메인 스레드에서 실행한다.