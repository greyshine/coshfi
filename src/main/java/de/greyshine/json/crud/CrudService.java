package de.greyshine.json.crud;

import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.InputStream;

@Service
@Slf4j
public class CrudService {

    public String create(InputStream is) {
        Assert.notNull(is, "InputStream is null");

        return Utils.throwNotYetImplemented();
    }


}
