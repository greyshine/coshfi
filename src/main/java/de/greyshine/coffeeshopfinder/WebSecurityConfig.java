package de.greyshine.coffeeshopfinder;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.Collection;

/**
 * https://www.marcobehler.com/guides/spring-security
 */
@Configuration
//@EnableWebSecurity
@Slf4j
public class WebSecurityConfig /*extends WebSecurityConfigurerAdapter*/ {

    public static boolean allowAll = false;

    //@Override
    protected void configure(/*HttpSecurity httpSecurity*/) throws Exception {

        //httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);

        if (allowAll) {
            //httpSecurity.authorizeRequests().antMatchers("/", "/**","*","**").permitAll();
            return;
        }

    }
}
