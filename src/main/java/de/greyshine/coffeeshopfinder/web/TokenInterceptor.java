package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.service.UserService;
import de.greyshine.coffeeshopfinder.web.annotation.Tokenized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

@Configuration
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private static final AtomicLong COUNT_REQUESTS = new AtomicLong(0);
    private static final Map<Tokenized, Set<String>> tokenizedRights = new HashMap<>();
    private final UserService userService;

    public TokenInterceptor(@Autowired UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        final long requestCount = COUNT_REQUESTS.addAndGet(1);

        if (!handler.toString().equals("de.greyshine.coffeeshopfinder.web.IndexController#ping(String)")) {
            log.info("request {}; {}, handler={}", requestCount, handler.getClass().getCanonicalName(), handler);
        }

        final var handlerMethod = handler instanceof HandlerMethod ? (HandlerMethod) handler : null;

        if (handlerMethod == null) {
            return true;
        }

        final var tokenized = handlerMethod.getMethod().getDeclaredAnnotation(Tokenized.class);

        if (tokenized == null) {
            return true;
        }

        final var token = trimToNull(request.getHeader("TOKEN"));

        final var isAllowed = userService.getUserInfo(token)
                .map(userInfo -> checkUserAllowed(tokenized, userInfo))
                .orElse(false);

        if (!isAllowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        updateToken(tokenized, token);

        return true;
    }

    private boolean checkUserAllowed(Tokenized tokenized, UserService.UserInfo userInfo) {

        if (tokenized == null || tokenized.rrs().strip().isBlank()) {
            return true;
        } else if (userInfo == null) {
            return false;
        } else if (!userInfo.isTokenTimeValid()) {
            return false;
        }

        final var tokenizedRights = getTokenizedRights(tokenized);

        if (tokenizedRights.isEmpty()) {
            return true;
        }

        for (var neededRight : tokenizedRights) {
            for (var userRight : userInfo.getRrs()) {
                if (isBlank(userRight)) {
                    continue;
                } else if (userRight.strip().toUpperCase(Locale.ROOT).equals(neededRight)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        userService.setCurrentToken(null);
    }

    private Set<String> getTokenizedRights(Tokenized tokenized) {

        if (tokenized == null || tokenized.rrs().strip().isBlank()) {
            return Collections.emptySet();
        } else if (tokenizedRights.containsKey(tokenized)) {
            return tokenizedRights.get(tokenized);
        }

        final var rights = new HashSet<String>();
        for (String s : tokenized.rrs().toUpperCase(Locale.ROOT).split(",", -1)) {

            s = s.strip();

            if (s.isBlank()) {
                continue;
            }

            rights.add(s.toUpperCase(Locale.ROOT));
        }

        tokenizedRights.put(tokenized, rights);
        return rights;
    }

    private void updateToken(Tokenized tokenized, String token) {

        userService.setCurrentToken(token);

        if (tokenized == null) {
            // nothing further to do
            return;
        }

        final var userInfo = this.userService.getUserInfo(token);

        if (!userInfo.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        userInfo.get().updateLastAccess();

    }
}
