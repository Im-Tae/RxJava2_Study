### RxAndroid 활용

#### 리액티브 RecyclerView

RecyclerView 클래스에는 서브 클래스인 LayoutManager가 있다. 이를 이용하여 뷰를 정의하고, Adapter 클래스를 이용하여 데이터 세트에 맞는 ViewHolder 클래스를 구현할 수 있다. 

이외에도 RecyclerView 클래스는 뷰를 제어하는 ItemDecoration, ItemAnimation이라는 서브 클래스를 둔다. Adapter 클래스와 상호 연결되며, ViewHolder 클래스에서 데이터와 뷰를 받아 이를 재 사용할 수 있게 한다. </br>



**build.gradle**

```kotlin
dependencies {
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support:recyclerview-v7:28.0.0'
    ...
}
```

</br>



**RecyclerView 클래스와 함께 사용하는 주요 클래스**

| 클래스 이름    | 설명                                                   |
| -------------- | ------------------------------------------------------ |
| Adapter        | 데이터 세트의 아이템을 나타내는 뷰를 생성한다.         |
| ViewHolder     | 재활용 뷰에 대한 모든 서브 뷰를 저장한다.              |
| LayoutManager  | 뷰에 있는 아이템을 배치하고 관리한다.                  |
| ItemDecoration | 아이템을 꾸미는 서브 뷰를 제어한다.                    |
| ItemAnimation  | 아이템을 추가, 정렬, 제거할 때 애니메이션 효과를 준다. |

</br>



##### Adapter 클래스

Adapter 클래스는 ViewHolder 클래스를 이용한 데이터 세트의 정의에 따라 UI를 선택해 보여준다.

아래 세 가지의 메서드를 구현 해주어야 한다. </br>



- **onCreateViewHoler** : ViewHolder를 생성하고 뷰를 붙여주는 부분이다.
- **onBindViewHolder** : 재활용하는 뷰를 호출하여 실행하는 메서드이다. 뷰 홀더를 전달하고 어댑터는 position 인자의 데이터를 결합한다.
- **getItemCount** : 데이터의 개수를 반환한다.

</br>



##### LayoutManager 클래스

LayoutManager 클래스는 뷰를 그리는 방법을 정의한다.



- **LinearLayoutManager** : 가로 또는 세로 형태의 리스트로 나타낼 때 사용
- **GridLayoutManager** : 그리드 형식으로 항목을 표시할 때 사용
- **StaggeredGridLayoutManager** : 지그재그 그리드 형태로 항목을 표시할 때 사용

</br>



또한 RecyclerView.ItemAnimator를 상속하여 setItemAnimator() 메서드를 오버라이딩하여 직업 애니메이션 효과를 정의할 수 있다.

</br>



##### 설치된 앱 리스트 나열하기 예제



**fragment_recycler_view**

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    tools:context=".MainActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/recycler_view"
        android:scrollbars="vertical"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"/>

</RelativeLayout>
```

</br>



**recycler_view_item**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:layout_marginTop="5dp"
    android:layout_marginBottom="5dp">

    <ImageView
        android:id="@+id/item_image"
        android:layout_width="50dp"
        android:layout_height="50dp"/>

    <TextView
        android:id="@+id/item_title"
        android:paddingStart="5dp"
        android:paddingLeft="5dp"
        android:gravity="center_vertical"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
</LinearLayout>

```

</br>



**RecyclerItem 데이터 클래스**

```kotlin
data class RecyclerItem(var image: Drawable, var title: String)
```



리스트에 표시할 이미지와 타이틀로 구성된 데이터 클래스이다.

</br>



**RecyclerViewAdapter**

```kotlin
class RecyclerViewAdapter(private var mItems: ArrayList<RecyclerItem>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var mPublishSubject : PublishSubject<RecyclerItem> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mItems[position]

        holder.mImage?.setImageDrawable(item.image)
        holder.mTitle?.text = item.title
        holder.getClickObservable(item).subscribe(mPublishSubject)
    }

    override fun getItemCount(): Int = mItems.size

    fun updateItems(items: ArrayList<RecyclerItem>) = mItems.addAll(items)

    fun updateItems(item: RecyclerItem) = mItems.add(item)

    fun getItemPublishSubject() : PublishSubject<RecyclerItem> = mPublishSubject

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val mImage = itemView?.findViewById<ImageView>(R.id.item_image)
        val mTitle = itemView?.findViewById<TextView>(R.id.item_title)

        fun getClickObservable(item: RecyclerItem) : Observable<RecyclerItem> {
            return Observable.create { e -> itemView.setOnClickListener { e.onNext(item) } }
        }
    }
}
```



먼저 ViewHolder 부분을 살펴보면, inner class로 선언을 했고 생성자의 인자로 itemView를 통해 mImage, mTitle의 로컬 변수를 바인딩해준다.

생성자에 Click 리스너 이벤트를 넣어 주는 게 일반적이지만, 리액티브 프로그래밍에서는 Click 이벤트를 분리된 Observable에 생성한다.

</br>



Adapter 클래스는 onCreateViewHolder, onBindViewHolder, getItemCount라는 3 개의 메서드를 구현해 주어야 한다. 구현 내용은 아래와 같다.

</br>

- onCreateViewHolder 메서드를 이용해 직접 정의한 ViewHolder 객체를 리턴 한다. 이 메서드는 viewType에 따라 최초 1회만 호출된다.
- ViewHolder 객체가 생성되면 onBindViewHolder 메서드에서 holder 인자의 뷰 아이템에 값을 넣어준다. 이후로 mItems에 저장된 배열 요소 개수만큼 메서드가 호출된다.
- Adapter 클래스는 원하는 데이터 세트를 직접 구현해야 한다. 정의가 끝나면 mItems에 저장된 배열 요소 개수를 리턴하는 getItemCount를 구현해 준다.
- RecycleViewFragment 클래스의 객체에서 데이터 세트를 전달 받을 updateItems를 구현한다.
- 어떤 아이템을 클릭했을 때의 이벤트를 RecycleViewFragment 객체로 전달할 PublishSubject 객체도 선언한다. 구독자가 없더라도 실시간 처리되어야 하는 Click 이벤트의 특성 때문에 PublishSubject 객체를 사용한다.

</br>



**RecyclerViewFragment**

```kotlin
class RecyclerViewFragment : Fragment() {

    private var mItems: ArrayList<RecyclerItem> = arrayListOf()
    private lateinit var mRecyclerViewAdapter : RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mRecyclerViewAdapter = RecyclerViewAdapter(mItems)
        recycler_view.adapter = mRecyclerViewAdapter
        mRecyclerViewAdapter.getItemPublishSubject().subscribe { s -> Toast.makeText(context, s.title, Toast.LENGTH_SHORT).show() }
    }

    override fun onStart() {
        super.onStart()

        getItemObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { item ->
                mRecyclerViewAdapter.updateItems(item)
                mRecyclerViewAdapter.notifyDataSetChanged()
            }
    }

    private fun getItemObservable(): Observable<RecyclerItem> {
        val pm = activity!!.packageManager
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        return Observable.fromIterable(pm.queryIntentActivities(i, 0))
            .sorted(ResolveInfo.DisplayNameComparator(pm))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { item ->
                val image = item.activityInfo.loadIcon(pm)
                val title  = item.activityInfo.loadLabel(pm).toString()

                return@map RecyclerItem(image, title)
            }
    }

}
```



getItemObservable 함수는 packageManager 클래스를 이용하여 설치된 앱 정보를 가져와 RecyclerItem 객체로 변경하는 간단한 함수이다.

queryIntentActivities는 설치된 앱 중 CATEGORY_LAUNCHER 타입의 앱만 결과로 가져오게 된다. 가져온 결과는 앱 이름으로 정렬하고, 이미지와 타이틀을 추출하여 RecyclerItem 객체를 생성한다.

</br>

클래스에서 처리하는 부분은 두 가지이다. RecyclerView를 생성하는 부분과 리스트를 클릭하면 toastMessage를 생성하는 부분이다.

</br>

- onActivityCreated 메서드에서는 Adapter 객체를 생성하여 리사이클러 뷰에서 사용할 수 있도록 설정한다. 
- onStart 메서드가 호출되면 설치된 애플리케이션의 정보가 RecyclerViewAdapter 객체에 업데이트되고 리스트에 출력된다.
- 설치한 앱의 정보를 이용하여 아이콘과 이름으로 리스트 아이템을 구성하고, 리스트를 클릭하면 앱의 이름을 toast Message로 보여준다.



</br></br>

#### 안드로이드 스레드를 대체하는 RxAndroid

안드로이드 기본적으로 싱글 스레드 모델이다. 그래서 처리하는 데 오래 걸리는 데이터 전송이나 파일 입출력 등은 별도의 스레드로 분리하여 작업해야 한다. 이 부분을 고려하지 않고 앱을 개발하면 성능이 나빠지거나 애플리케이션이 응답하지 않는 현상이 발생하기도 한다.

스레드를 효과적으로 관리하려면 스케줄러를 만들어 관리해야 한다.

</br>



##### 뷰와 뷰 그룹의 스레드 관리

안드로이드의 뷰나 뷰 그룹은 UI 스레드에서만 업데이트할 수 있게 설계되어 있다. 여러 스레드에서 동시에 UI를 업데이트할 때 발생할 수 있는 동기화 문제를 예방하기 위함이다.

Looper와 Handler 클래스를 통해 일반 스레드에서 작업한 결과를 뷰에 업데이트할 수 있다.



아래는 Looper와 Handler를 이용한 UI 스레드와의 통신 원리를 나타낸 것이다.

</br>



<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/UIThread.png?raw=true" width = "580" height = "340"  /> </br>



스레드는 Handler 클래스를 이용해 Message를 Message Queue에 넣는다.

UI 스레드는 Looper 클래스를 이용해 Message Queue에 접근하여 다른 스레드에서 보내는 데이터를 처리할 수 있다. 즉, 개별 스레드에서 UI 스레드와 통신할 방법은 Handler를 통해 Message를 보내고 UI 스레드는 Looper를 이용해 해당 Message를 꺼내서 사용하는 구조이다.

좀 더 구조 설명을 하자면 스레드가 Handler 객체를 생성하는 것이 아니라 Handler 객체가 스레드를 생성하는 것이다. 그리고 Handler 객체는 실행되는 스레드와 스레드에 있는 Message Queue에 종속된다.

안드로이드는 스레드 관리를 위해 Handler 클래스를 좀 더 사용하기 쉽게 래핑한 HandlerThread 클래스, 그리고 RxAndroid를 도입한 AsyncTask 클래스를 제공한다.

</br></br>



##### AsyncTask 클래스에 RxAndroid 적용하기

안드로이드에서 제공하는 추상 클래스로 안드로이드에서 사용하는 대표적인 스레드 중 하나이다.

별도의 Handler 클래스나 스레드 사용 없이 UI 스레드에서 백그라운드 작업을 수행하고 결과를 바로 뷰 화면에 업데이트할 수 있다.

아래는 AsyncTask 클래스를 활용한 예제이다.</br>



**입력**

```kotlin
private lateinit var myAsyncTask : MyAsyncTask

private fun initAndroidAsync() {
    myAsyncTask = MyAsyncTask()
    myAsyncTask.execute("Hello", "async", "world")
}

inner class MyAsyncTask : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String?): String {
        val word = StringBuilder()

        for (s in params) {
            word.append(s).append(" ")
        }

        return word.toString()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        textView.text = result
    }
}
```



AsyncTaskActivity 클래스의 initAndroidAsync 메서드에서는 AsyncTask<String, Void, String>를 상속한 MyAsyncTask 객체를 생성하고 execute 메서드를 호출한다. execute 메서드를 호출하면 MyAsyncTask 클래스의 doInBackground를 호출하여 "Hello", "async", "world"를 전달한다.

</br>



AsyncTask 클래스는 UI 스레드가 아닌 싱글 워커 스레드에서 동작하며 publishProgress를 이용하여 실행 중간 중간 화면을 바로 갱신한다는 장점이 있다. 

위에 코드는 실행 중간 화면을 업데이트하지 않고 모든 결과를 처리한 후 마지막에 onPostExecute 메서드를 호출하여 "Hello async world"를 텍스트 뷰에 표시한다.

안드로이드는 화면을 업데이트하기 위해 UI 스레드를 이용한다. AsyncTask 클래스는 위에서 설명한 스레드나 메시지 루프의 원리를 이해하지 못하더라도 백그라운드에서 처리한 결과를 바로 스레드에 업데이트 할 수 있는 편리한 기능을 제공한다. 또한 THREAD_POOL_EXECUTOR를 이용하여 병렬로 처리할 수 있다는 장점도 있다.

하지만 아래와 같이 단점도 있으므로 주의해야 한다.



- 오직 한 번만 실행되고 재사용이 불가능하다.
- 액티비티 종료를 명시해야만 종료되므로 메모리 누수가 발생한다.
- AsyncTask 클래스는 항상 UI 스레드 위에서 불러와야 한다.

</br>



아래는 AsyncTask를 이용하여 구현한 내용을 RxAndroid를 이용하여 구현한 코드이다.</br>



**입력**

```kotlin
private fun initRxAsync() {
    Observable.just("Hello", "rx", "world")
        .reduce { x: String, y: String -> "$x $y" }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(getObserver())
}

private fun getObserver() : MaybeObserver<String> {
    return object : MaybeObserver<String> {

        override fun onSubscribe(d: Disposable) {}

        override fun onSuccess(result: String) {
            textView2.text = result
        }

        override fun onError(e: Throwable) {
            Log.e("log", e.message.toString())
        }

        override fun onComplete() {
            Log.i("log", "done")
        }
    }
}
```



just 함수를 이용하여 "hello", "rx", "world"라는 데이터를 발행한다.

reduce 함수는 StringBuilder 클래스의 append 메서드의 역할을 한다.

다음으로는 observeOn(AndroidSchedulers.mainThread())를 이용하여 구독자가 실행될 스레드를 안드로이드의 UI 스레드로 지정한다. 에러 없이 정상적으로 텍스트 뷰에 "Hello rx world"가 출력 된다. 이처럼 간단한 설정 만으로 구독자나 Observable로 데이터를 발행할 스레드를 설정할 수 있다.

</br>

위에 코드에서 람다 표현식을 이용하는 방법과 구독자를 직접 구현하는 방법을 모두 적용했다. 중요한 것은 코드 실행 중 발생하는 모든 에러는 구독자의 onError 함수에서 처리할 수 있어야 한다는 것이다. 람다 표현식으로 간소화 하는 것도 좋지만 가능하면 모든 메서드를 구현하여 상황에 맞게 직접 처리 해주는 방식이 좋다.

</br>

RxAndroid는 강력한 스케줄러 기능이 있다. 어렵지 않게 파일 다운로드나 입출력 등 처리 시간이 오래 걸리는 작업을 간단히 대체할 수 있다.



| AsyncTask        | RxAndroid                                    |
| ---------------- | -------------------------------------------- |
| execute()        | subscribe()                                  |
| doInBackground() | 리액티브 연산자와 함께 사용하는 onSubcribe() |
| onPostExecuted() | observer                                     |

</br></br>



##### RxAndroid를 이용하여 TimerTask 대체하기

안드로이드에서는 주기적으로 실행하는 동작을 구현하는 여러 가지 방법이 있지만 보통 Timer 클래스나 Handler 클래스를 이용한다.

Timer 클래스는 schedule 메서드를 이용하여 지연 시간을 설정하거나, 특정 시간에 어떤 동작을 실행할 수도 있으며, 고정된 시간을 설정해 동작을 반복 실행할 수 있다. Handler 클래스는 postDelayed 메서드를 이용하여 지연 시간 설정이나 반복 실행을 구현할 수 있다.



아래는 Timer 클래스를 사용한 예제 코드이다.</br>



**입력**

```kotlin
private val DELAY = 0L
private val PERIOD = 1000L
private val mTimer = Timer()

private fun timerStart() {
    var count = 0
    mTimer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            textView.text = count++.toString()
        }
    }, DELAY, PERIOD)
}

private fun timerStop() = mTimer.cancel()
```



Timer 클래스를 이용하여 1 초마다 한 번씩 run 메서드를 호출하는 코드이다.

DELAY는 첫 실행 시 지연 시간을 선언하는 부분이다. 1000ms 가 1 초이다.



아래는 Timer 클래스에서 사용할 수 있는 메서드이다.</br>



| 메서드 이름                                                  | 설명                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| schedule(TimerTask task, long delay, long period)            | 설정된 지연 시간 후 주기적으로 실행                          |
| schedule(TimerTask task, Date time)                          | 지정된 시간에 한번 실행                                      |
| schedule(TimerTask task, Date firstTime, long period)        | 지정된 시간에 실행을 시작한 후 주기적으로 실행               |
| schedule(TimerTask task, long delay)                         | 설정된 지연 시간 후 한번 실행                                |
| scheduleAtFixedRate(TimerTask task, long delay, long period) | 설정된 지연 시간 후 고정된 간격으로 주기적으로 실행          |
| scheduleAtFixedRate(TimerTask task, Date firstTime, long period) | 지정된 시간에 실행을 시작한 후 고정된 간격으로 주기적으로 실행 |

</br>

메서드 이름에 Fixed 라는 단어가 없다면 Timer 클래스 실행 주기에 오차가 발생한다. 예를 들어 30 초 간격으로 ping을 보내야 하는 시스템이라면 schedule 메서드보다는 scheduleAtFixedRate 메서드를 사용해야 한다. schedule 메서드는 오차가 발생할 수 있지만 scheduleAtFixedRate 메서드는 오차가 발생하지 않는다. </br>



아래는 실행 횟수 제한을 위해 CountDownTimer 클래스를 사용한 예제이다.</br>



**입력**

```kotlin
private var count = 0

private val MILLISINFUTURE = 11 * 1000L
    private val COUNT_DOWN_INTERVAL = 1000L
    lateinit var mCountDownTimer : CountDownTimer

private fun initCountDownTask() {
    mCountDownTimer = object : CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {

        override fun onTick(millisUntilFinished: Long) {
            textView.text = count--.toString()
        }

        override fun onFinish() {
            textView.text = "Finish ."
        }
    }
}

private fun countDownTimerStart() {
    count = 10
    mCountDownTimer.start()
}

private fun countDownTimerStop() = mCountDownTimer.cancel()
```



11 초 동안 1 초에 한 번씩 onTick 메서드를 호출한다. 완료할 때는 onFinish 메서드가 호출된다.

</br>



아래는 Handler 클래스를 이용하여 Timer 클래스를 대체한 예제이다.</br>

**입력**

```kotlin
private var count = 0

private lateinit var mHandler : Handler

private val timer = object : Runnable {
    override fun run() {
        textView.text = count++.toString()
        mHandler.postDelayed(this, 1000)
    }
}

private fun initHandler() { mHandler = Handler() }

private fun handlerStart() {
    count = 0
    mHandler.postDelayed(timer, 0)
}
```



Handler 클래스로 Timer 클래스를 대체하는 실제 동작은 Runnable 객체 안  1 초에 한 번씩 Handler를 호출하는 부분에서 이루어진다. 처음 실행할 때 지연 시간을 설정하고 싶은 경우에는 handlerStart 메서드에서 지연 시간을 설정하면 된다.</br>



아래는 RxAndroid를 이용하여 Timer 클래스를 대체하는 예제이다.</br>

**입력**

```kotlin
private fun startPollingV1() {
    val ob = Observable.interval(3, TimeUnit.SECONDS)
        .flatMap { o -> Observable.just("polling #1 $o") }

    ob.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { s -> Log.i("log", s) }
}
```

**출력**

```
polling #1 0
polling #1 1
polling #1 2
polling #1 3
polling #1 4
...
```



위에 코드의 동작은 아래와 같다.



1. interval 함수를 이용하여 3 초마다 정수를 발생하는 Observable을 생성한다.
2. 생성한 Observable은 flatMap 함수에서 "polling #1"과 발행한 정수를 결합한 새로운 Observable로 변경한다.
3. subscribeOn과 observerOn 함수를 이용하여 실행할 스레드를 설정한다. 결과를 화면에 바로 업데이트해야 하므로 결과를 표시하는 observerOn은 mainThread로 설정한다.

</br>



아래는 위에 코드를 repeatWhen과 delay 함수를 사용하여 만든 것이다.</br>



**입력**

```kotlin
private fun startPollingV2() {
    val ob2 = Observable.just("polling #2")
        .repeatWhen { o -> o.delay(3, TimeUnit.SECONDS) }

    ob2.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { s -> Log.i("log", s) }
}
```

**출력**

```
polling #2
polling #2
polling #2
polling #2
polling #2
polling #2
...
```



위에 코드의 동작은 아래와 같다.



1. just 함수를 이용하여 문자를 발행하는 Observable을 생성한다.
2. repeatWhen 함수를 이용하여 동일한 Observable 객체를 계속 발행하게 설정한다.
3. repeatWhen 함수는 입력된 Observable delay 함수를 이용하여 3 초의 지연을 설정한다.
4. subscribeOn과 observerOn 함수를 이용하여 실행할 스레드를 설정한다. 결과를 화면에 바로 업데이트해야 하므로 결과를 표시하는 observerOn은 mainThread로 설정한다.

</br>



3초 지연 시간 설정은 delay가 반복 실행은 repeatWhen이 담당하는 구조이다.

</br></br>



##### AndroidSchedulers 클래스

RxAndroid에서 추가되는 스케줄러 함수는 두 가지로 하나는 mainThread, 다른 하나는 from 이다.

mainThread는 스케줄러 내부에서 직접 MainLooper에 바인딩 하며, from은 개발자가 임의의 Looper 객체를 설정할 수 있다.

AndroidSchedulers.mainThread() 은 AndroidSchedulers.from(Looper.getMainLooper())와 동일하다.



스레드 사이에서 통신하기 위해서는 Handler 클래스가 필요하다.

기본적으로 RxAndroid에서도 Handler 클래스를 이용하여 스레드와 통신하는 구조이며, UI 스레드와 통신을 위해서 MainLooper를 이용하여 스레드 안에서 핸들러를 생성한다.

</br></br>



#### REST API를 활용한 네트워크 프로그래밍

REST는 네트워크 아키텍처 원리의 모음으로 자원을 정의하고 자원에 대한 주소를 지정하는 방법 전반을 말한다. 웹 상의 자료를 HTTP 위에서 SOAP나 쿠키를 통한 세션 트랙킹 같은 별도의 전송 계층 없이 전송하는 아주 간단한 인터페이스이다. 간단하게 설명하면 아래와 같다.



1. HTTP를 사용한 웹 서비스이다.
2. 모든 자원은 고유 주소로 식별한다.
3. HTTP 메서드를 사용한다.
4. JSON, XML 등을 사용한다.



이러한 사양에 따라 구현된 서비스를 RESTful 웹 서비스라고 한다.

네이버, 구글 등도 RESTful API를 이용한 웹 서비스를 제공한다.

</br>



##### Volley 라이브러리 활용

Volley는 구글에서 공개한 안드로이드 용 라이브러리이다. 다른 안드로이드 용 HTTP 클라이언트 라이브러리가 제공하는 기능을 제공하면서도 용량이 작고 빠른 실행 속도를 보여 준다. 구글은 Volley와 같은 라이브러리 사용을 권장한다. 

Volley의 사용은 아래와 같다.</br>



1. RequestQueue 생성
2. Request Object 생성
3. Request Object를 RequestQueue에 추가
4. 설정한 Callback으로 응답 수신



</br>

**build.gradle**

```kotlin
implementation 'com.android.volley:volley:1.1.1'
```

</br>



아래는 Volley 라이브러리를 이용하여 http://time.jsontest.com 에서 시간 정보를 얻는 예제이다.</br>



**LocalVolley 클래스**

```kotlin
import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.lang.IllegalStateException

class LocalVolley {
    private lateinit var sRequestQueue : RequestQueue
    
    fun init(context: Context) {
        sRequestQueue = Volley.newRequestQueue(context)
    }
    
    @Throws(IllegalStateException::class)
    fun getRequestQueue(): RequestQueue = sRequestQueue
}
```



LocalVolley 클래스는 init 메서드가 호출되면 Context를 이용하여 RequestQueue를 생성하게 된다.

생성된 RequestQueue는 getRequestQueue 메서드를 이용하여 가져올 수 있다.</br>



**RxAndroid 클래스**

```kotlin
import android.app.Application
import com.leaf.rxandroid.volley.LocalVolley

class RxAndroid : Application() {
    override fun onCreate() {
        super.onCreate()

        LocalVolley().init(applicationContext)
    }
}
```

RxAndroid 클래스는 Application 클래스를 상속 받고 있다. 안드로이드 애플리케이션이 시작하면 해당 클래스의 onCreate 메서드가 최우선으로 호출이 되고 단 한 번만 실행된다.



애플리케이션 실행을 시작하면서 LocalVolley 클래스의 init 메서드가 호출되고 Volley의 RequestQueue를 사용할 수 있게 초기화한다.

싱글톤으로 초기화하여 사용할 수도 있지만 Context를 가지고 있어야 하여 애플리케이션이 생성되는 시점에 Queue를 생성하도록 한다. 싱글톤과 동일한 효과가 있다. </br>



**입력**

```kotlin
private val URL = "http://time.jsontest.com/"

private fun getFuture(): RequestFuture<JSONObject> {
    val future = RequestFuture.newFuture<JSONObject>()
    val req = JsonObjectRequest(URL, null, future, future)
    LocalVolley().getRequestQueue().add(req)
    return future
}

@Throws(ExecutionException::class, InterruptedException::class)
private fun getData(): JSONObject = getFuture().get()
```



getFuture 메서드는 생성한 JsonObjectRequest 객체를 RequestQueue에 추가한다.

Future은 자바에서 사용하는 비동기 계산 결과를 얻는 객체이다.</br>



Obsevable에서는 다양한 비동기 함수를 제공한다.

아래는 defer 함수를 사용한 예제이다.</br>



**입력**

```kotlin
private fun getObservable(): Observable<JSONObject> {
    return Observable.defer {
        try {
            return@defer Observable.just(getData())
        } catch (e: InterruptedException) {
            Log.e("log", e.message.toString())
            return@defer Observable.error<JSONObject>(e)
        } catch (e: ExecutionException) {
            Log.e("log", e.cause.toString())
            return@defer Observable.error<JSONObject>(e)
        }
    }
}
```



Observable.just를 사용하여 새로운 Observable를 생성하는 이유는 내부적으로 예외 처리를 하지 못하기 때문이다. 따라서 try-catch를 이용하여 명시적으로 에러를 처리해야 한다.



아래는 fromCallable을 사용한 예제이다.</br>

**입력**

```kotlin
private fun getObservableFromCallable(): Observable<JSONObject> = Observable.fromCallable(this::getData)
```



defer와 다르게 어떤 데이터 타입도 사용할 수 있다. fromCallable 함수를 이용하기 위해서는 Future 객체를 직접 전달하지 않고 Future.get을 전달한다.



마지막으로 fromFuture을 사용한 예제이다.

</br>



**입력**

```kotlin
private fun getObservableFromFuture(): Observable<JSONObject> = Observable.fromFuture(getFuture())
```



Observable 내부에서 get 메서드를 요청하고 결과를 전달 받아 Future 객체 자체를 내부에서 바로 처리한다.</br>



실행 하기 전에 AndroidManifast에 다음과 같이 추가한다.

```xml
<uses-permission android:name="android.permission.INTERNET"/>

<application
    android:name=".RxAndroid"
    android:usesCleartextTraffic="true"
    ...
</application>
```

</br>



**실행 결과**

```
{"date":"04-07-2020","milliseconds_since_epoch":1586282377603,"time":"05:59:37 PM"}
complete
{"date":"04-07-2020","milliseconds_since_epoch":1586282379142,"time":"05:59:39 PM"}
complete
{"date":"04-07-2020","milliseconds_since_epoch":1586282380154,"time":"05:59:40 PM"}
complete
```

</br></br>



#### Retrofit2 + OkHttp 활용하기

Retrofit2와 OkHttp는 RxAndroid를 배포하는 Square Open Source의 안드로이드 용 네트워킹 라이브러리이다.



OkHttp는 안드로이드에서 사용할 수 있는 대표 클라이언트 중 하나이며 페이스북에서 사용하고 있다. SPDY / GZIP 지원 등 네트워킹 스택을 효율적으로 관리할 수 있고, 빠른 응답 속도를 보일 수 있다는 장점이 있다.



Retrofit은 서버 연동과 응답 전체를 관리하는 라이브러리이다.

</br>



**build.gradle**

```
dependencies {
	// OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")

    // Retrofit2
    implementation 'com.squareup.retrofit2:retrofit:2.8.1'
    implementation 'com.squareup.retrofit2:converter-gson:2.8.1'
    implementation 'com.squareup.retrofit2:adapter-rxjava2:2.8.1'
    ...
}
```

</br>



Retrofit의 장점 중 하나는 애너테이션을 지원하는 것이다. 스프링처럼 애너테이션으로 APi를 설계할 수 있다.</br>



**GithubServiceApi**

```kotlin
interface GithubServiceApi {

    @GET("repos/{owner}/{repo}/contributors")
    fun getCallContributors(@Path("owner") owner: String, @Path("repo") repo: String) : Call<List<Contributor>>

    @GET("repos/{owner}/{repo}/contributors")
    fun getObContributors(@Path("owner") owner: String, @Path("repo") repo: String) : Observable<List<Contributor>>

    @Headers("Accept: application/vnd.github.v3.full+json")
    @GET("repos/{owner}/{repo}/contributors")
    fun getFutureContributors(@Path("owner") owner: String, @Path("repo") repo: String) : Future<List<Contributor>>
}
```



getCallContributors 메서드를 호출하면 Retrofit은 URL을 생성한다.

Retrofit은 RxJava를 정식으로 지원하므로 Observable을 API 리턴 값으로 사용할 수 있다.</br>



아래는 정의한 API를 사용할 수 잇는 사용자화 Adapter 클래스이다.

</br>



**RestfulAdapter**

```kotlin
class RestfulAdapter {

    companion object {

        private val BASE_URL = "https://api.github.com/"

        fun getSimpleApi() : GithubServiceApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GithubServiceApi::class.java)
        }

        fun getServiceApi() : GithubServiceApi {
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(GithubServiceApi::class.java)
        }
    }
}
```



Retrofit.Builder 객체를 생성하고 baseUrl 메서드와 addConverterFactory 메서드로 JSON 변환기를 설정하면 간단하게 생성할 수 있다. 마지막에 retrofit.create()를 호출할 때 해당 API 인터페이스의 클래스를 넣어주면 Builder에 설정한 정보를 바탕으로 단일 인터페이스 프락시를 생성한다.



getSimpleApi와 getServiceApi의 차이점은 REST API 스택의 디버깅이 가능 한지의 여부이다.

getSimpleApi는 Retrofit에 포함된 OkHttpClient 클래스를 사용하게 되고 getServiceApi는 따로 OkHttpClient.Builder 객체를 구성하여 로그를 위한 인터셉터를 설정한다. 인터셉터를 설정하게 되면 네트워크를 통해 이동하는 데이터나 에러 메시지를 실시간으로 확인할 수 있다.</br>



아래는 JSON 응답에서 필요한 정보 데이터를 추출하는 Contributor 데이터 클래스이다.

</br>



**Contributor**

```kotlin
data class Contributor(val login : String? = null, val url : String? = null, val id : Int? = null)
```



원하는 정보만 JSON에 맞게 정의하면 GSON에서 디코딩하여 원하는 값을 Contributor 클래스의 필드에 설정한다.

따라서 JSON에서 응답해야 할 login, url, id만 추출한다.

</br>



**입력**

```kotlin
class OkHttpFragment : Fragment() {

    private val sName = "Im-Tae"
    private val sRepo = "RxJava2_Study"
    private val mCompositeDisposable = CompositeDisposable()

	// 생략

    // retrofit + okHttp
    private fun startOkHttp() {
        val service = RestfulAdapter.getServiceApi()
        val call = service.getCallContributors(sName, sRepo)

        call.enqueue(object : Callback<List<Contributor>> {
            override fun onResponse(call: Call<List<Contributor>>, response: Response<List<Contributor>>) {
                if (response.isSuccessful) {
                    val contributors = response.body()

                    for (c in contributors!!) {
                        Log.i("log", c.toString())
                    }
                } else {
                    Log.i("log", "not successful")
                }
            }

            override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                Log.i("log", t.message.toString())
            }
        })
    }

    // retrofit + okHttp( Call의 내부 )
    private fun startRetrofit() {
        val service = RestfulAdapter.getSimpleApi()
        val call = service.getCallContributors(sName, sRepo)

        call.enqueue(object : Callback<List<Contributor>> {
            override fun onResponse(call: Call<List<Contributor>>, response: Response<List<Contributor>>) {
                if (response.isSuccessful) {
                    val contributors = response.body()

                    for (c in contributors!!) {
                        Log.i("log", c.toString())
                    }
                } else {
                    Log.i("log", "not successful")
                }
            }

            override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                Log.i("log", t.message.toString())
            }
        })
    }

    // retrofit + okHttp + rxJava
    private fun startRx() {
        val service = RestfulAdapter.getServiceApi()
        val observable = service.getObContributors(sName, sRepo)

        mCompositeDisposable.add(
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Contributor>>() {
                    override fun onNext(contributors: List<Contributor>) {
                        for (c in contributors) {
                            Log.i("log", c.toString())
                        }
                    }

                    override fun onError(t: Throwable) {
                        Log.i("log", t.message.toString())
                    }

                    override fun onComplete() {
                        Log.i("log", "complete")
                    }
                })
        )
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
        android:id="@+id/ohf_btn_retrofit"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="retrofit"/>

    <Button
        android:id="@+id/ohf_btn_get_retrofit_okhttp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="retrofit + okhttp"/>

    <Button
        android:id="@+id/ohf_btn_get_retrofit_okhttp_rx"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="retrofit + okhttp + rxJava"/>

</LinearLayout>
```

**출력**

```
Contributor(login=Im-Tae, url=https://api.github.com/users/Im-Tae, id=41174361)
Contributor(login=Im-Tae, url=https://api.github.com/users/Im-Tae, id=41174361)
Contributor(login=Im-Tae, url=https://api.github.com/users/Im-Tae, id=41174361)
complete
```



startRx 메서드는 RestfulAdapter 클래스의 getServiceApi 메서드 안 retrofit 변수를 이용해 생성된 API 프락시를 가져온다. owner와 repo의 값을 전달하면 Observable 변수에 저장된 Observable을 리턴 한다. 생성된 Observable에 구독자를 설정하면 getServiceApi 메서드를 호출하여 github에서 정보를 얻어온다. 결과는 구독자가 수신하게 되고 GSON에서 Contributor 클래스의 구조에 맞게 디코딩하고 출력한다.



startRetrofit 메서드도 동일하다. 하지만 retrofit에서 제공하는 Call 인터페이스를 사용해야 한다.

Call 인터페이스의 enqueue 메서드에 콜백을 등록하면 GSON에서 디코딩한 결과를 얻을 수 있다.



안드로이드에서 Retrofit의 콜백은 UI 스레드에서 실행한다. 만약 처리 시간이 오래 걸리는 작업이 필요하다면 새로운 스레드를 생성해서 실행해야 한다.

</br></br>

