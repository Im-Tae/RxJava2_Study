### Subject

차가운 Observable을 뜨거운 Observable로 바꿔준다.

Subject 클래스의 특성은 Observable의 속성과 구독자 속성이 모두 있다는 것이다.

Observable처럼 데이터를 발행할 수 있고, 구독자처럼 발행된 데이터를 바로 처리할 수도 있다.

Subject 클래스에는 **AsyncSubject**, **BehaviorSubject**, **PublishSubject**, **ReplaySubject** 등이 있다.

</br>



#### AsyncSubject 

Observable에서 발행한 마지막 데이터를 얻어올 수 있는 Subject 클래스이다.

마지막 데이터만 가져오고 이전 데이터는 무시한다.

</br>

<img src="" width = "500" height = "250"  /> </br>



지금까지와 다르게 마블 다이어그램의 아래쪽에 있는 구독자의 시간 표시줄이 여러개 이다.

<br/>

1. 처음 구독자가 subscribe를 호출한다.
2. 첫번째 원과 두번째 원이 발행된 후 두번째 구독자가 subscribe를 호출한다.
3. 마지막으로 세번째 원이 발행되고 발행을 완료한다.

</br>

이때 완료되기 전까지는 구독자에게 데이터를 전달하지 않다가 완료됨과 동시에 첫 번째와 두 번째 구독자에게 마지막 데이터를 발핸하고 종료한다. 아래는 AsyncSubject의 코드 이다.</br>



**입력**

```kotlin
import io.reactivex.subjects.AsyncSubject

class AsyncSubjectExample {
    fun marbleDiagram() {
        val subject = AsyncSubject.create<String>()
        subject.subscribe { data -> println("Subscriber #1 => $data") }
        subject.onNext("1")
        subject.onNext("3")
        subject.subscribe { data -> println("Subscriber #1 => $data") }
        subject.onNext("5")
        subject.onComplete()
    }
}

fun main() {
    val demo = AsyncSubjectExample()
    demo.marbleDiagram()
}
```

**출력**

```
Subscriber #1 => 5
Subscriber #2 => 5
```

</br>



AsyncSubject 클래스는 구독자로도 동작할 수 있다. 아래는 AsyncSubject 클래스가 Observable의 구독자로 동작하는 코드이다.</br>



**입력**

```kotlin
import io.reactivex.Observable
import io.reactivex.subjects.AsyncSubject

class AsyncSubjectExample {
    fun asSubscriber() {
        val temperature = arrayOf(10.1f, 13.4f, 125f)
        val source = Observable.fromArray(*temperature)

        val subject = AsyncSubject.create<Float>()
        subject.subscribe { data -> println("Subscriber #1 => $data") }

        source.subscribe(subject)
    }
}

fun main() {
    val demo = AsyncSubjectExample()
    demo.asSubscriber()
}
```

**출력**

```
Subscriber #1 => 125.0
```

</br>



마지막으로 아래 코드는  AsyncSubject에 onComplete 함수를 호출한 후에 구독하는 코드이다.

마블 다이어그램에 있는 상황은 아니지만 아래 코드도 마지막에 발행된 값을 가져올 수 있다.<br/>



**입력**

```kotlin
package chapter02

import io.reactivex.subjects.AsyncSubject

class AsyncSubjectExample {
    fun afterComplete() {
        val subject = AsyncSubject.create<Int>()
        subject.onNext(10)
        subject.onNext(11)
        subject.subscribe { data -> println("Subscriber #1 => $data") }
        subject.onNext(12)
        subject.onComplete()
        subject.onNext(13)
        subject.subscribe { data -> println("Subscriber #2 => $data") }
        subject.subscribe { data -> println("Subscriber #3 => $data") }
    }
}

fun main() {
    val demo = AsyncSubjectExample()
    demo.afterComplete()
}
```

**출력**

```
Subscriber #1 => 12
Subscriber #2 => 12
Subscriber #3 => 12
```

</br></br>

#### 

#### BehaviorSubject

