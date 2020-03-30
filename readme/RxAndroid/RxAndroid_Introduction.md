### RxAndroid 소개

RxAndroid는 RxJava에 최소한의 클래스를 추가하여 안드로이드 앱에서 리액티브 구성 요소를 쉽고 간편하게 사용하게 만드는 라이브러리이다. </br>



기존 안드로이드 개발에서 가장 어려움을 겪는 문제 중 하나는 복잡한 스레드 사용이다.

복잡한 스레드 사용으로 발생하는 문제는 다음과 같다.</br>



- 안드로이드의 비동기 처리 및 에러 핸들링
- 수많은 핸들러와 콜백 때문에 발생하는 디버깅 문제
- 2 개의 비동기 처리 후 결과를 하나로 합성하는 작업
- 이벤트 중복 실행

</br>



RxAndroid는 습득하기 어려운 부분도 있지만 기존 안드로이드 개발과 비교했을 때 장점이 많다. 다음 특징을 통해 앞 문제를 해결하는 데 도움을 준다.</br>



- 간단한 코드로 복잡한 병행 프로그래밍을 할 수 있다.
- 비동기 구조에서 에러를 다루기 쉽다.
- 함수형 프로그래밍 기법도 부분적으로 적용할 수 있다.

</br>



##### 리액티브 라이브러리와 API

Rxandroid는 기본적으로 RxJava의 리액티브 라이브러리를 이용한다.

아래는 안드로이드에서 사용할 수 있는 리액티브 API와 라이브러리의 목록이다.</br>



| 리액티브 API 이름        | 설명                                                         |
| ------------------------ | ------------------------------------------------------------ |
| RxLifecycle              | RxJava를 사용하는 안드로이드 앱용 라이프 사이클 처리 API이다. 일정 관리 도구로 유명한 트렐로에서 만들었다. |
| RxBinding                | 안드로이드 UI 위젯용 RxJava 바인딩 API이다.                  |
| SqlBrite                 | SQLiteOpenHelper와 ContentResolver 클래스의 래퍼 클래스로 쿼리에 리액티브 스트림을 도입한다. |
| Android-ReactiveLocation | 안드로이드용 리액티브 위치 API 라이브러리이다. (RxJava 1.x 기준) |
| RxLocation               | 안드로이드용 리액티브 위치 API 라이브러리이다. (RxJava 2.x 기준) |
| rx-preferences           | 안드로이드용 리액티브 SharedPreferences 인터페이스이다.      |
| RxFit                    | 안드로이드용 리액티브 Fit 라이브러리이다.                    |
| RxWear                   | 안드로이드용 리액티브 웨어러블 API 라이브러리이다.           |
| RxPermissions            | RxJava에서 제공하는 안드로이드 런타임 권한 라이브러리이다.   |
| RxNotification           | RxJava로 알림을 관리하는 API이다.                            |
| RxClipboard              | 안드로이드 클립 보드용 RxJava 바인딩 API이다.                |
| RxBroadCast              | 안드로이드 Broadcast 및 LocalBroadcast에 관한 RxJava 바인딩 API이다. |
| RxAndroidBle             | 블루투스 LE 장치를 다루기 위한 리액티브 라이브러리이다.      |
| RxImagePicker            | 갤러리 또는 카메라에서 이미지를 선택하기 위한 리액티브 라이브러리이다. |
| ReactiveNetwork          | 네트워크 연결 상태나 인터넷 연결 상태를 확인하는 리액티브 라이브러리이다. (RxJava 1.x 및 RxJava 2.x와 호환) |
| ReactiveBeacons          | 주변에 있는 블루투스 LE 기반의 비컨을 수신하는 리액티브 라이브러리이다. (RxJava 1.x 및 RxJava 2.x와 호환) |
| RxDataBinding            | 안드로이드 데이터 바인딩 라이브러리용 RxJava2 바인딩 API이다. |

</br>



##### 안드로이드 스튜디오 환경 설정

아래는 build.gradle 설정이다.</br>



```kotlin
dependencies {

    // RxJava
    implementation 'io.reactivex.rxjava3:rxjava:3.0.0'
    implementation 'io.reactivex.rxjava3:rxjava:3.0.0'

    // RxLifecycle
    implementation 'com.trello.rxlifecycle3:rxlifecycle-android:3.1.0'
    implementation 'com.trello.rxlifecycle3:rxlifecycle:3.1.0'
    implementation 'com.trello.rxlifecycle3:rxlifecycle-components:3.1.0'
}
```

</br>

RxAndroid는 RxJava에 대한 의존성이 있어 RxJava를 추가하지 않아도 되지만, 최신 버전의 RxJava를 사용하려면 명시해 주는 것이 좋다.