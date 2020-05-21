### 예외 처리

지금까지 onError 이벤트를 통해 예외 처리를 하였다. 하지만 try-catch 문을 사용하여 예외 처리를 할 수 있다.



아래는 try-catch문을 사용한 예외 처리 코드이다.</br>



**입력**

```kotlin
class ExceptionHandling {
    fun cannotCatch() {
        val source = Observable.create{ emitter: ObservableEmitter<String> ->
            emitter.onNext("1")
            emitter.onError(Exception("Some Error"))
            emitter.onNext("3")
            emitter.onComplete()
        }

        try {
            source.subscribe { data -> Log.it(data) }
        } catch (e: Exception) {
            Log.it(e.message.toString())
        }
    }
}

fun main() {
    val demo = ExceptionHandling()
    demo.cannotCatch()
}
```

**출력**

```
main | value = 1
io.reactivex.exceptions.OnErrorNotImplementedException: The exception was not handled due to missing onError handler in the subscribe() method call. Further reading: https://github.com/ReactiveX/RxJava/wiki/Error-Handling | java.lang.Exception: Some Error
...
```



try-catch문이 동작하지 않는 것을 볼 수 있다.</br>



아래는 Observable.subscribe의 코드이다.

```java
public final Disposable subscribe(Consumer<? super T> onNext) {
        return subscribe(onNext, Functions.ON_ERROR_MISSING, Functions.EMPTY_ACTION, Functions.emptyConsumer());
    }
```



함수를 호출할 때는 onNext 인자만 넘겼고, 두 번째 인자로 Functions.EMPTY_ACTION을 입력했다.

</br>



아래는 ON_ERROR_MISSING의 코드이다.

```java
public static final Consumer<Throwable> ON_ERROR_MISSING = new OnErrorMissingConsumer();


static final class OnErrorMissingConsumer implements Consumer<Throwable> {
        @Override
        public void accept(Throwable error) {
            RxJavaPlugins.onError(new OnErrorNotImplementedException(error));
        }
    }
```



전체적으로 보면 OnErrorNotImplementedException 예외를 던지라고 알려 준다.</br>



마지막으로 아래는 onError 코드이다.

```java
public static void onError(@NonNull Throwable error) {
        Consumer<? super Throwable> f = errorHandler;

        if (error == null) {
            error = new NullPointerException("onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        } else {
            if (!isBug(error)) {
                error = new UndeliverableException(error);
            }
        }

        if (f != null) {
            try {
                f.accept(error);
                return;
            } catch (Throwable e) {
                // Exceptions.throwIfFatal(e); TODO decide
                e.printStackTrace(); // NOPMD
                uncaught(e);
            }
        }

        error.printStackTrace(); // NOPMD
        uncaught(error);
    }
```



f 변수는 subscribe 함수에서 전달했던 ON_ERROR_MISSING이다.

Observable을 생성하여 onError를 호출하면 rxjava에서 onError을 처리하기 때문에 결과적으로 try-catch문은 rxjava에서 사용할 수 없다.

</br></br>



#### onErrorReturn

rxjava에서는 에러도 데이터로 보는 것이 적절하다. 따라서 예외 처리하는 방식 중에 하나는 예외가 발생했을 때 에러를 의미하는 다른 데이터로 대체하는 것이다.

onError 이벤트는 데이터 흐름이 바로 중단되므로 subcribe 함수를 호출할 때 onError 이벤트를 처리하는 것은 Out Of Memory 같은 중대한 에러가 발생했을 때만 활용한다.

onErrorReturn 함수는 에러가 발생했을 때 내가 원하는 데이터로 대체하는 함수이다.</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/onErrorReturn.png?raw=true" width = "550" height = "220"  /> </br>



</br>



에러가 발생하는 경우 onErrorReturn 함수는 인자로 넘겼던 기본 값을 대신 발행하고 onComplete 이벤트가 발생한다. 



아래는 onErrorReturn의 예제이다.</br>



**입력**

```kotlin
class ExceptionHandling {
    fun onErrorReturn() {
        val grades = arrayOf("70", "88", "$100", "93", "83")

        val source = Observable.fromArray(*grades)
            .map { data -> Integer.parseInt(data) }
            .onErrorReturn { -1 }

        source.subscribe { data ->
            if (data < 0) {
                Log.it("Wrong Data Found")
                return@subscribe
            }

            Log.it("Grade is $data")
        }
    }
}

fun main() {
    val demo = ExceptionHandling()
    demo.onErrorReturn()
}
```

**출력**

```
main | value = Grade is 70
main | value = Grade is 88
main | value = Wrong Data Found
```



Integer.parseInt에서 발생하는 NumberFormatException을 onErrorReturn을 통해 예외 처리를 한 것이다. onErrorReturn에서 예외 발생 시에 음수 값을 리턴 하도록 했고, data가 0보다 작으면 에러 로그를 출력하도록 했다.



onErrorReturn의 장점으로는 예외 발생이 예상되는 부분을 선언적으로 처리할 수 있고, Observable을 생성하는 측과 구독하는 측이 서로 다를 수 있어서 생성하는 측에서 발생할 수 있는 예외 처리를 미리 해두면 구독자가 선언된 예외 상황을 보고 그에 맞는 처리를 할 수 있다는 것이다.</br>



아래는 onError에서 예뢰 처리를 한 것이다.</br>



**입력**

```kotlin
class ExceptionHandling {
    fun onError() {
        val grades = arrayOf("70", "88", "$100", "93", "83")

        val source = Observable.fromArray(*grades)
            .map { data -> Integer.parseInt(data) }

        source.subscribe(
            { data -> Log.it("Grade is $data") }
        )
        { Log.it("Wrong Data found!!") }
    }
}

fun main() {
    val demo = ExceptionHandling()
    demo.onError()
}
```

**출력**

```
main | value = Grade is 70
main | value = Grade is 88
main | value = Wrong Data found!!
```



마지막으로 아래는 onErrorReturn 함수와 비슷한 onErrorReturnItem 함수를 사용한 예제이다.</br>



**입력**

```kotlin
class ExceptionHandling {
    fun onErrorReturnItem() {
        val grades = arrayOf("70", "88", "$100", "93", "83")

        val source = Observable.fromArray(*grades)
            .map { data -> Integer.parseInt(data) }
            .onErrorReturnItem(-1)

        source.subscribe { data ->
            if (data < 0) {
                Log.it("Wrong Data Found")
                return@subscribe
            }

            Log.it("Grade is $data")
        }
    }
}

fun main() {
    val demo = ExceptionHandling()
    demo.onErrorReturnItem()
}
```

**출력**

```
main | value = Grade is 70
main | value = Grade is 88
main | value = Wrong Data Found
```



</br></br>



#### onErrorResumeNext

onErrorReturn과 onErrorReturnItem은 에러가 발생한 시점에 특정 값으로 대체하는 것이다.

onErrorResumeNext은 에러가 발생했을 때 원하는 Observable로 대체하는 방법이다.

에러 발생 시에 데이터를 교체하는 것 뿐만 아니라 관리자에게 이메일을 보내거나, 자원을 해제하는 등의 추가 작업을 할 때 유용하다.</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/onErrorResumeNext.png?raw=true" width = "550" height = "220"  /> </br>



</br>

에러 발생 시에 특정 값을 발행한다는 점은 같다. 하지만 특정 값을 원하는 Observable로 설정할 수 있다는 점이 다르다.



아래는 onErrorResumeNext의 코드이다.</br>



**입력**

```kotlin
class ExceptionHandling {
    fun onErrorResumeNext() {
        val salesData = arrayOf("100", "200", "A300")

        val onParseError = Observable.defer {
            Log.d("send email to administrator")
            Observable.just(-1)
        }.subscribeOn(Schedulers.io())

        val source = Observable.fromArray(*salesData)
            .map { data -> Integer.parseInt(data) }
            .onErrorResumeNext(onParseError)

        source.subscribe { data ->
            if (data < 0) {
                Log.it("Wrong Data Found")
                return@subscribe
            }

            Log.it("Sales data : $data")
        }
    }
}

fun main() {
    val demo = ExceptionHandling()
    demo.onErrorResumeNext()
}
```

**출력**

```
main | value = Sales data : 100
main | value = Sales data : 200
RxCachedThreadScheduler-1 | debug = send email to administrator
RxCachedThreadScheduler-1 | value = Wrong Data Found
```



에러 발생 시 관리자에게 이메일을 보내고 -1이라는 데이터를 발행하는 Observable로 대체한다.

onParseError 변수는 subscribeOn 함수를 호출하여 IO 스케줄러에서 실행한다.



onErrorResumeNext 함수는 onErrorReturn 함수처럼 Throwable을 받아오는 오버로딩도 제공한다.

</br>



#### retry

인터넷 문제 때문에 통신이 되지 않을 때 일정 시간 후에 다시 통신을 요청하는 것이 필요하다.

retry를 통해 간단하게 해결할 수 있다.</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/Retry.png?raw=true" width = "550" height = "220"  /> </br>



</br>

retry 함수는 onError 이벤트 발생 시에 다시 subscribe하여 재 구독 하도록 되어있다.

 아래는 대기 시간 없는 retry를 사용한 예제이다.</br>



**입력**

```kotlin
class RetryExample {
    fun try5() {
        CommonUtils.start()

        val url = "https://api.github.com/zen"

        val source = Observable.just(url)
            .map(OkHttpHelper()::get)
            .retry(5)
            .onErrorReturnItem("-500")

        source.subscribe { data -> Log.it("result : $data") }
    }
}

fun main() {
    val demo = RetryExample()
    demo.try5()
}
```

**출력**

```
main | 14512 | value = result : -500
```

총 5 회 재 시도 후 최종 요청이 실패 처리된 것을 볼 수 있다.

하지만 위와 같이 대기 시간이 없는 요청은 도움이 되지 않는다.

</br>



아래는 대기 시간이 있는 예제이다.</br>



**입력**

```kotlin
class RetryExample {
    fun retryWithDelay() {
        val RETRY_MAX = 5
        val RETRY_DELAY = 1000L
        
        CommonUtils.start()
        
        val url = "https://api.github.com/zen"

        val source = Observable.just(url)
            .map(OkHttpHelper()::get)
            .retry{
                retryCnt : Int, _ : Throwable ->
                 Log.it("retryCnt = $retryCnt")
                CommonUtils.sleep(RETRY_DELAY)

                retryCnt < RETRY_MAX
            }
            .onErrorReturnItem("-500")
        
        source.subscribe { data -> Log.it("result : $data") }
    }
}

fun main() {
    val demo = RetryExample()
    demo.retryWithDelay()
}
```

**출력**

```
main | 2591 | value = retryCnt = 1
main | 3592 | value = retryCnt = 2
main | 4592 | value = retryCnt = 3
main | 5593 | value = retryCnt = 4
main | 6593 | value = retryCnt = 5
main | 7593 | value = result : -500
```



재 시도 횟수는 5 번으로 설정하고 간격은 1000ms로 지정했다.

retry 함수는 인자로 retryCnt와 Throwable 객체를 전달 받는다.



재 시도 횟수를 제한하기 위해서 5 회 이내일 때는 true, 이후에는 false를 리턴 한다.

</br></br>



#### retryUntil

특정 조건이 충족될 때까지만 재 시도하는 함수이다.

</br>



**입력**

```kotlin
class RetryExample {
    fun retryUntil() {

        CommonUtils.start()

        val url = "https://api.github.com/zen"

        val source = Observable.just(url)
            .map(OkHttpHelper()::get)
            .subscribeOn(Schedulers.io())
            .retryUntil {
                if (CommonUtils.isNetworkAvailable())
                    true
                CommonUtils.sleep(1000)
                false
            }

        source.subscribe { data -> Log.it(data) }

        CommonUtils.sleep(5000)
    }
}

fun main() {
    val demo = RetryExample()
    demo.retryUntil()
}
```

**출력**

```
RxCachedThreadScheduler-1 | Network is not avaliable
RxCachedThreadScheduler-1 | Network is not avaliable
RxCachedThreadScheduler-1 | Network is not avaliable
RxCachedThreadScheduler-1 | Network is not avaliable
RxCachedThreadScheduler-1 | Network is not avaliable
```



보통 재 시도 로직은 별도의 스레드에서 동작하기 때문에 IO 스케줄러를 활용한다.

isNetworkAvailable 함수를 통해 네트워크가 사용 가능한 상태인지 확인하고, true를 리턴한다.

네트워크를 사용할 수 없는 상태이면 1000ms 후에 재 시도한다.



isNetworkAvailable 함수는 아래와 같다.</br>



**입력**

```kotlin
class CommonUtils {
    
    companion object {
        
        fun isNetworkAvailable(): Boolean {
            try {
                return InetAddress.getByName("www.google.com").isReachable(1000)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false
        }
    }
}
```

isNetworkAvailable 함수는 구글에 접속할 수 있는지 확인하여 간접적으로 네트워크를 사용할 수 있는지 확인하도록 되어있다.

</br>



#### retryWhen

재 시도 함수 중에 가장 복잡한 함수이다.

</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/retryWhen.png?raw=true" width = "550" height = "250"  /> </br>





재 시도를 하며, 재 시도 횟수가 늘어날 때마다 재 시도 시간이 늘어난다.

</br>



**입력**

```kotlin
class RetryExample {
    fun retryWhen() {

        Observable.create { emitter: ObservableEmitter<String?> ->
            println("subscribing")
            emitter.onError(RuntimeException("always fails"))
        }
            .retryWhen { attempts: Observable<Throwable?> ->
                attempts.zipWith(
                    Observable.range(1, 3),
                    BiFunction { n: Throwable?, i: Int -> i }
                ).flatMap { i: Int ->
                    println("delay retry by $i second(s)")
                    Observable.timer(i.toLong(), TimeUnit.SECONDS)
                }
            }.blockingForEach { x: String? -> println(x) }

        Observable.create{ emitter: ObservableEmitter<String> ->
            emitter.onError(RuntimeException("always fails"))
        }.retryWhen { attemps : Observable<Throwable> ->
            attemps.zipWith(
                Observable.range(1, 3),
                BiFunction { _: Throwable, i: Int -> i }
            ).flatMap { i ->
                Log.it("delay retry by $i second(s)")
                Observable.timer(i.toLong(), TimeUnit.SECONDS)
            }
        }.blockingForEach { data -> Log.it(data) }
    }
}

fun main() {
    val demo = RetryExample()
    demo.retryWhen()
}
```

**출력**

```
subscribing
delay retry by 1 second(s)
subscribing
delay retry by 2 second(s)
subscribing
delay retry by 3 second(s)
subscribing
```



Observable은 데이터 발행을 항상 실패하도록 설정하였다.

attemps는 Observable이다. 재 시도를 할 때 Observable.range와 zip 함수로 두 Observalbe을 합성한다. 즉, 3 번 재 시도 한다는 뜻이다. 또한 재 시도 할 때마다 timer 함수를 호출하여 1000ms씩 대기 시간을 늘린다.

</br>