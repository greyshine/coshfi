package de.greyshine.coffeeshopfinder.service;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ValidationFailure extends Throwable {

    private final Object value;
    private final String info;

    public ValidationFailure(Object value, String info) {

        super(info);

        this.value = value;
        this.info = info;
    }
}

