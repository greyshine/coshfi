package de.greyshine.coffeeshopfinder.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LocationCrudService extends CrudService<Location> {

    public LocationCrudService() {
        super(Location.class);
    }
}
