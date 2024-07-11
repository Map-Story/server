# 소셜 로그인 가이드:
- [자료](https://substantial-park-a17.notion.site/OAuth2-JWT-2c0ed188191f48bc8f1f45b73eef4f65) 참고한 소셜 로그인 기능

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
             redirect-uri: http://localhost:8080/login/oauth2/code/kakao
             client-authentication-method: client_secret_post
             authorization-grant-type: authorization_code
             scope:
               - profile_nickname
               - profile_image
           naver:
             client-name: naver
             client-id: naver client id
             client-secret: naver secret
             redirect-uri: http://localhost:8080/login/oauth2/code/naver
             authorization-grant-type: authorization_code
             scope:
               - name
               - email
         provider:
           kakao:
             authorization-uri: https://kauth.kakao.com/oauth/authorize
             token-uri: https://kauth.kakao.com/oauth/token
             user-info-uri: https://kapi.kakao.com/v2/user/me
             user-name-attribute: id
           naver:
             authorization-uri: https://nid.naver.com/oauth2.0/authorize
             token-uri: https://nid.naver.com/oauth2.0/token
             user-info-uri: https://openapi.naver.com/v1/nid/me
             user-name-attribute: response
   springdoc:
     swagger-ui:
       path: /swagger-ui.html
       tags-sorter: alpha
       operations-sorter: method
     api-docs:
       path: /api-docs

   client:
     url: http://localhost:3000/
  ```


## social login react 요청 예시
```
import React from 'react';

const onNaverLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/kakao";
}

function KakaoLogin(props) {
    return (
        <>
            <h1>Login</h1>
            <button onClick={onNaverLogin}>kakao login</button>
        </>
    );
}

export default KakaoLogin;
```

```
import React from 'react';

const onNaverLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/naver";
}

function Login(props) {
    return (
        <>
            <h1>Login</h1>
            <button onClick={onNaverLogin}>naver login</button>
        </>
    );
}

export default Login;
```
## 확인
- 개발자 도구의 cookie부분에 Authorization 토큰 있으면 성공
- 실패 : 실패 페이지가 뜸.
