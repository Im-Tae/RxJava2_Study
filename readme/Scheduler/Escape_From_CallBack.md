### 콜백 지옥 벗어나기

RxJava의 스케줄러를 활용하면 비동기 프로그래밍 방식이 달라진다.

리액티브 프로그래밍은 서버와 연동하는 비동기 프로그래밍을 작성할 대 큰 힘을 발휘한다.

아래는 HTTP 기반의 네트워크 프로그램 예제이다.

</br>



**입력**

```kotlin
import common.Log
import okhttp3.*
import java.io.IOException

class HttpGetExample {
    private val client : OkHttpClient = OkHttpClient()
    private val URL_README = "https://raw.githubusercontent.com/Im-Tae/RxJava2_Study/master/readme/Scheduler/README_TEST.md"

    fun run() {
        val request = Request.Builder()
            .url(URL_README)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                Log.it(response.body().string())
            }
        })
    }
}

fun main() {
    val demo = HttpGetExample()
    demo.run()
}
```

**출력**

```
OkHttp https://raw.githubusercontent.com/Im-Tae/RxJava2_Study/master/readme/Scheduler/README_TEST.md | value = Hello, Leaf!
```



HTTP GET 명령으로 URL_README에 저장된 URL 내용을 가져온다. 성공하면 가져온 내용을 출력하고, 실패하면 printStackTrace 메서드로 메서드 호출 스택을 출력한다.

</br>



아래는 위에 코드를 발전시켜 첫 번째 URL을 호출한 다음, 성공했을 때 다른 서버의 두 번째 URL을 호출하는 코드이다. 즉, 성공했다는 콜백을 받았을 때만 두 번째 URL을 호출해야 한다.</br>



먼저 CommonUtils 클래스에 아래 코드를 추가한다. </br>

```kotlin
class CommonUtils {
    companion object {
        var GITHUB_ROOT = "https://raw.githubusercontent.com/Im-Tae/RxJava2_Study/master/"
    }
}
```

</br>





**입력**

```kotlin
import common.CommonUtils.Companion.GITHUB_ROOT
import common.Log
import okhttp3.*
import java.io.IOException

class CallbackHell {
    private val FIRST_URL = "https://api.github.com/zen"
    private val SECOND_URL = "$GITHUB_ROOT/samples/callback_hell.md"

    private val client : OkHttpClient = OkHttpClient()

    private val onSuccess = object : Callback {

        override fun onFailure(call: Call, e: IOException) = e.printStackTrace()
        override fun onResponse(call: Call, response: Response) = Log.it(response.body().string())
    }

    fun run() {
        val request = Request.Builder()
            .url(FIRST_URL)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) = e.printStackTrace()
            override fun onResponse(call: Call, response: Response) {
                Log.it(response.body().string())

                val request = Request.Builder()
                    .url(SECOND_URL)
                    .build()
                client.newCall(request).enqueue(onSuccess)
            }
        })
    }
}

fun main() {
    val demo = CallbackHell()
    demo.run()
}
```

**출력**

```
OkHttp https://api.github.com/zen | value = Responsive is better than fast.
OkHttp https://raw.githubusercontent.com/Im-Tae/RxJava2_Study/master/samples/callback_hell.md | value = Callback Hell!
```



코드가 굉장히 복잡하다. 첫 번째 HTTP GET 호출은 run 메서드 실행 코드 내부에서 포함 할 수 있다.

하지만 실행 결과를 얻은 후 두 번째 URL을 호출할 때는 지역 변수를 사용할 수 없으므로 객체의 멤버 변수로 선언해야 한다.

즉, 이미 첫 번째 호출의 성공과 실패가 있고 다시 그것을 기준으로 해서 두 번째 호출의 성공과 실패가 있기 때문에 이를 모두 고려해 코드를 작성해야 한다.</br>



아래는 RxJava의 스케줄러를 활용하여 간결하게 작성한 비동기 네트워크 코드이다.

</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import common.OkHttpHelper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class CallbackHeaven {

    private val FIRST_URL = "https://api.github.com/zen"
    private val SECOND_URL = "${CommonUtils.GITHUB_ROOT}/samples/callback_heaven.md"

    fun usingConcat() {
        CommonUtils.start()

        val source = Observable.just(FIRST_URL)
            .subscribeOn(Schedulers.io())
            .map(OkHttpHelper()::get)
            .concatWith(
                Observable.just(SECOND_URL)
                    .map(OkHttpHelper()::get)
            )

        source.subscribe { data -> Log.it(data) }
        CommonUtils.sleep(5000)
    }
}

fun main() {
    val demo = CallbackHeaven()
    demo.usingConcat()
}
```

**출력**

```
RxCachedThreadScheduler-1 | 4309 | value = Design for failure.
RxCachedThreadScheduler-1 | 5326 | value = Callback Heaven!
```



concatWith 함수는 concat 함수와 기능이 동일하다.

단, concat 함수는 첫 번째 Observable과 두 번째 Observable을 함께 인자로 넣어야 하지만 concatWith 함수는 현재의 Observable에 새로운 Observable을 결합할 수 있다는 차이가 있다.

get 메서드 안에 OkHttpClient의 execute 메서드를 호출한다. 

또한, IO 스케줄러로 별도의 스레드에서 네트워크를 호출한다.</br>



동시성, 가독성으로 훨씬 코드가 보기 쉬워 진 것을 볼 수 잇다.

</br>



##### 동시성 네트워크 호출

concat 함수는 첫 번째 Observable에서 데이터 발행이 끝날 때까지 기다려야 한다.

zip 함수를 사용하여 첫 번째 URL과 두 번째 URL 요청을 동시에 수행하고 결과만 결합한다면 첫 번째 URL의 응답을 기다리지 않고 두 번째 URL 호출을 시작할 수 있기 때문에 성능 향상을 기대할 수 있다.

</br>



**입력**

```kotlin
import common.CommonUtils
import common.Log
import common.OkHttpHelper
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class CallHeaven {

    private val FIRST_URL = "https://api.github.com/zen"
    private val SECOND_URL = "${CommonUtils.GITHUB_ROOT}/samples/callback_heaven.md"

    fun usingZip() {
        CommonUtils.start()

        val first = Observable.just(FIRST_URL)
            .subscribeOn(Schedulers.io())
            .map(OkHttpHelper()::get)

        val second = Observable.just(SECOND_URL)
            .subscribeOn(Schedulers.io())
            .map(OkHttpHelper()::get)

        Observable.zip(
            first,
            second,
            BiFunction { a: String, b: String -> "\n>> $a\n>> $b" }
        )
            .subscribe { data -> Log.it(data) }

        CommonUtils.sleep(5000)
    }
}

fun main() {
    val demo = CallHeaven()
    demo.usingZip()
}
```

**출력**

```
RxCachedThreadScheduler-2 | 3506 | value = 
>> It's not fully shipped until it's fast.
>> Callback Heaven!
```



첫 번째 URL 호출은 first 변수, 두 번째 URL 호출은 second 변수에 Observable을 할당했다.

concatWith 함수를 활용한 예제는 5326ms의 실행 시간이 소요되었는데, zip 함수의 예제는 3506ms만 소요되었다.

RxJava의 스케줄러를 활용하면 비즈니스 로직과 비동기 프로그래밍을 분리할 수 있기 때문에 프로그램의 효율을 향상 시킬 수 있다.