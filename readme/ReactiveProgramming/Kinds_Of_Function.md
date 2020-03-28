### 함수의 종류

</br>



#### just 함수

데이터를 차례로 발행하려고 Observable을 생성한다. 

[타입이 모두 같아야 하고, 한 개의 값부터 최대 열 개의 값을 넣을 수 있다.]

</br></br>



#### create 함수

onNext,  onComplete, onError 같은 알림을 개발자가 호출해야 한다.

</br></br>



#### fromArray

배열에 들어 있는 데이터를 처리할 때 사용한다.

</br></br>



#### fromIterable

이터레이터 패턴을 구현한 것으로 다음에 어떤 데이터가 있는지와 그 값을 얻어오는 것만 관여할 뿐 특정 데이터 타입에 의존하지 않는 장점이 있다.

Iterable 인터페이스를 구현하는 대표적인 클래스는 ArrayList, ArrayBlockingQueue, HashSet, LinkedList, Stack 등이 있다.

</br></br>



#### fromCallable

callable 객체와 fromCallable 함수를 이용해 Observable을 만드는 방법이다.

</br></br>



#### fromFuture

future 객체에서 fromFuture 함수를 사용해 Observable을 생성하는 방법이다.

</br></br>



#### fromPublisher

자바 9의 표준 API인 Publisher을 사용하여 fromPublisher  함수를 통해서 Observable을 만드는 방법이다.

</br></br>



#### Map

입력 값에 어떤 함수에 넣어서 원하는 값으로 변환하는 함수이다.

</br></br>



#### FlatMap

FlatMap 함수는 Map 함수를 발전시킨 함수이다. FlatMap 함수는 결과가 Observable로 나온다.

Map 함수가 일대일 함수라면 FlatMap 함수는 일대다 혹은 일대일 Observable 함수이다.

</br></br>



#### Filter

Observable에서 원하는 데이터만 걸러내는 역할을 한다.

</br></br>



#### Reduce

발행한 데이터를 모두 사용하여 어떤 최종 결과 데이터를 합성할 때 활용한다.

</br></br>



#### interval 

일정 시간 간격으로 데이터 흐름을 생성한다.

</br></br>



#### timer

일정 시간이 지난 후에 한 개의 데이터를 발행하고 onComplete 이벤트가 발생한다.

</br></br>



#### range

주어진 값(n)부터 m개의 Int 객체를 발행한다.

</br></br>



#### intervalRange

interval처럼 일정한 시간 간격으로 값을 출력하지만, rnage 함수처럼 시작 숫자(n)로부터 m개 만큼의 값만 생성하고 onComplete 이벤트가 발생한다.

</br></br>



#### defer

timer 함수와 비슷하지만 데이터 흐름 생성을 구독자가 subscribe를 호출할 때까지 미룰 수 있다.

</br></br>



#### repeat

단순 반복 실행을 한다.

</br></br>



#### concatMap

flatMap은 먼저 들어온 데이터를 처리하는 도중에 새로운 데이터가 들어오면 나중에 들어온 데이터의 처리 결과가 먼저 출력 될 수도 있다. 하지만 concatMap은 먼저 들어온 데이터 순서대로 처리해서 결과를 낼 수 있도록 보장한다.

</br></br>



#### switchMap

순서를 보장하기 위해 기존에 진행 중이던 작업을 바로 중단한다. 중간에 끊기더라도 마지막 데이터의 처리는 보장하기 때문에 여러 개의 값이 발행 되었을 때 마지막에 들어온 값만 처리하고 싶을 때 사용한다.

</br></br>



#### groupBy

어떤 기준으로  단일 Observable을 여러 개로 이루어진 Observable 그룹으로 만든다.

</br></br>



#### scan

reduce 함수가 Observable에서 모든 데이터가 입력된 후 그것을 종합하여 마지막 1 개의 데이터 만을 구독자에게 발행한다면, scan 함수는 실행할 때마다 입력 값에 맞는 중간 결과 및 최종 결과를 구독자에게 발행한다.

</br></br>



#### zip

입력 Observable에서 데이터를 모두 새로 발행했을 때 그것을 합해 준다.

</br></br>



#### combineLatest

처음에 각 Observable에서 데이터를 발행한 후에는 어디에서 값을 발행하던 최신 값으로 갱신한다.

</br></br>



#### merge

최신 데이터 여부와 상관없이 각 Observable에서 발행하는 데이터를 그대로 출력한다.

</br></br>



#### concat

입력된 Observable을 Observable 단위로 이어 붙여 준다.

</br></br>



#### amb

둘 중 어느 것이든 먼저 나오는 Observable을 채택한다.

</br></br>



#### takeUntil

다른 Observable에서 데이터가 발행되기 전까지만 현재 Observable을 채택한다.

</br></br>



#### skipUntil

다른 Observable에서 데이터가 발행될 때까지는 현재 Observable에서 발행하는 값을 무시한다.

</br></br>



#### all

Observable에 입력되는 값의 조건이 100% 맞을 때만 true를 발행하고, 조건에 맞지 않는 데이터가 발행되면 바로 false 값을 발행한다.

</br></br>



#### delay

timer, interval, defer처럼 delay도 시간을 다루는 함수이다.

</br></br>



#### timeInterval

어떤 값을 발행했을 때 이전 값을 발행한 이후 얼마나 시간이 지났는지 알려 준다.

</br></br>

