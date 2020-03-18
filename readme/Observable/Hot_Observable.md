### 뜨거운 Observable

Observable에는 차가운 Observable, 뜨거운 Observable이 있다.



##### 차가운 Observable

Observable을 선언하고 just, fromArray 함수를 호출해도 옵서버가 subscribe를 호출하여 구독하지 않으면 데이터를 발행하지 않는다. 즉, 게으른 접근법이다.

웹 요청, 데이터베이스 쿼리, 파일 읽기 등에 쓰이고, 원하는 URL이나 데이터를 지정하면 그때부터 서버나 데이터베이스 서버에 요청을 하고 결과를 받아온다.

</br>



##### 뜨거운 Observable

구독자의 존재 여부와 관계없이 데이터를 발행하는 Observable이다. 즉, 여러 구독자를 고려할 수 있다.

마우스 이벤트, 키보드 이벤트, 시스템 이벤트 등이 있고, 온도를 처리하는 앱이라면 최근의 온도 정보만 표시하면 된다.

뜨거운 Observable에서 주의 해야될 점은 데이터를 발행하는 속도와 구독자가 처리하는 속도의 차이가 클 때 발생하는 **배압**을 주의 해야한다.

</br></br>



**차가운 Observable**은 구독하면 준비된 데이터를 처음부터 발행하고, **뜨거운 Observable**은 구독한 시점부터 Observable에서 발핸한 값을 받는다.

차가운 Observable을 뜨거운 Observable 객체로 변환하는 방법은 [Subject]() 객체를 만들거나 [ConnectableObservable]() 클래스를 활용한다.