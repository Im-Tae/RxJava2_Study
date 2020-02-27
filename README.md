## RxJava2 공부 정리

#### 🤔 What is RxJava2_Study?

RxJava2를 공부한 흔적을 보여주는 Repository 입니다.



<img src="https://lh3.googleusercontent.com/proxy/mm-hAjoDFH2KozPg0eoqUpi5cgVpKtkuaYnayKfiMv3eNgRd8fVqHFgRHU4TtYOXL94kUXZZBCHe0ensy4RICHsaje9t3AdMTGLVmHfq40KgLzN0i4yvQUpDzw" width = "300" height = "450"  />

한빛 미디어의 RxJava 프로그래밍 책을 보면서 안에 코드를 코틀린으로 바꿔가며 공부하고 있습니다.</br>



#### 리액티브 프로그래밍 이란?

 데이터 흐름과 전달에 관한 프로그래밍 패러다임이다.  [링크](https://en.wikipedia.org/wiki/Reactive_programming)</br>



#### 마블 다이어그램 보는 법

[ReactiveX 홈페이지](http://reactivex.io) 에 있는 flip() 함수의 마블 다이어그램을 살펴보면



<img src="" width = "300" height = "450"  /></br>



1. 위에 있는 실선은 Observable의 시간 표시줄이다. 시간순으로 데이터가 발행되는 것을 표현한다.

2. Observable에서 발행되는 데이터는 시간 순서대로 별, 삼각형, 오각형, 원 등의 도형을 발행한다. 

   데이터를 발행할 때는 onNext 알림이 발생한다.

3. 파이프(|)는 Observable에서 데이터 발행을 완료했다는 의미이다. onComplete 알림이 발생한다.

4. 아래로 내려오는 점선 화살표는 각각 함수의 입력과 출력 데이터이다. 가운데 박스는 함수를 의미한다. flip함수는 입력값을 뒤집는 함수여서 모양이 180도 회전이 된다.

5. 아래에 있는 실선은 함수의 결과가 출력된 시간 표시줄이다.

6. X는 처리할 때 발생한 에러를 의미한다. onError 알림이 발생한다. </br></br>



flip 마블 다이어그램보다 좀 더 복잡한 마블 다이어그램인 combineLatest를 살펴보면, combineLatest 함수의 마블 다이어그램은 2개 이상의 Observable을 처리한다.



<img src="" width = "300" height = "450"  /></br>



1. 첫번째 Observable은 같은 모양이지만 번호가 다른 도형을 발행한다.

2. 두번째 Observable은 모양은 다르지만 번호가 없는 도형을 발행한다.

3. combineLatest 함수는 첫번째 Observable 도형과 두번째 Observable 도형이 모두 들어오면 합성한다.

4. 아래 시간 표시줄을 보면 두 Observable을 조합한 결과라는 것을 알 수 있다. 

   (첫번째는 Observable은 색상, 두번째 Observable은 모양)