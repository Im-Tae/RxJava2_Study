### 흐름 처리

Observable이 데이터를 발행하는 속도 옵서버가 데이터를 받아서 처리하는 속도 사이의 차이가 발생할 때 사용하는 함수이다. 계산 중인데 시작 버튼을 눌러서 계산이 다시 시작이 되면 매우 곤란할 것이다. 이를 방지하기 위해 사용한다. 

</br>



#### sample

특정한 시간 동안 가장 최근에 발행된 데이터만 걸러 준다.

많은 데이터가 들어와도 마지막 데이터만 발행한다.

</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/sample.png?raw=true" width = "550" height = "280"  /> </br>



</br>



**입력**

```kotlin
class SampleExample {

    fun marbleDiagram() {
        val data = arrayOf("1", "7", "2", "3", "6")

        CommonUtils.start()

        // 100ms 간격으로 4개 발행
        val earlySource = Observable.fromArray(*data)
            .take(4)
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any  -> a })

        // 마지막 데이터는 300ms 후에 발행
        val lateSource = Observable.just(data[4])
            .zipWith(Observable.interval(300, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })

        // 2개의 Observable을 결합하고 300ms로 샘플링
        val source = Observable.concat(earlySource, lateSource)
            .sample(300, TimeUnit.MILLISECONDS)

        source.subscribe { value -> Log.it(value) }

        CommonUtils.sleep(1000)
    }
}

fun main() {
    val demo = SampleExample()
    demo.marbleDiagram()
}
```

**출력**

```
RxComputationThreadPool-1 | 534 | value = 7
RxComputationThreadPool-1 | 832 | value = 3
```



100ms 간격으로 데이터 4 개를 발행한다. 그리고 마지막에 300ms 후에 마지막 데이터를 발행한다.

최종적으로 300ms 간격으로 가장 최근에 들어온 값만 발행한다.

sample 함수에 의해서 7과 3만 발행이 되었고 6은 발행이 되지 않은 것을 볼 수 있다.

마지막 데이터인 6을 발행하기 위해서는 sample 함수의 emitLast 인자로 true를 넣어주면 된다.</br>



아래는 emitLast에 인자를 넣어서 마지막 데이터도 받는 예제이다.

</br>



**입력**

```kotlin
class SampleExample {
    
    fun emitLast() {
        val data = arrayOf("1", "7", "2", "3", "6")

        CommonUtils.start()

        val earlySource = Observable.fromArray(*data)
            .take(4)
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any  -> a })

        val lateSource = Observable.just(data[4])
            .zipWith(Observable.interval(300, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })

        val source = Observable.concat(earlySource, lateSource)
            .sample(300, TimeUnit.MILLISECONDS, true)

        source.subscribe { value -> Log.it(value) }

        CommonUtils.sleep(1000)
    }
}

fun main() {
    val demo = SampleExample()
    demo.emitLast()
}
```

**출력**

```
RxComputationThreadPool-1 | 496 | value = 7
RxComputationThreadPool-1 | 794 | value = 3
RxComputationThreadPool-3 | 899 | value = 6
```



마지막 데이터인 6도 발행이 된 것을 볼 수 있다.

</br></br>



#### buffer

일정 시간 동안 데이터를 모아두었다가 한꺼번에 발행 해 준다. 넘치는 데이터 흐름을 제어할 필요가 있을 때 활용할 수 있다.

</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/buffer.png?raw=true" width = "550" height = "300"  /> </br>





처음에 빨강, 노랑, 초록 원을 발행하면 그것을 모아서 List 객체에 전달하는 간단한 구조이다.

buffer 함수는 스케줄러 없이 메인 스레드에서 동작을 하고, 입력되는 값을 count에 저장된 수만큼 모아서 List에 한꺼번에 발행한다.

</br>



**입력**

```kotlin
class BufferExample {

    fun marbleDiagram() {
        val data = arrayOf("1", "2", "3", "4", "5", "6")

        CommonUtils.start()

        val earlySource = Observable.fromArray(*data)
            .take(3)
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any  -> a })

        val middleSource = Observable.just(data[3])
            .zipWith(Observable.timer(300, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })

        val lateSource = Observable.just(data[4], data[5])
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })

        val source = Observable.concat(earlySource, middleSource,lateSource)
            .buffer(3)

        source.subscribe { value -> Log.it(value) }

        CommonUtils.sleep(1000)
    }
}

fun main() {
    val demo = BufferExample()
    demo.marbleDiagram()
}
```

**출력**

```
RxComputationThreadPool-1 | 510 | value = [1, 2, 3]
RxComputationThreadPool-3 | 1014 | value = [4, 5, 6]
```



3 개 씩 모아서 발행하기 위해서 buffer에 3을 넣었다.



아래는 skip을 사용한 buffer 예제이다.

</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/buffer(count,skip).png?raw=true" width = "550" height = "300"  /> </br>



skip 변수는 count 보다 값이 커야 한다. count가 2이고 skip이 3이면 위와 같이 2 개의 데이터를 모으고 1 개는 넘긴다.

</br>

**입력**

```kotlin
class BufferExample {

    fun bufferSkip() {
        val data = arrayOf("1", "2", "3", "4", "5", "6")

        CommonUtils.start()

        val earlySource = Observable.fromArray(*data)
            .take(3)
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any  -> a })

        val middleSource = Observable.just(data[3])
            .zipWith(Observable.timer(300, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })

        val lateSource = Observable.just(data[4], data[5])
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })

        // 3개 모아서 한꺼번에 발행
        val source = Observable.concat(earlySource, middleSource, lateSource)
            .buffer(2, 3)

        source.subscribe { value -> Log.it(value) }

        CommonUtils.sleep(1000)
    }
}

fun main() {
    val demo = BufferExample()
    demo.bufferSkip()
}
```

**출력**

```
RxComputationThreadPool-1 | 383 | value = [1, 2]
RxComputationThreadPool-3 | 884 | value = [4, 5]
```



</br></br>



### ThrottleFirst, ThrottleLast

ThrottleFirst는 주어진 조건에서 가장 먼저 입력된 값을, ThrottleLast는 가장 마지막에 입력된 값을 발행한다.

ThrottleFirst은 어떤 데이터를 발행하면 지정된 시간 동안 다른 데이터를 발행하지 않도록 막는다.

</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/throttlefirst.png?raw=true" width = "550" height = "300"  /> </br>



</br>



**입력**

```kotlin
class ThrottleFirstExample {

    fun marbleDiagram() {
        val data = arrayOf("1", "2", "3", "4", "5", "6")

        CommonUtils.start()

        val earlySource = Observable.just(data[0])
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any  -> a })

        val middleSource = Observable.just(data[1])
            .zipWith(Observable.timer(300, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })

        val lateSource = Observable.just(data[2], data[3], data[4], data[5])
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })
            .doOnNext { log -> Log.d(log) }

        // 200ms 간격으로 throttleFirst 실행
        val source = Observable.concat(earlySource, middleSource, lateSource)
            .throttleFirst(200, TimeUnit.MILLISECONDS)

        source.subscribe { value -> Log.it(value) }

        CommonUtils.sleep(1000)
    }
}


fun main() {
    val demo = ThrottleFirstExample()
    demo.marbleDiagram()
}
```

**출력**

```
RxComputationThreadPool-1 | 289 | value = 1
RxComputationThreadPool-3 | 593 | value = 2
RxComputationThreadPool-4 | debug = 3
RxComputationThreadPool-4 | debug = 4
RxComputationThreadPool-4 | 795 | value = 4
RxComputationThreadPool-4 | debug = 5
RxComputationThreadPool-4 | debug = 6
```



ThrottleFirst는 계산 스케줄러에서 실행한다.

windowDuration 인자는 시간 간격을 지정한다.



처음 100ms가 지난 후에 1을 발행한 후 300ms 동안 기다린 다음 2를 발행한다.

그리고 100ms 간격으로 나머지 값들을 발행한다.

마지막으로 ThrottleFirst를 호출하여 200ms 간격으로 타임 윈도에 먼저 입력된 값을 발행한다.

</br></br>



#### window

groupBy 함수처럼 특정 조건에 맞는 입력 값들을 그룹화해 별도의 Observable을 병렬로 만들지만, throttleFirst나 sample처럼 처리할 수 있는 일부의 값만 받아들일 수 있다.

간단하게 흐름 제어 기능에 groupBy와 비슷한 별도의 Observable 분리 기능을 모두 갖추었다고 생각하면 된다.

</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/window(count).png?raw=true" width = "550" height = "300"  /> </br>



count에 3이라는 값을 받았고, 데이터 3 개가 발행될 때마다 새로운 Observable을 생성한다.



window 함수는 메인 스레드에서 동작하고, 계산 스케줄러를 활용한다.

</br>



**입력**

```kotlin
class WindowExample {

    fun marbleDiagram() {
        val data = arrayOf("1", "2", "3", "4", "5", "6")

        CommonUtils.start()

        val earlySource = Observable.fromArray(*data)
            .take(3)
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any  -> a })

        val middleSource = Observable.just(data[3])
            .zipWith(Observable.timer(300, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })

        val lateSource = Observable.just(data[4], data[5])
            .zipWith(Observable.interval(100, TimeUnit.MILLISECONDS), BiFunction{ a : String, _ : Any -> a })

        // 200ms 간격으로 throttleFirst 실행
        val source = Observable.concat(earlySource, middleSource, lateSource)
            .window(3)

        source.subscribe{ value ->
            Log.d("New Observable Started!!")
            value.subscribe { Log.it(it) }
        }

        CommonUtils.sleep(1000)
    }
}


fun main() {
    val demo = WindowExample()
    demo.marbleDiagram()
}
```

**출력**

```
RxComputationThreadPool-1 | debug = New Observable Started!!
RxComputationThreadPool-1 | 313 | value = 1
RxComputationThreadPool-1 | 405 | value = 2
RxComputationThreadPool-1 | 504 | value = 3
RxComputationThreadPool-2 | debug = New Observable Started!!
RxComputationThreadPool-2 | 806 | value = 4
RxComputationThreadPool-3 | 908 | value = 5
RxComputationThreadPool-3 | 1007 | value = 6
```



3 개의 데이터를 전달 받으면 새로운 Observable을 다시 생성하는 것을 볼 수 있다.

</br></br>



#### debounce

UI 같은 곳에서 버튼을 빠르게 눌렀을 때 마지막에 누른 이벤트만 처리할 때 사용한다.

</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/debounce.png?raw=true" width = "600" height = "300"  /> </br>



빨간색 원은 지정한 시간 간격 안에 들어왔고 문제 없이 발행되었다.

노란색 원의 경우에는 발행되기 전에 초록색 원이 들어와서 초록색 원이 대신 발행되었다.

파란색 원도 지정한 시간 간격 안에 들어와서 문제 없이 발행된 것을 볼 수 있다.

</br>



**입력**

```kotlin
class DebounceExample {

    fun marbleDiagram() {
        val data = arrayOf("1", "2", "3", "5")

        val source = Observable.concat(
            Observable.timer(100, TimeUnit.MILLISECONDS).map { _ -> data[0] },
            Observable.timer(300, TimeUnit.MILLISECONDS).map { _ -> data[1] },
            Observable.timer(100, TimeUnit.MILLISECONDS).map { _ -> data[2] },
            Observable.timer(300, TimeUnit.MILLISECONDS).map { _ -> data[3] }
        ).debounce(200, TimeUnit.MILLISECONDS)

        source.subscribe { value -> Log.it(value) }

        CommonUtils.sleep(1000)
    }
}


fun main() {
    val demo = DebounceExample()
    demo.marbleDiagram()
}
```

**출력**

```
RxComputationThreadPool-2 | value = 1
RxComputationThreadPool-2 | value = 3
RxComputationThreadPool-5 | value = 5
```



각각 시간 간격이 서로 다르므로 concat을 사용하여 데이터를 발행했다.

</br></br>