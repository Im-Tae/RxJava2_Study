### Reduce

발행한 데이터를 모두 사용하여 어떤 최종 결과 데이터를 합성할 때 활용한다.

보통 Observable에 입력된 데이터를 필요한 map 함수로 매핑하고, 원하는 데이터만 추출할 때는 불필요한 데이터를 걸러내는 filter 함수를 호출한다. 또한 상황에 따라 발행된 데이터를 취합하여 어떤 결과를 만들어낼 때는 reduce 함수를 사용한다. </br>





<img src="https://github.com/Im-Tae/RxJava2_Study/blob/master/image/reduce.png?raw=true" width = "550" height = "250"  /> </br>



아래는 마블 다이어그램의 코드이다.</br>



**입력**

```kotlin
import io.reactivex.Observable

class ReduceExample {
    fun marbleDiagram() {
        val balls = arrayOf("1", "3", "5")
        val source = Observable.fromArray(*balls)
            .reduce{ ball1, ball2 -> "$ball2($ball1)"}
        source.subscribe { data -> println(data) }
    }
}

fun main() {
    val demo = ReduceExample()
    demo.marbleDiagram()
}
```

**출력**

```
5(3(1))
```



빨간색, 초록색, 파란색 원을 받아서 먼저 받은 원이 내부로 흡수되는 것을 보여준다.

최종적으로 빨간색, 초록색, 파란색 원을 모두 포함하는 더 큰 원을 출력한다.

</br>



여기서 source 변수는 Observable<String> 이 아니라 Maybe<String>이다. 

reduce 함수를 호출하면 인자로 넘긴 람다 표현식에 의해 결과 없이 완료될 수도 있어서 Observable이 아니라 Maybe 객체로 리턴된다.

</br>



람다 표현식에서 인자의 개수가 2개 이상일 때는 괄호로 인자의 목록을 명시적으로 표현해줘야 한다. 

따라서 아래와 같은 람다 표현식을 사용한다.

```kotlin
ball1, ball2 -> "$ball2($ball1)"
```

</br>



아래는 람다 표현식을 별도 함수로 분리한 코드이다.</br>



**입력**

```kotlin
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class ReduceExample {fun biFunction() {
        val mergeBalls = BiFunction<String, String, String> { 
            ball1, ball2 -> "$ball2($ball1)" 
        }
    
        val balls = arrayOf("1", "3", "5")
        val source = Observable.fromArray(*balls)
            .reduce(mergeBalls)
        source.subscribe { data -> println(data) }
    }
}

fun main() {
    val demo = ReduceExample()
    demo.biFunction()
}
```

**출력**

```
5(3(1))
```

</br></br>



#### 데이터 쿼리 예제

아래는 map, filter, reduce를 활용한 간단한 데이터 쿼리 예제이다.

```
오늘의 매출

TV- $2,500
Camera - $300
TV: $1,600
Phone- $800
```

</br>

예제 코드는 오늘 발생한 TV 매출의 총합을 구한다.

```
계산 방법

1. 전체 매출 데이터를 입력한다.
2. 매출 데이터 중 TV 매출을 필터링한다.
3. TV 매출의 합을 구한다.
```

</br>



**입력**

```kotlin
import io.reactivex.Observable
import kotlin.collections.ArrayList


class QueryTvSales {
    fun run() {
        
        // 1. 데이터 입력
        val sales: MutableList<Pair<String, Int>> = ArrayList()
        sales.add(Pair("TV", 2500))
        sales.add(Pair("Camera", 300))
        sales.add(Pair("TV", 1600))
        sales.add(Pair("Phone", 800))
        
        val tvSales = Observable.fromIterable(sales)
                
            // 2. TV 매출 필터링
            .filter { sale -> "TV" == sale.first }
            .map { sale -> sale.second } 
                
            // 3. TV 매출의 합
            .reduce { sale1, sale2 -> sale1 + sale2 }
        tvSales.subscribe { total -> println("TV Sales : $$total") }
    }
}

fun main() {
    val demo = QueryTvSales()
    demo.run()
}
```

**출력**

```
TV Sales : $4100
```



먼저 데이터는 MutableList와 Pair를 사용하여 입력을 받았다.

TV 매출 필터링을 위해 filter 함수를 사용했고, TV 매출의 합을 구하기 위해 map과 reduce를 사용한 것을 볼 수 있다.

마지막으로 subscribe를 호출하여 결과를 확인한다.



</br></br>