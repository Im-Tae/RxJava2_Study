### 스케줄러 종류

Rxjava는 다양한 스케줄러를 제공한다.

특정 스케줄러를 사용하다가 다른 스케줄러로 변경하기 쉽다.

마치 map 함수를 한 번 더 호출하는 것처럼 새롭게 스케줄러를 추가하거나 기존의 스케줄러를 다른 것으로 교체할 수 있다.



#### 뉴 스레드 스케줄러

이름 그대로 새로운 스케줄러를 생성한다.

Rxjava의 스케줄러는 subscribeOn과 observeOn에 나눠서 적용할 수 있는데, 아래는 subscribeOn만 적용한 코드이다.</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class NewThreadSchedulerExample {
    fun emit() {
        val orgs = arrayOf("1", "3", "5")

        Observable.fromArray(*orgs)
            .doOnNext { data -> Log.it("Original data : $data") }
            .map { data -> "<<$data>>" }
            .subscribeOn(Schedulers.newThread())
            .subscribe { data -> Log.it(data) }
        CommonUtils.sleep(500)

        Observable.fromArray(*orgs)
            .doOnNext { data -> Log.it("Original data : $data") }
            .map { data -> "##$data##" }
            .subscribeOn(Schedulers.newThread())
            .subscribe { data -> Log.it(data) }
        CommonUtils.sleep(500)

    }
}

fun main() {
    val demo = NewThreadSchedulerExample()
    demo.emit()
}
```

**출력**

```
RxNewThreadScheduler-1 | value = Original data : 1
RxNewThreadScheduler-1 | value = <<1>>
RxNewThreadScheduler-1 | value = Original data : 3
RxNewThreadScheduler-1 | value = <<3>>
RxNewThreadScheduler-1 | value = Original data : 5
RxNewThreadScheduler-1 | value = <<5>>
RxNewThreadScheduler-2 | value = Original data : 1
RxNewThreadScheduler-2 | value = ##1##
RxNewThreadScheduler-2 | value = Original data : 3
RxNewThreadScheduler-2 | value = ##3##
RxNewThreadScheduler-2 | value = Original data : 5
RxNewThreadScheduler-2 | value = ##5##
```



뉴 스레드 스케줄러를 사용했기 때문에 첫 번째 Observable은 RxNewThreadScheduler-1에서 실행되었고, 두 번째 Observabled은 RxNewThreadScheduler-2에서 실행된 것을 볼 수 있다.

각 스레드에서 값이 처리되는 것을 기다려야 하기 때문에 sleep를 제거하면 발행이 되지 않거나, 값이 뒤섞이게 된다.

</br></br>



#### 계산 스케줄러

RxJava에서 추천하는 스케줄러는 계산 스케줄러, IO 스케줄러, 트램펄린 스케줄러이다.

계산 스케줄러 CPU에 대응하는 계산용 스케줄러이다. '계산' 작업을 할 때는 대기 시간 없이 빠르게 결과를 도출하는 것이 중요하다. 간단히, 입출력 작업을 하지 않는 스케줄러라고 생각하면 된다.

내부적으로 스레드 풀을 생성하며, 스레드 개수는 기본적으로 프로세서 개수와 동일하다.

</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ComputationSchedulerExample {
    fun emit() {
        val orgs = arrayOf("1", "3", "5")

        val source = Observable.fromArray(*orgs)
            .zipWith(
                Observable.interval(100, TimeUnit.MILLISECONDS),
                BiFunction { a: String, _: Long -> a }
            )

        // 구독 #1
        source.map { item -> "<<$item>>" }
            .subscribeOn(Schedulers.computation())
            .subscribe { data -> Log.it(data) }

        // 구독 #2
        source.map { item -> "##$item##" }
            .subscribeOn(Schedulers.computation())
            .subscribe { data -> Log.it(data) }
        CommonUtils.sleep(1000)
    }
}

fun main() {
    val demo = ComputationSchedulerExample()
    demo.emit()
}
```

**출력**

```
RxComputationThreadPool-3 | value = <<1>>
RxComputationThreadPool-4 | value = ##1##
RxComputationThreadPool-3 | value = <<3>>
RxComputationThreadPool-4 | value = ##3##
RxComputationThreadPool-3 | value = <<5>>
RxComputationThreadPool-4 | value = ##5##
```



데이터와 시간을 합성할 수  있는 zipWith를 사용하여 interval을 통해 시간 간격으로 데이터를 발행한다.

동시 실행을 위해서 첫 번째 구독과 두 번째 구독 사이에 sleep을 제거했다.

interval은 기본적으로 계산 스케줄러를 사용하기 때문에 subscribeOn을 제거해도 동일하게 동작한다.</br>



여러 번 실행하다 보면 아래와 같은 결과를 얻기도 한다.</br>



**출력**

```
RxComputationThreadPool-3 | value = <<1>>
RxComputationThreadPool-3 | value = ##1##
RxComputationThreadPool-3 | value = <<3>>
RxComputationThreadPool-3 | value = ##3##
RxComputationThreadPool-3 | value = <<5>>
RxComputationThreadPool-3 | value = ##5##
```



첫 번째 구독과 두 번째 구독이 거의 동시에 이루어지기 때문에 RxJava 내부에서 동일한 스레드에 작업을 할당했기 때문이다.



</br></br>



#### IO 스케줄러

네트워크 상의 요청을 처리하거나 각종 입출력 작업을 실행하기 위한 스케줄러이다.

계산 스케줄러는 CPU 개수만큼 스레드를 생성하지만, IO 스케줄러는 필요할 때마다 스레드를 계속 생성한다. 입출력 작업은 비동기로 실행되지만 결과를 얻기까지 대기 시간이 길다.

</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File

class IOSchedulerExample {
    fun emit() {
        val root = "c:\\"
        val files = File(root).listFiles()

        val source = Observable.fromArray(*files)
            .filter { f -> !f.isDirectory }
            .map { f -> f.absolutePath }
            .subscribeOn(Schedulers.io())

        source.subscribe { data -> Log.it(data) }
        CommonUtils.sleep(500)
    }
}

fun main() {
    val demo = IOSchedulerExample()
    demo.emit()
}
```

**출력**

```
RxCachedThreadScheduler-1 | value = c:\agentlog.txt
RxCachedThreadScheduler-1 | value = c:\hiberfil.sys
RxCachedThreadScheduler-1 | value = c:\pagefile.sys
RxCachedThreadScheduler-1 | value = c:\swapfile.sys
```



C 드라이브 루트 디렉터리인 root로 File 객체를 생성하여 listFiles 메서드를 호출하여 파일 목록을 File 배열로 받는다. 그리고 디렉터리는 제외하고 파일들만 필터링 한다.

</br></br>



#### 트램펄린 스케줄러

새로운 스레드를 생성하지 않고 현재 스레드에 무한한 크기의 대기 행렬(Queue)을 생성하는 스케줄러이다.

</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class TrampolineSchedulerExample {
    fun emit() {
        val orgs = arrayOf("1", "3", "5")

        val source = Observable.fromArray(*orgs)

        // 구독 #1
        source.subscribeOn(Schedulers.trampoline())
            .map { data -> "<<$data>>" }
            .subscribe { data -> Log.it(data) }

        // 구독 #2
        source.subscribeOn(Schedulers.trampoline())
            .map { data -> "##$data##" }
            .subscribe { data -> Log.it(data) }
        CommonUtils.sleep(500)
    }
}

fun main() {
    val demo = TrampolineSchedulerExample()
    demo.emit()
}
```

**출력**

```
main | value = <<1>>
main | value = <<3>>
main | value = <<5>>
main | value = ##1##
main | value = ##3##
main | value = ##5##
```



배열 데이터를 트램펄린 스케줄러를 활용해 Observable에서 발행한다.

subsribeOn 함수의 호출 위치가 IO 스케줄러의 예제보다 앞에 있는데 호출 위치는 상관없다.

처음에 지정한 스레드로 구독자에게 데이터를 발행한다.</br>

출력 결과를 보면 새로운 스레드를 생성하지 않고 메인 스레드에서 모든 작업을 실행하는 것을 볼 수 있다. 큐에 작업을 넣은 후 1 개씩 꺼내어 동작하므로 첫 번째 구독과 두 번째 구독의 실행 순서가 바뀌는 경우가 발생하지 않는다.

</br></br>



#### 싱글 스레드 스케줄러

RxJava 내부에서 단일 스레드를 별도로 생성하여 구독 작업을 처리한다.

단, 생성된 스레드는 여러 번 구독 요청이 와도 공통으로 사용한다.

</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class SingleSchedulerExample {
    fun emit() {
        val numbers = Observable.range(100, 5)
        val chars = Observable.range(0, 5)
            .map(CommonUtils()::numberToAlphabet)

        numbers.subscribeOn(Schedulers.single())
            .subscribe { data -> Log.it(data) }

        chars.subscribeOn(Schedulers.single())
            .subscribe { data -> Log.it(data) }
        CommonUtils.sleep(500)
    }
}

fun main() {
    val demo = SingleSchedulerExample()
    demo.emit()
}
```

**출력**

```
RxSingleScheduler-1 | value = 100
RxSingleScheduler-1 | value = 101
RxSingleScheduler-1 | value = 102
RxSingleScheduler-1 | value = 103
RxSingleScheduler-1 | value = 104
RxSingleScheduler-1 | value = A
RxSingleScheduler-1 | value = B
RxSingleScheduler-1 | value = C
RxSingleScheduler-1 | value = D
RxSingleScheduler-1 | value = E
```



아래는 numberToAlphabet 메서드의 코드이다.</br>

**입력**

```kotlin
class CommonUtils {
    var ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun numberToAlphabet(x: Int) = ALPHABET[x % ALPHABET.length].toString()
}
```



싱글 스레드 스케줄러에서 실행하면 비록 여러 개 Observable이 있어도 별도 마련해놓은 단일 스레드에서 차례로 실행한다.

</br></br>



#### Executor 변환 스케줄러

java.util.current 패키지에서 제공하는 실행자를 변환하여 스케줄러를 생성할 수 있다.

추천하는 방법은 아니므로 기존에 사용하던 Executor 클래스를 재 사용할 때만 한정적으로 활용하니 알아 만 두면 좋다.

</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class ExecutorSchedulerExample {
    fun emit() {
        val THREAD_NUM = 10

        val data = arrayOf("1", "3", "5")

        val source = Observable.fromArray(*data)
        val executor = Executors.newFixedThreadPool(THREAD_NUM)

        source.subscribeOn(Schedulers.from(executor))
            .subscribe { value -> Log.it(value) }

        source.subscribeOn(Schedulers.from(executor))
            .subscribe { value -> Log.it(value) }
        CommonUtils.sleep(500)
    }
}

fun main() {
    val demo = ExecutorSchedulerExample()
    demo.emit()
}
```

**출력**

```
pool-1-thread-1 | value = 1
pool-1-thread-1 | value = 3
pool-1-thread-1 | value = 5
pool-1-thread-2 | value = 1
pool-1-thread-2 | value = 3
pool-1-thread-2 | value = 5
```



executor 변수는 고정 개수의 스레드 풀을 생성한다. 그리고 첫 번째 Observable과 두 번째 Observable에 subscribeOn을 호출하여 Executor 변환 스케줄러를 지정했다.

만약 newSingleThreadExecutor로 Executors를 생성했다면 실행 결과가 2 개의 스레드가 아니라 1 개의 스레드에서 모두 실행한다.

</br></br>