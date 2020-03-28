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

</br></br>



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



