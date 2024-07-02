social login react 요청 예시
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
