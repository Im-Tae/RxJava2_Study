### 메모리 누수

메모리 누수란 보통 참조가 완료되었지만 할당한 메모리를 해제하지 않아서 발생한다. 특히 강한 참조의 경우 가비지 컬렉터가 메모리에서 객체를 제거할 수 없으므로 라이프 사이클에 맞게 객체 참조를 끊어야 사용하지 않는 메모리를 해제할 수 있다. 메모리 누수는 시스템 전체 성능에 영향을 미치므로 중요하게 관리해야 한다.</br>



```kotlin
class HelloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val observer = object : DisposableObserver<String>() {
            override fun onNext(s: String) {
                textView.text = s
            }

            override fun onError(e: Throwable) {}

            override fun onComplete() {}
        }

        Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext("hello world!")
            emitter.onComplete()
        }).subscribe(observer)
    }
}
```



정상 동작하여 문제가 없어 보이지만 메모리 누수가 발생하는 코드이다. 

Observable은 안드로이드의 컨텍스트를 복사하여 유지한다. onComplete, onError 함수가 호출되면 내부에서 자동으로 unsubscribe 함수를 호출한다.

그런데 구독자가 텍스트 뷰를 참조하기 때문에 액티비티가 비정상적으로 종료되면 텍스트 뷰가 참조하는 액티비티는 종료해도 가비지 컬렉션의 대상이 되지 못한다. 따라서 메모리 누수가 발생한다.



따라서 안드로이드와 subscribe 함수의 라이프 사이클을 고려하여 잘 설계해야 한다.



아래는 메모리 누수를 해결하는 방법들 이다.

</br></br>



##### Disposable 인터페이스를 이용하여 명시적으로 자원 해제하기

onCreate 메서드에서 subscribe 함수를 호출하면 onDestory 메서드에서 메모리 참조를 해제하고, onResume 메서드에서 subscribe 함수를 호출하면 onPause 메서드에서 메모리 참조를 해제해야 한다. 이 해결책을 라이프 사이클에 맞게 설정해 준다.

</br>



**입력**

```kotlin
class HelloActivity : AppCompatActivity() {

    lateinit var mDisposable : Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val observer = object : DisposableObserver<String>() {
            override fun onNext(s: String) {
                textView.text = s
            }

            override fun onError(e: Throwable) {}

            override fun onComplete() {}
        }

        mDisposable = Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext("hello world!")
            emitter.onComplete()
        }).subscribeWith(observer)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.dispose()
    }
}
```



RxJava1에서 쓰이던 Subscription 인터페이스가 RxJava2에서 Disposable 인터페이스로 변경되었다.

그리고 기존의 Publisher.subscribe는 void를 반환하므로 subscribeWith 함수를 이용하여 구독자에게 그대로 리턴 해주어야 한다.

</br></br>



##### RxLifecycle 라이브러리 이용

액티비티의 부모를 RxAppCompatActivity으로 변경하고 compose 함수를 사용하여 RxLifecycle 라이브러리를 적용할 수 있다. </br>



**입력**

```kotlin
class HelloRxAppActivity : RxAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext("Hello world!")
            emitter.onComplete()
        })
            //.compose(bindToLifecycle())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe { s -> textView.text = s }
    }
}
```



RxLifecycle 라이브러리를 적용한 코드이다. Disposable 인터페이스를 사용할 때보다 코드가 더 단순하게 변경되었다.

아래의 흐름으로 동작한다.



1. RxLifecycle를 사용하기 위해 RxAppCompatActivity 클래스를 상속한다.
2. compose 함수를 이용하여 RxLifecycle 라이브러리를 설정한다. 설정하는 방법은 Observable 자체를 안드로이드 액티비티에 바인딩하는 방법과 액티비티의 특정 콜백 메서드에 바인딩하는 방법이 있다. bindToLifecycle 함수를 사용하게 되면 onCreate - onDestory 메서드와 onResume - onPause 메서드가 쌍으로 동작한다. 즉, onCreate 메서드에서 subscribe 함수를 호출하면 onDestory 메서드에서 unsubscirbe 함수를 호출한다.
3. 종료되는  시점은 직접 bindUtilEvent 함수를 선언하여 바꿀 수 있다. 종류는 아래와 같다.

```java
public enum ActivityEvent {
    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY;
}
```

4. onDestory 메서드에서는 더 이상 dispose 함수가 필요 없으므로 삭제한다.

</br></br>



##### CompositeDisposable 클래스 이용

CompositeDisposable 클래스를 이용하면 생성된 모든 Observable을 안드로이드 라이프 사이클에 맞춰 한 번에 모두 해제할 수 있다.

</br>



**입력**

```kotlin
class HelloActivityComposite : AppCompatActivity() {

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val disposable = Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext("hello world!")
            emitter.onComplete()
        }).subscribe { s -> textView.text = s }

        mCompositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.dispose()
    }
}
```



**Disposable 인터페이스를 이용하여 명시적으로 자원 해제하기**에서 DisposableObserver 객체를 직접 해지한다면 위 방법은 Publisher.subscribe 함수를 이용하여 Disposable를 리턴한 후 CompositeDisposable 클래스에서 일괄 관리한다.



Publisher.subscribe는 void를 리턴하므로 새로운 subscribeWith 함수를 사용하거나 인자에 구독자가 아닌 소비자를 전달해서 Disposable 객체를 리턴 받아야 한다.



clear와 dispose 함수 모두 등록된 Disposable 객체를 삭제한다는 점은 같다. 그러나 clear 함수의 경우 계속 Disposable 객체를 받을 수 있지만, dispose 함수의 경우 isDisposed 함수를 true로 설정하여 새로운 Disposable 객체를 받을 수 없다.

</br></br>



