### ConnectableObservable

Subject 클래스처럼 차가운 Observable을 뜨거운 Observable로 변환한다.

Observable을 여러 구독자에게 공유할 수 있으므로 원 데이터 하나를 여러 구독자에게 동시에 전달할 때 사용한다. 

특이한 점은 subscribe를 호출해도 아무 동작이 일어나지 않는다. 새로 추가된 connect는 호출한 시점부터 subscribe를 호출한 구독자에게 데이터를 발행하기 때문이다. </br>

ConnectableObservable 객체를 생성하려면 먼저 Observable에 publish를 호출해야 한다. publish는 여러 구독자에게 데이터를 발행하기 위해 connect를 호출하기 전까지 데이터 발행을 유예하는 역할을 한다.

</br>

<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/ConnectableObservable.png?raw=true" width = "550" height = "300"  /> </br>



위에 마블 다이어그램과 같이 connect 함수를 호출해야 그때까지 구독했던 구독자 모두에게 데이터를 발행한다.</br>



**입력**

```kotlin
import io.reactivex.Observable

class ConnectableObservableExample {
    fun emit() {
        val balls = Observable.just(1, 3, 5)

        val source = balls.publish()
        source.subscribe { data -> println("Subscriber #1 => $data") }
        source.subscribe { data -> println("Subscriber #2 => $data") }
        source.connect()

        source.subscribe { data -> println("Subscriber #3 => $data") }
    }
}

fun main() {
    val demo = ConnectableObservableExample()
    demo.emit()
}
```

**출력**

```
Subscriber #1 => 1
Subscriber #2 => 1
Subscriber #1 => 3
Subscriber #2 => 3
Subscriber #1 => 5
Subscriber #2 => 5
```

첫 번째 구독자와 두 번째 구독자를 등록한 후 connect를 호출하면 데이터가 배출되는 것을 볼 수 있다.

또한 배출이 완료된 이후에 등록된 세 번째 구독자는 데이터를 하나도 전달 받지 못한다.



아래 코드는 동일한 코드에 interval 함수를 추가해 항상 출력 가능한 상태로 바꾼 것이다.</br>



**입력**

```kotlin
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ConnectableObservableExample {
    fun marbleDiagram() {
        val dt = arrayOf("1", "3", "5")

        val balls = Observable.interval(100, TimeUnit.MILLISECONDS)
            .map { data -> data.toInt() }
            .map { i -> dt[i] }
            .take(dt.size.toLong())

        val source = balls.publish()
        source.subscribe { data -> println("Subscriber #1 => $data") }
        source.subscribe { data -> println("Subscriber #2 => $data") }
        source.connect()

        Thread.sleep(300)

        source.subscribe { data -> println("Subscriber #3 => $data") }

        Thread.sleep(300)
    }
}

fun main() {
    val demo = ConnectableObservableExample()
    demo.marbleDiagram()
}
```

**출력**

```
Subscriber #1 => 1
Subscriber #2 => 1
Subscriber #1 => 3
Subscriber #2 => 3
Subscriber #1 => 5
Subscriber #2 => 5
Subscriber #3 => 5
```

발행하려는 데이터는 1, 3, 5 이고, interval은 인자 2 개를 받는데 각각 시간과 시간의 단위이다.

interval은 테스트 코드를 작성할 때 많이 활용한다.</br>



첫 번째 구독자와 두 번째 구독자가 추가되면 connect 함수를 호출해 데이터 발생을 시작한다.

그다음 세 번째 구독자가 나오기 전까지 sleep를 이용하여 300ms를 기다리고 세 번째 구독자를 추가한다.

connect 함수를 호출했으므로 이후에 구독하면 다음의 데이터를 바로 수신할 수 있다.

마지막의 sleep를 이용하여 balls 객체의 데이터를 모두 발행하고 세 구독자 모두 구독 해지 된다.