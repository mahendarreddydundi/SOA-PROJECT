package com.resolvenow.gateway;

import java.nio.charset.StandardCharsets; import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts; import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; import org.springframework.cloud.gateway.filter.*; import org.springframework.core.Ordered; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Component; import org.springframework.web.server.ServerWebExchange; import reactor.core.publisher.Mono;

@Component public class JwtGatewayFilter implements GlobalFilter, Ordered {
 private final SecretKey key; public JwtGatewayFilter(@Value("${security.jwt.secret}") String secret){key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
 @Override public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){String path=exchange.getRequest().getURI().getPath(); if(path.startsWith("/auth/")||path.equals("/actuator/health"))return chain.filter(exchange); String header=exchange.getRequest().getHeaders().getFirst("Authorization"); if(header==null||!header.startsWith("Bearer "))return reject(exchange); try{String subject=Jwts.parser().verifyWith(key).build().parseSignedClaims(header.substring(7)).getPayload().getSubject(); if(subject==null||subject.isBlank())return reject(exchange); return chain.filter(exchange);}catch(RuntimeException ex){return reject(exchange);}}
 private Mono<Void> reject(ServerWebExchange exchange){exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);return exchange.getResponse().setComplete();} @Override public int getOrder(){return -100;}
}
