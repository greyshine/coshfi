package de.greyshine.coffeeshopfinder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CoffeeShopFinder {

    public static void main(String[] args) {
        SpringApplication.run( CoffeeShopFinder.class, args );
    }

}