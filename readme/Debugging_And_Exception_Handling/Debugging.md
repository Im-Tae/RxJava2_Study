### 디버깅

RxJava의 코드에는 로그를 넣을 수 있는 공간이 없다.

Observable로 시작하는 업스트림과 그것을 받아서 처리하는 다운스트림이 동일한 문장으로 이루어져 있기 때문이다. 따라 가독성이 높지만 예외 코드를 넣는데 어려움이 있다.

</br>



#### doOnNext, doOnComplete, doOnError

Observable에서 어떤 데이터를 발행할 때는 onNext, 중간에 에러가 발생하면 onError, 모든 데이터를 발행하면 onComplete 이벤트가 발생한다.</br>



**입력**

```kotlin
class DoOnExample {
    fun basic() {
        val orgs = arrayOf("1", "2", "3")
        val source = Observable.fromArray(*orgs)

        source.doOnNext{ data -> Log.d(data) }
            .doOnComplete{ Log.d("onComplete()") }
            .doOnError{ e -> Log.d(e.message.toString()) }
            .subscribe { data -> Log.it(data) }
    }
}

fun main() {
    val demo = DoOnExample()
    demo.basic()
}
```

**출력**

```
main | debug = 1
main | value = 1
main | debug = 2
main | value = 2
main | debug = 3
main | value = 3
main | debug = onComplete()
```

모두 main 스레드에서 실행되었고, 발행한 데이터는 value로 표시하고 이벤트 발생은 debug로 표시했다.



아래는 onError 이벤트 동작을 확인하는 코드이다.

</br>



**입력**

```kotlin
class DoOnExample {
    fun withError() {
        val divider = arrayOf(10, 5, 0)

        Observable.fromArray(*divider)
            .map { div -> 1000 / div }
            .doOnNext { data -> Log.d(data) }
            .doOnComplete { Log.d("onComplete()") }
            .doOnError{ e -> Log.d(e.message.toString()) }
            .subscribe { data -> Log.it(data) }
    }
}

fun main() {
    val demo = DoOnExample()
    demo.withError()
}
```

**출력**

```
main | debug = 100
main | value = 100
main | debug = 200
main | value = 200
main | debug = / by zero
io.reactivex.exceptions.OnErrorNotImplementedException: ...
```

Observable이 1000을 어떤 숫자로 나누며, 나누는 수로 10, 5, 0을 대입한다. 0으로는 나눌 수 없기 때문에 에러가 발생한다.

</br>



#### doOnEach

이벤트를 한 번에 처리할 수 있도록 해준다.



아래는 Notification 객체를 전달 받아서 처리하는 방법이다.</br>



**입력**

```kotlin
class DoOnExample {
    fun doOnEach() {
        val data = arrayOf("ONE", "TWO", "THREE")
        val source = Observable.fromArray(*data)

        source.doOnEach{noti ->
            if(noti.isOnNext) Log.d(noti.value.toString())
            if(noti.isOnComplete) Log.d("onComplete()")
            if (noti.isOnError) Log.d(noti.error?.message.toString())
        }
            .subscribe { data -> Log.it(data) }
    }
}

fun main() {
    val demo = DoOnExample()
    demo.doOnEach()
}
```

**출력**

```
main | debug = ONE
main | value = ONE
main | debug = TWO
main | value = TWO
main | debug = THREE
main | value = THREE
main | debug = onComplete()
```

Notification 객체는 이벤트 종류를 boolean 타입의 isOnNext, isOnComplete, isOnError로 제공한다.

onNext는 getValue를 통해 발행한 값을 알 수 있고, onError는 getError를 통해 Throwable 객체를 얻어올 수 있다.</br>



아래는 Observer을 사용한 예제이다.</br>



**입력**

```kotlin
class DoOnExample {
    fun doOnEachObserver() {
        val orgs = arrayOf("1", "3", "5")
        val source = Observable.fromArray(*orgs)

        source.doOnEach(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                // doOnEach에서는 onSubscribe 함수가 호출되지 않는다.
            }

            override fun onNext(value: String) {
                Log.d(value)
            }

            override fun onError(e: Throwable) {
                Log.d(e.message.toString())
            }

            override fun onComplete() {
                Log.d("onComplete()")
            }
        }).subscribe { data -> Log.it(data) }
    }
}

fun main() {
    val demo = DoOnExample()
    demo.doOnEachObserver()
}
```

**출력**

```
main | debug = 1
main | value = 1
main | debug = 3
main | value = 3
main | debug = 5
main | value = 5
main | debug = onComplete()
```

doOnEach 함수는 onNext, onError, onComplete 이벤트만 처리하기 때문에 onSubscribe 함수는 호출되지 않는다.

Observer로 doOnEach를 구현하는 것은 자주 사용하는 방법이 아니다. 따라서 Notification을 활용하는 것이 더 좋을 것이다.



</br>

#### doOnSubscribe, doOnDispose, etc

Observable을 구독했을 때와 구독 해지했을 때의 이벤트를 처리할 수 있다.

doOnSubscribe 함수는 Observable을 구독했을 때 작업을 할 수 있고 인자는 Disposable이다.

doOnDispose 함수는 Observable의 구독을 해지했을 대 호출되고 인자는 Action 객체이다.

</br>



**입력**

```kotlin
class DoOnExample {
    fun doOnSubscribeAndDispose() {
        val orgs = arrayOf("1", "3", "5", "2", "6")
        val source = Observable.fromArray(*orgs)
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction { a: Any, _:Any -> a })
            .doOnSubscribe { _ -> Log.d("onSubscribe()") }
            .doOnDispose{ Log.d("onDispose()") }

        val d = source.subscribe { data -> Log.it(data) }
        CommonUtils.sleep(200)
        d.dispose()
        CommonUtils.sleep(300)
    }
}

fun main() {
    val demo = DoOnExample()
    demo.doOnSubscribeAndDispose()
}
```

**출력**

```
main | debug = onSubscribe()
RxComputationThreadPool-1 | value = 1
RxComputationThreadPool-1 | value = 3
main | debug = onDispose()
```

100ms 간격으로 orgs 배열의 데이터를 발행한 후 doOnSubscribe와 doOnDispose로 로그를 출력한다.

Observable은 zipWith 함수를 활용하여 interval 함수와 합성했기 때문에 main 스레드가 아닌 계산 스케줄러에서 동작한다.</br>



아래는 doOnSubscribe와 doOnDispose 함수를 한꺼번에 호출하는 doOnLifecycle을 사용한 예제이다.</br>



**입력**

```kotlin
class DoOnExample {
    fun doOnLifecycle() {
        val orgs = arrayOf("1", "3", "5", "2", "6")
        val source = Observable.fromArray(*orgs)
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction { a: Any, _:Any -> a })
            .doOnLifecycle( { _ -> Log.d("onSubscribe()") }, { Log.d("onDispose()") })

        val d = source.subscribe { data -> Log.it(data) }
        CommonUtils.sleep(200)
        d.dispose()
        CommonUtils.sleep(300)
    }
}

fun main() {
    val demo = DoOnExample()
    demo.doOnLifecycle()
}
```

**출력**

```
main | debug = onSubscribe()
RxComputationThreadPool-1 | value = 1
RxComputationThreadPool-1 | value = 3
main | debug = onDispose()
```



아래는 doOnTerminate 함수를 사용한 예제이다.

</br>



**입력**

```kotlin
class DoOnExample {
    fun doOnTerminate() {
        val orgs = arrayOf("1", "3", "5")
        val source = Observable.fromArray(*orgs)

        source.doOnTerminate { Log.d("onTerminate()") }
            .doOnComplete { Log.d("onComplete") }
            .doOnError{ e -> Log.d(e.message.toString()) }
            .subscribe { data -> Log.it(data) }
    }
}

fun main() {
    val demo = DoOnExample()
    demo.doOnTerminate()
}
```

**출력**

```
main | value = 1
main | value = 3
main | value = 5
main | debug = onTerminate()
main | debug = onComplete
```

onComplete 혹은 onError 이벤트 발생 직전에 호출된다.</br>





마지막으로 아래는 doFinally 함수를 사용한 예제이다.

</br>



**입력**

```kotlin
class DoOnExample {
    fun doFinally() {
        val orgs = arrayOf("1", "3", "5")
        val source = Observable.fromArray(*orgs)

        source.doFinally { Log.d("doFinally()") }
            .doOnComplete { Log.d("onComplete") }
            .doOnError{ e -> Log.d(e.message.toString()) }
            .subscribe { data -> Log.it(data) }
    }
}

fun main() {
    val demo = DoOnExample()
    demo.doFinally()
}
```

**출력**

```
main | value = 1
main | value = 3
main | value = 5
main | debug = onComplete
main | debug = doFinally()
```

onError, onComplete, onDispose 이벤트 발생 시에 호출된다.</br>
