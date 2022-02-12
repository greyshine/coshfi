package de.greyshine.coffeeshopfinder;


import de.greyshine.coffeeshopfinder.web.TokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * https://www.marcobehler.com/guides/spring-security
 */
@Configuration
//@EnableWebSecurity
@Slf4j
public class WebSecurityConfig implements WebMvcConfigurer /*extends WebSecurityConfigurerAdapter*/ {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    public static boolean allowAll = false;

    //@Override
    protected void configure(/*HttpSecurity httpSecurity*/) throws Exception {

        //httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);

        if (allowAll) {
            //httpSecurity.authorizeRequests().antMatchers("/", "/**","*","**").permitAll();
            return;
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**");
    }


}
