### Observable 클래스

Observable은 데이터 흐름에 맞게 알림을 보내 구독자가 데이터를 처리할 수 있도록 한다.

RxJava1 에서는 Observable과 Single 클래스를 사용하였지만 RxJava2에서는 Observable을 세분화 하여 Observable, Maybe, Flowable 클래스로 구분하여 사용한다. </br></br>



Observable은 [Observer 패턴](https://ko.wikipedia.org/wiki/옵서버_패턴)을 구현한다. Observer 패턴은 객체의 상태 변화를 관찰하는 관찰자 목록을 객체에 등록하고, 상태 변화가 있을 때마다 메서드를 호출하여 객체가 직접 목록의 각 Observer에게 변화를 알려준다.

</br>

> Observed라는 단어가 관찰을 통해서 얻은 결과를 의미한다면 Observable은 현재는 관찰되지 않았지만 이론을 통해서 앞으로 관찰할 가능성을 의미한다.

</br>

RxJava의 Observable은 세 가지의 알림을 구독자에게 전달한다.

**onNext** : Observable의 데이터의 발행을 알린다. 기존의 Observer 패턴과 같다.

**onComplete** : 모든 데이터의 발행을 완료했음을 알린다. onComplete 이벤트는 한 번만 발생하며, 발생 후에 onNext 이벤트가 발생하지 않는다.

**onError** : Observable에서 어떤 이유로 에러가 발생했음을 알린다. onError 이벤트가 발생하면 Observable의 실행을 종료된다.

</br>

Observable 클래스에는 많은 수의 함수가 존재한다.

Observable을 생성할 때는 직접 인스턴스를 만들지 않고 정적 팩토리 함수를 호출한다.

</br></br>



#### just 함수

데이터를 발행하는 가장 쉬운 방법은 기존의 자료구조를 사용하는 것이다. 

just 함수는 데이터를 차례로 발행하려고 Observable을 생성한다. 

[타입이 모두 같아야하고 한 개의 값부터 최대 열개의 값을 넣을 수 있다.]

</br>

<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/justMarbleDiagram.png?raw=true" width = "500" height = "250"  /> </br>

중앙의 원은 Observable에서 발행하는 데이터로 just 함수를 거치면 입력한 원을 그대로 발행한다. </br>



**입력**

```kotlin
import io.reactivex.Observable

class FirstExample {
    fun emit() {
        Observable.just(1, 2, 3, 4, 5, 6)
            .subscribe(System.out::println)
    }
}

fun main() {
    val demo = FirstExample()
    demo.emit()
}
```

**출력**

```
1
2
3
4
5
6
```

</br></br>



#### subscribe 함수와 Disposable 객체

subscrible 함수를 사용하여 실행되는 시점을 조절할 수 있다.

Observable은 just 등의 팩토리 함수로 데이터 흐름을 정의한 후 subscribe 함수를 호출해야 실제로 데이터를 발행한다. </br>



subscribe 함수는 Disposable 인터페이스의 객체를 리턴한다. </br>



dispose는 Observable에게 더 이상 데이터를 발행하지 않도록 구독을 해지하는 함수이다.

Observable이 onComplete 알림을 보냈을 때 자동으로 dispose를 호출해 구독자의 관계를 끊는다. ( 즉, onComplete 이벤트가 정상적으로 발생하면 별도로 dispose를 호출할 필요가 없다. )</br>



***입력***

```kotlin
import io.reactivex.Observable

class ObservableNotifications {
    fun emit() {
        val source = Observable.just("RED", "GREEN", "YELLOW")

        val d = source.subscribe(
            { v -> println("onNext() : value : $v") },
            { err -> println("onError() : err : ${err.message}") },
            { println("onComplete()") }
        )

        println("isDisposed() : " + d.isDisposed)
    }
}


fun main() {
    val demo = ObservableNotifications()
    demo.emit()
}
```

**출력**

```
onNext() : value : RED
onNext() : value : GREEN
onNext() : value : YELLOW
onComplete()
isDisposed() : true
```

모든 값이 발행한 후에 onComplete 이벤트가 발생하였고, 마지막으로 isDisposed 함수를 통하여 정상적으로 구독이 해지 되었는지 확인 할 수 있다.

</br></br>



#### create 함수

