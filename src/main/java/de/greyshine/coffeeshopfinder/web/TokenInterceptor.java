package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.entity.UserCrudService;
import de.greyshine.coffeeshopfinder.web.annotation.Token;
import de.greyshine.coffeeshopfinder.web.annotation.Tokenized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.apache.commons.lang3.StringUtils.trimToNull;

@Configuration
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserCrudService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.debug("request {}, {}", handler.getClass().getCanonicalName(), handler);

        final HandlerMethod handlerMethod = handler instanceof HandlerMethod ? (HandlerMethod) handler : null;

        if (handlerMethod == null) {
            return true;
        }

        final Method method = handlerMethod.getMethod();
        //log.info("{}", method);

        final String token = trimToNull(request.getHeader("TOKEN"));
        updateToken(method.getDeclaredAnnotation(Tokenized.class), token);

        // Check @Token parameter
        int i = -1;
        for (Annotation[] annotations : method.getParameterAnnotations()) {

            i++;

            for (int i2 = 0; i2 < annotations.length; i2++) {

                // log.info( "[{}] {} {}", i, i2, annotations[i2] );

                if (annotations[i2].annotationType() == Token.class && String.class.isAssignableFrom(method.getParameterTypes()[i])) {

                    // Scheint nicht zu gehen, evtl. mit AspectOrientatedProgramming versuchen...
                    final MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
                    //log.info( "Hit [{}][{}], token={}, mp={}", i, i2, token, methodParameters[i].getParameter() );
                }

            }
        }

        return true;
    }

    private void updateToken(Tokenized tokenized, String token) throws Exception {

        if (tokenized == null) {
            // nothing to do
            return;
        }

        final UserCrudService.TokenInfo tokenInfo = this.userService.getTokenInfo(token);

        if (tokenInfo == null || !tokenInfo.isAccessible()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        tokenInfo.updateLastAccess();
    }
}
