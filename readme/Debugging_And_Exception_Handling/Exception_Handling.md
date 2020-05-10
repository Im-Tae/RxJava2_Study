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



전체적으로 보면 OnErrorNotImplementedException 예외를 던지라고 알려준다.</br>



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

rxjava에서는 에러도 데이터로 보는 것이 적절하다. 따라서 예외 처리하는 방식중에 하나는 예외가 발생했을 때 에러를 의미하는 다른 데이터로 대체하는 것이다.

onError 이벤트는 데이터 흐름이 바로 중단되므로 subcribe 함수를 호출할 때 onError 이벤트를 처리하는 것은 Out Of Memory 같은 중대한 에러가 발생했을 때만 활용한다.

onErrorReturn 함수는 에러가 발생했을 때 내가 원하는 데이터로 대체하는 함수이다.</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/onErrorReturn.png?raw=true" width = "550" height = "220"  /> </br>



</br>



에러가 발생하는 경우 onErrorReturn 함수는 인자로 넘겼던 기본값을 대신 발행하고 onComplete 이벤트가 발생한다. 



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



Integer.parseInt에서 발생하는 NumberFormatException을 onErrorReturn을 통해 예외처리를 한 것이다. onErrorReturn에서 예외 발생시에 음수 값을 리턴하도록 했고, data가 0보다 작으면 에러 로그를 출력하도록 했다.



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