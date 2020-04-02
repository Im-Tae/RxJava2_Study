### RxAndroid 기본

RxAndroid의 기본 개념은 RxJava와 동일하다.

RxJava의 구조에 안드로이드의 각 컴포넌트를 사용할 수 있게 변경해 놓은 것이다.</br>



- Observable : 비즈니스 로직을 이용해 데이터를 발행한다.
- 구독자 : Observable에서 발행한 데이터를 구독한다.
- 스케줄러 : 스케줄러를 통해서 Observable, 구독자가 어느 스레드에서 실행될지 결정할 수 있다.

</br>



아래는 위 구성 요소를 간단한 코드로 나타낸 것이다.</br>



```kotlin
// 1. Observable 생성
Observable.create()
	// 2. 구독자 이용
	.subscribe()

	// 3. 스케줄러 이용
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())
```

</br>



Observable과 구독자가 연결되면 스케줄러에서 각 요소가 사용할 스레드를 결정하는 기본적인 구조이다. 

Observable이 실행되는 스레드는 subscribeOn 함수에서 설정하고 처리된 결과를 observeOn 함수에 설정된 스레드로 보내 최종 처리한다.</br>



RxAndroid에서 제공하는 스케줄러는 아래와 같다.

| 스케줄러 이름                  | 설명                                              |
| ------------------------------ | ------------------------------------------------- |
| AndroidSchedulers.mainThread() | 안드로이드의 UI 스레드에서 동작하는 스케줄러이다. |
| HandlerScheduler.from(handler) | 특정 핸들러에 의존하여 동작하는 스케줄러이다.     |

</br></br>



#### Hello world 예제

Observable에서 문자를 입력 받고 텍스트 뷰에 결과를 출력하는 예제이다.</br>



**입력**

```kotlin
class HelloActivity : Activity() {

    private lateinit var mDisposable : Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val observer : DisposableObserver<String> = object: DisposableObserver<String>() {

            override fun onNext(s: String) { textView.text = s }

            override fun onError(e: Throwable) {}

            override fun onComplete() {}
        }

        mDisposable = Observable.create(ObservableOnSubscribe<String> { e ->
            e.onNext("Hello World!")
            e.onComplete()
        }).subscribeWith(observer)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!mDisposable.isDisposed) mDisposable.dispose()
    }
}
```

**XML**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@+id/textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAlignment="center"
        android:textSize="30sp"/>

    <TextView
        android:id="@+id/textView2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAlignment="center"
        android:textSize="30sp"/>

</LinearLayout>
```

</br>



Observable.create로 Observable을 생성해 "Hello world!"를 입력 받고 subscribe 함수 안 onNext 함수에 전달한다. onNext 함수에는 마지막으로 전달된 문자를 텍스트 뷰에 업데이트하는 코드가 있다. 따라서 실제 구독자를 subscribe 함수를 통해 등록하고, 호출하면 "Hello world!"를 텍스트 뷰에 표시한다.



</br>



아래는 위에 코드를 간단하게 표현한 예제이다. </br>



**입력**

```kotlin
class HelloActivityV2 : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Observable.create<String> { s ->
            s.onNext("Hello world!")
            s.onComplete()
        }.subscribe { t -> textView.text = t }
    }
}
```

</br>



아래는 just 함수를 사용한 코드이다.</br>



**입력**

```kotlin
class HelloActivityV2 : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Observable.just("Hello world!")
            .subscribe { t -> textView.text = t }
    }
}
```

</br></br>



#### 제어 흐름

RxJava에서 제공하는 리액티브 연산자를 이용하여 제어 흐름을 구현한 코드이다.

Iterable 객체에서 apple을 찾으면 "apple"을 출력하고, 그렇지 않으면 "Not Found"를 출력한다.

</br>



**입력**

```kotlin
class LoopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loop)

        val samples: Iterable<String> = listOf("banana", "orange", "apple", "apple mango", "melon", "watermelon")

        // kotlin
        btn_loop.setOnClickListener {
            Log.i("log", ">>>>> get an apple :: kotlin")

            for (s in samples) {
                if (s.contains("apple")) {
                    Log.i("log", s)
                    break
                }
            }
        }

        // RxJava 2.x
        btn_loop2.setOnClickListener {
            Log.i("log", ">>>>> get an apple :: RxJava 2.x")

            Observable.fromIterable(samples)
                .filter { s -> s.contains("apple") }
                //.skipWhile { s -> !s.contains("apple") }
                .first("Not found")
                .subscribe { s -> Log.i("log", s) }
        }
    }
}
```

**XML**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@+id/tv_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginEnd="5dp"
        android:layout_marginStart="5dp"/>

    <Button
        android:id="@+id/btn_loop"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Loop::first::apple"/>

    <Button
        android:id="@+id/btn_loop2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="RxLoop 2"/>
</LinearLayout>
```

**출력**

```
...I/log: >>>>> get an apple :: kotlin
...I/log: apple
...I/log: >>>>> get an apple :: RxJava 2.x
...I/log: apple
```



버튼을 클릭한 후 Log를 살펴보면 위와 같은 출력 결과가 나온다.</br>



코드에서 주목해야 하는 부분은 Observable을 이용해 데이터를 발행하고 처리하는 기능에만 집중할 수 있게 된다는 것이다.

자바의 for문을 filter와 first로 대체한 것을 볼 수 있다.

</br></br>



#### RxLifecycle 라이브러리

RxAndroid에는 [RxLifecycle](https://github.com/trello/RxLifecycle)이라는 라이브러리를 제공한다.

액티비티와 프래그먼트의 라이프 사이클을 RxJava에서 사용할 수 있게 한다.

구독할 때 발생할 수 있는 메모리 누수를 방지하기 위해 사용한다.

완료하지 못한 구독을 자동으로 해제한다.</br>



아래는 RxLifecycle의 컴포넌트이다.

| 컴포넌트                  | 설명                                              |
| ------------------------- | ------------------------------------------------- |
| RxActivity                | 액티비티에 대응                                   |
| RxDialogFragment          | Native/Support 라이브러리인 DialogFragment에 대응 |
| RxFragment                | Native/Support 라이브러리인 Fragment에 대응       |
| RxPreferenceFragment      | PreferenceFragment에 대응                         |
| RxAppCompatAcitivty       | Support 라이브러리 AppCompatActivity에 대응       |
| RxAppCompatDialogFragment | Support 라이브러리 AppCompatDialogFragment에 대응 |
| RxFragmentActivity        | Support 라이브러리 FragmentActivity에 대응        |

</br>



build.gradle에 아래 코드를 추가한다.

```kotlin
implementation 'com.trello.rxlifecycle3:rxlifecycle:3.1.0'
implementation 'com.trello.rxlifecycle3:rxlifecycle-android:3.1.0'
implementation 'com.trello.rxlifecycle3:rxlifecycle-components:3.1.0'
```

</br>



아래는 RxLifecycle 라이브러리 활용 코드이다. </br>



**입력**

```kotlin
class HelloActivityV3 : RxAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Observable.just("Hello, rx world!")
            .compose(bindToLifecycle())
            .subscribe { s -> textView.text = s }
    }
}
```



RxCompatActivity 클래스 대신 RxAppCompatActivity 클래스를 상속하도록 바꿨고, Observable 생성 부분에서는 compse 함수로 라이프 사이클을 관리하도록 추가했다.

Observable은 HelloActivityV3 클래스가 종료되면 자동으로 해제된다.

</br></br>



#### UI 이벤트 처리

안드로이드는 사용자가 애플리케이션과 상호 작용할 때 발생하는 특정 View 객체의 이벤트를 얻는 방법을 제공한다. 따라서 View 클래스 안에는 UI 이벤트를 처리하기 위한 몇 가지 콜백 메서드가 있다.

</br>



##### 이벤트 리스너

콜백 메서드 하나를 포함하는 뷰 클래스 안의 인터페이스를 뜻한다.

리스너가 등록된 뷰 UI 안의 아이템과 사용자 사이에 상호 작용이 발생할 때 안드로이드 프레임워크가 호출한다.</br>

| 콜백 메서드 이름      | 설명 |
| --------------------- | ---- |
| onClick()             |      |
| onLongClick()         |      |
| inFocusChange()       |      |
| onKey()               |      |
| onTouch()             |      |
| onCreateContextMenu() |      |

</br>



아래는 onClick 메서드에 Observable을 활용한 코드이다.</br>



**입력**

```kotlin
class OnClickFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_on_click, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getClickEventObservable()
            .map { _ -> "clicked" }
            .subscribe(getObserver())
    }

    private fun getClickEventObservable() : Observable<View> {
        return Observable.create { e -> btn_click_observer.setOnClickListener(e::onNext) }
    }
    
    private fun getObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {

            override fun onNext(s: String) { Log.i("log", s) }

            override fun onError(e: Throwable) { Log.i("log", e.message.toString()) }

            override fun onComplete() { Log.i("log", "complete") }
        }
    }
}

```

**XML**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <Button
        android:id="@+id/btn_click_observer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Normal"/>

    <Button
        android:id="@+id/btn_click_observer_extra"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="7"/>
</LinearLayout>
```

**출력**

```
...I/log: clicked
```



동일한 동작을 하는 세 가지 Observable을 작성했다. btn_click_observer을 클릭하면 e::onNext를 호출한다. 

클릭한 아이템이 있는 View 정보를 전달하면 map 함수는 "clicked"라는 String 값으로 변경한다. 

그럼 리턴 값이 Observable<View>에서 Observable<String>으로 변경되고 옵서버는 "clicked"를 출력한다.

</br>



아래는 숫자를 확인해 주는 예제 코드이다.</br>

**입력**

```kotlin
class OnClickFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_on_click, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getClickEventObservableExtra()
            .map { _ -> 7 }
            .flatMap(this::compareNumbers)
            .subscribe { s -> Log.i("log", s) }
    }

    private fun getClickEventObservableExtra(): Observable<View> {
        return Observable.create { s -> btn_click_observer_extra.setOnClickListener(s::onNext) }
    }

    private fun compareNumbers(input : Int): Observable<String> {
        return Observable.just(input)
            .flatMap { i ->
                val data = (1..10).random()
                return@flatMap Observable.just("local : $i", "remote : $data", "result = ", (i == data).toString())
            }
    }
```

**출력**

```
...I/log: local : 7
...I/log: remote : 5
...I/log: result = 
...I/log: false
...I/log: local : 7
...I/log: remote : 7
...I/log: result = 
...I/log: true

```



map 함수에 있는 숫자를 flatMap 함수로 전달한다. 또 다른 Observable은 해당 숫자와 새로 만든 숫자를 비교하여 3 개의 아이템으로 결과를 전달한다.

</br>



##### 간단히 액티비티 중복 실행 문제 해결하기

현업에서 자주 발생하는 액티비티 중복 실행 문제를 간단히 해결할 수 있는 예제이다.</br>



**입력**

```kotlin
class DebounceFragment : Fragment() {

    lateinit var mDisposable : Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_debounce, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDisposable = getObservable()
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { s ->
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
                val time = sdf.format(Calendar.getInstance().time)
                tv_display.text = "Clicked : ${time.toString()}"
            }
    }

    private fun getObservable() : Observable<View> {
        return Observable.create { e -> btn_debounce.setOnClickListener(e::onNext) }
    }

}
```

**XML**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAlignment="center"
        android:text="300ms 내에 입력되는 button click은 스킵한다."
        android:textSize="30sp"/>

    <Button
        android:text="Debounce"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/btn_debounce"/>

    <TextView
        android:id="@+id/tv_display"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAlignment="center"
        android:textSize="30sp"/>

</LinearLayout>
```



빠른 시간 안에 액티비티의 실행을 다시 요청하면 singleTop의 경우에도 액티비티가 중복 실행된다.

RxAndroid에서는 위에서 사용한 debounce 함수를 통해 이 문제를 쉽게 해결할 수 있다.

</br>



##### 추천 검색어 기능 구현 예제

TextChangeListener를 이용하여 검색할 키워드를 입력하고 500ms 안에 다른 문자를 입력하지 않으면 검색을 시작하는 예제이다.</br>



**입력**

```kotlin
class DebounceSearchFragment : Fragment() {

    lateinit var mDisposable : Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_debounce_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDisposable = getObservable()
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { s -> !TextUtils.isEmpty(s) }
            .subscribeOn(Schedulers.newThread())
            .subscribeWith(getObserver())
    }

    private fun getObservable() : Observable<CharSequence> {
        return Observable.create { e -> dsf_input_deb_search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = e.onNext(s.toString())

            override fun afterTextChanged(s: Editable) {}

        }) }
    }

    private fun getObserver() : DisposableObserver<CharSequence> {
        return object : DisposableObserver<CharSequence>() {

            override fun onNext(word: CharSequence) { Log.i("log", word.toString()) }

            override fun onError(e: Throwable) {}

            override fun onComplete() {}

        }
    }
}

```

**XML**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <EditText
        android:id="@+id/dsf_input_deb_search"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter search text"/>

</LinearLayout>
```



onActivityCreated 함수에서는 debounce 함수와 EditText 클래스의 리스너 이벤트를 Observable에 연결하는 부분을 주의 깊게 살펴봐야 한다.

getObservable 함수에서는 EditText의 객체인 dsf_input_deb_search가 사용자 입력 문자열의 상태 변경을 알 수 있도록 해야 한다. 따라서 addTextChangedListener 메서드의 인자로 TextWatcher 인터페이스의 객체를 전달한다.</br>



TextWatcher는 beforeTextChanged, onTextChanged, afterTextChanged의 3 개 메서드를 제공한다.

여기에서는 onTextChanged 메서드를 사용하여 EditText의 입력 내용을 확인한다.</br>



onTextChanged의 인자를 간단히 설명하면 아래와 같다.

```kotlin
override fun onTextChanged(
    // 사용자가 새로 입력한 문자열을 포함하는 EditText 객체의 문자열
    s: CharSequence, 
    
    // 새로 추가된 문자열의 시작 위치값
    start: Int, 
    
    // 새 문자열 대신 삭제된 기존 문자열의 수
    before: Int, 
    
    // 새로 추가된 문자열의 수
    count: Int
)
```



사용자가 문자를 입력했을 때 TextWatcher 객체의 onTextChanged 메서드가 호출된다.

getObservable의 발행자는 onNext 함수로 변경된 문자열을 입력 받게 되고 debounce 에서 filter 순서로 리액티브 연산자를 처리하게 된다. 최종 처리된 문자열을 구독자에게 전달한다.