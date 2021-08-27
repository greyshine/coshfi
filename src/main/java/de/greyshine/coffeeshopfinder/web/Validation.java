package de.greyshine.coffeeshopfinder.web;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Validation {

    private Map<String, List<String>> fieldErrors = new HashMap<>();

    public boolean isError() {
        return !fieldErrors.isEmpty();
    }

}
