package com.exercise.mycoolservice.opa;

import com.exercise.mycoolservice.opa.model.Input;
import com.exercise.mycoolservice.opa.model.OpaRequest;
import com.exercise.mycoolservice.opa.model.User;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Slf4j
public class OpaAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Autowired
    public WebClient opaWebClient;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        log.info("Inside OpaAuthorizationManager to authorize request based on OPA policy !");

        final Mono<OpaRequest> opaPayload = getPayload(authentication, authorizationContext);

        return opaPayload.flatMap(opaRequest -> {
            if (opaRequest != null) {
                //ROLE, AUTHENTICATION STATUS IS REQUIRED FOR POLICY TO EVALUATE AUTHORIZATION, IF IT IS NOT PRESENT SIMPLY UN-AUTHORIZE.
                log.info("Invoking OPA api to retrieve authorization decision for given user based on opa-policy");
                return opaWebClient.post()
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(opaRequest)
                        .exchangeToMono(this::toDecision)
                        .onErrorComplete(err -> {
                            //If api fails to respond, log error and un-authorize request !
                            log.error("Error while invoking OPA api", err);
                            return false;
                        });
            }
            return Mono.just(new AuthorizationDecision(false));
        });
    }

    private static Mono<OpaRequest> getPayload(Mono<Authentication> authentication, AuthorizationContext context) {

        return authentication.map(auth -> {
            if (Optional.ofNullable(auth.getAuthorities()).isPresent() && !auth.getAuthorities().isEmpty()) {
                //ONLY IF AUTHORITIES (ROLE) IS PRESENT.
                final String role = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().get();
                final String httpRequestMethod = context.getExchange().getRequest().getMethod().name();

                log.debug("Preparing request payload : isAuthenticated: [{}], role: [{}], method: [{}]", auth.isAuthenticated(), role, httpRequestMethod);
                return OpaRequest.builder()
                        .input(Input.builder().method(httpRequestMethod)
                                .user(User.builder().authenticated(auth.isAuthenticated()).role(role).build())
                                .build())
                        .build();
            }
            //RETURN EMPTY IF ROLES ARE NOT PRESENT.
            return null;
        });
    }

    private Mono<AuthorizationDecision> toDecision(ClientResponse response) {

        if (!response.statusCode().is2xxSuccessful()) {
            log.error("Error while retrieving authorization decision from OPA, marking as Un-authorized");
            return Mono.just(new AuthorizationDecision(false));
        }

        return response.bodyToMono(String.class).map(decision -> {
            log.debug("Received authorization decision from OPA, Authorized: [{}]", decision);
            return new AuthorizationDecision(JsonPath.read(decision, "$.result.allow"));
        });
    }
}
