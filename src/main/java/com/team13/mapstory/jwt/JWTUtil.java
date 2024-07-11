package com.team13.mapstory.jwt;

import com.team13.mapstory.entity.User;
import com.team13.mapstory.repository.UserRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

// @RequiredArgsConstructor 를 쓰면 오류가 남.
// @Value를 사용한 외부 설정 값 주입은 생성자가 아니라 Spring 빈 초기화 과정에서 처리된다.
// @RequiredArgsConstructor는 생성자에서 초기화를 한다.
// Spring 빈 초기화 순서
//    1. 클래스의 빈 인스턴스 생성(lombok의 어노테이션들을 통한)
//    2. @Autowired나 @Value
//    3. @PostConstruct 메서드 호출

@Component
public class JWTUtil {

    private SecretKey secretKey;
    private UserRepository userRepository;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret, UserRepository userRepository) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

        this.userRepository = userRepository;
    }

    public String getNickName(String token) {

        Claims claims = parseClaims(token);
        String nickname = claims.get("nickname", String.class);
        return nickname;
    }

    public String getRole(String token) {
        Claims claims = parseClaims(token);
        String role = claims.get("role", String.class);
        return role;
    }

    public String getLoginId(String token) {
        Claims claims = parseClaims(token);
        String loginId = claims.get("loginId", String.class);
        return loginId;
    }

    public Boolean isExpired(String token) {
         Claims claims = parseClaims(token);
         return claims.getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            System.out.println("MalformedJwtException");
            return null;
        } catch (SecurityException e) {
            System.out.println("SecurityException");
            return null;
        }
    }

    public String createJwt(String nickname, String role, String loginId, Long expiredMs) {

        return Jwts.builder()
                .claim("nickname", nickname)
                .claim("role", role)
                .claim("loginId",loginId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String accessToken) {
        String nickname = getNickName(accessToken);
        String role = getRole(accessToken);
        String loginId = getLoginId(accessToken);
        String refeshToken = createJwt(nickname,role,loginId,86400000L);
        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRefreshToken(refeshToken);
            userRepository.save(user);
        }

        return refeshToken;
    }

    public String refreshAccessToken(String refreshToken, String accessToken) {
        String nickName = getNickName(accessToken);
        String role = getRole(accessToken);
        String loginId = getLoginId(accessToken);
        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getRefreshToken().equals(refreshToken)){
                if(!isExpired(refreshToken)) {
                    String refreshedAccessToken = createJwt(nickName,role,loginId,60*60*60L);
                    return refreshedAccessToken;
                }
            }
        }

        return null;
    }
}
