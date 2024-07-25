# 소셜 로그인 가이드:
- 모든 책임을 백엔드가 맡는 방식을 사용했습니다.
- 카카오 공식 답변중 여러 사용자가 접근할 수 있는 웹에서는 Access 토큰을 서버 에서 관리하는 것이 좋다는 글이 있었습니다. [카카오 질문 답변 링크](https://devtalk.kakao.com/t/ios/130676/10)
- 서버에서 (로그인페이지 요청 -> 코드 발급 -> Access토큰 -> 유저 정보 획득 -> JWT발급) 과정을 처리합니다.
- 로그인을 성공하고 요청한 클라이언트의 쿠키에 서버에서 발급한 JWT가 저장됩니다.
- 로그인 정보는 JWT를 사용한 요청으로 알 수 있습니다.

# 소셜 로그인 참고사항
- 카카오톡과 구글 이렇게 2종류의 소셜 로그인을 적용시켰습니다.
- 소셜 정보에서 프로필 이미지와 이름 사용을 합니다. (정보 제공 동의 항목에서 보실 수 있습니다.)
- 카카오톡은 별도의 등록과정이 필요없이 로그인을 진행하면 됩니다.
- 구글은 비지니스 앱이 아니면 구글 API에서 구글 계정 등록을 해야 로그인이 가능하기 때문에 구글  로그인 해보고 싶으시다면 연락 부탁드립니다.

## 소셜 로그인 세팅
- 루트 디렉토리(server).src.main에 resources폴더 생성 후, application.yml생성.
- application.yml
  ```
  spring:
   jwt:
     secret: jwt시크릿 키 
   datasource:
     driver-class-name: db드라이버
     url: db주소
     username: db의 username
     password: db의 password

   jpa:
     hibernate:
       ddl-auto: ddl 설정
   security:
     oauth2:
       client:
         registration:
           kakao:
             client-name: kakao
             client-id: 카카오 restAPI키
             client-secret: 카카오 시크릿키
             redirect-uri: kakao redirect url
             client-authentication-method: client_secret_post
             authorization-grant-type: authorization_code
             scope:
               - profile_nickname
               - profile_image
           naver:
             client-name: google
             client-id: google client id
             client-secret: google secret
             redirect-uri: google redirect url
             authorization-grant-type: authorization_code
             scope:
               - picture
               - name
         provider:
           kakao:
             authorization-uri: https://kauth.kakao.com/oauth/authorize
             token-uri: https://kauth.kakao.com/oauth/token
             user-info-uri: https://kapi.kakao.com/v2/user/me
             user-name-attribute: id
   client:
     url: http://localhost:3000/
  ```


## social login react 요청 예시
```
import React from 'react';

const onKakaoLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/kakao";
}

function KakaoLogin(props) {
    return (
        <>
            <h1>Login</h1>
            <button onClick={onKakaoLogin}>kakao login</button>
        </>
    );
}

export default KakaoLogin;
```

```
import React from 'react';

const onGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
}

function Login(props) {
    return (
        <>
            <h1>Login</h1>
            <button onClick={onGoogleLogin}>google login</button>
        </>
    );
}

export default Login;
```
## 성공 확인
- 개발자 도구로 Cookie에 들어갔을때 Access Token과 Refresh Token이 있으면 성공
  
