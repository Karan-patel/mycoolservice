package com.exercise.mycoolservice.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class JwtRequestFilter implements WebFilter {

    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        log.debug("JwtRequestFilter filter to authenticate and load context...");

        final String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String jwt = authorizationHeader.substring(7);
            final String username = jwtUtil.extractUsername(jwt);
            final List<String> roles = jwtUtil.extractRoles(jwt);

            return userDetailsService.findByUsername(username)
                    .filter(userDetails -> jwtUtil.validateToken(jwt, userDetails.getUsername()))
                    .flatMap(userDetails -> {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, roles.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .toList());
                        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
                    });
        }
        return chain.filter(exchange);
    }
}
