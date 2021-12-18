package com.example.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.Jwts;

// thx for ref : https://wonit.tistory.com/500 , https://cloud.spring.io/spring-cloud-gateway/multi/multi__developer_guide.html
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private Environment env;
    public static class Config { }
    public AuthenticationFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) { // key가 있는지 체크
                return onError(exchange,"You don't have authorization header", HttpStatus.UNAUTHORIZED);
            }
            String authorizationHeader =  request.getHeaders().get(org.apache.http.HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer","");
            if (!isJwtValid(jwt)) {
                return onError(exchange,"JWT is not valid", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        };
    }
    // thx for ref : https://m.blog.naver.com/sthwin/222008385384
    private Mono<Void> onError(ServerWebExchange exchange, String errorMsg, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
    private boolean isJwtValid(String jwt) {
        try {
            Claims accessClaims = Jwts.parser().setSigningKey("token_secret")
                    .parseClaimsJws(jwt)
                    .getBody();
            return true;
        }
        catch (ExpiredJwtException exception) {
            return false;
        }
        catch (JwtException exception) {
            return false;
        }
        catch (NullPointerException exception) {
            return false;
        }
    }
}
