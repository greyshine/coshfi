package de.greyshine.coffeeshopfinder.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

@Slf4j
public class ValidationServiceTests {

    @Test
    public void testLoginPattern() {

        String login = "a";
        Assert.isTrue( !ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "aa";
        Assert.isTrue( ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "aaa";
        Assert.isTrue( ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "a-a";
        Assert.isTrue( ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "0-7";
        Assert.isTrue( ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "-a";
        Assert.isTrue( !ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "a-";
        Assert.isTrue( !ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "-aa-";
        Assert.isTrue( !ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "Èä+0éa";
        Assert.isTrue( ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "Èä+/$+*_-0éa";
        Assert.isTrue( !ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "Èä+-_*0éa";
        Assert.isTrue( ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );

        login = "Èä+-_*0é a";
        Assert.isTrue( !ValidationService.LOGIN_PATTERN.matcher( login ).matches(), "Illegal match: "+ login );
    }

    @Test
    public void testUserPattern() {

        String user = "Èä+-_0é a";
        Assert.isTrue( ValidationService.USER_PATTERN.matcher( user ).matches(), "Illegal match: "+ user );
    }


}
