### Maybe 클래스

Single 클래스와 같이 최대 데이터 하나를 가질 수 있지만 데이터 발행 없이 바로 데이터 발생을 완료 할 수 있다.

즉, Maybe 클래스는 Single 클래스에 onComplete 이벤트가 추가된 형태이다.

Maybe 클래스를 통해서 생성 할 수 있지만 보통 Observable 연산자를 통해 생성한다.

Maybe 객체를 생성할 수 있는 연산자는 elementAt(), firstElement(), flatMapMaybe(), lastElement(), reduce(), singleElement() 등이 있다.