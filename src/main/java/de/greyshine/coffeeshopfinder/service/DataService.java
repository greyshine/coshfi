package de.greyshine.coffeeshopfinder.service;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.greyshine.coffeeshopfinder.entity.RegistrationEntity;
import de.greyshine.coffeeshopfinder.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DataService {

    public static final Object SYNC = new Object();

    private final List<RegistrationEntity> registrations = new ArrayList<>();
    private final List<UserEntity> users = new ArrayList<>();

    public boolean isLogin(String login) {

        Assert.isTrue( isNotBlank(login), "Login is blank" );

        // DO check registration candiates as well as users

        throw new UnsupportedOperationException("TODO implement");
    }

    public boolean isUserName(String name) {

// DO check registration candiates as well as users
        throw new UnsupportedOperationException("TODO implement");
    }
}
