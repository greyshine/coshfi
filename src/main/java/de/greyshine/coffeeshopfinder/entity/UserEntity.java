package de.greyshine.coffeeshopfinder.entity;

import de.greyshine.coffeeshopfinder.service.RightsAndRoles;
import de.greyshine.coffeeshopfinder.utils.Utils;
import de.greyshine.json.crud.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserEntity extends Entity {

    @NotBlank(message = "login is mandatory")
    private String login;

    private final List<String> address = new ArrayList<>();

    @NotBlank(message = "password is mandatory")
    public String password;

    public String confirmationCode;

    public int badlogins;

    private String name;
    private LocalDateTime lastLogin;
    private String contactPerson;
    private String phone;
    @NotBlank(message = "email is mandatory")
    private String email;

    private String language;

    private final Set<String> rightsAndRoles = new HashSet<>();

    @Valid
    public final List<Location> locations = new ArrayList<>(1);

    public void increaseBadLogins() {
        badlogins++;
    }

    {
        addRightAndRole(RightsAndRoles.RIGHT_DEFAULT);
    }

    public UserEntity addRightAndRole(String... rightOrRoles) {

        if (rightOrRoles == null) {
            return this;
        }

        for (String rr : rightOrRoles) {
            if (isBlank(rr)) {
                continue;
            }
            this.rightsAndRoles.add(rr.strip());
        }

        return this;
    }

    public void setPassword(String password, boolean hash) {
        setPassword(!hash ? password : Utils.toHashPassword(password));
    }

    public UserEntity addAddressLines(String... lines) {

        lines = Utils.getDefault(lines, () -> new String[0]);

        this.address.clear();
        Arrays.asList(lines).forEach(line -> {
            if (isBlank(line)) {
                return;
            }
            this.address.add(line);
        });

        return this;
    }

    public UserEntity addAddressLines(List<String> addressLines) {
        Assert.notNull(addressLines, "AddressLines must not be null");
        return this.addAddressLines(addressLines.toArray(new String[addressLines.size()]));
    }

    public List<String> getAddress() {
        final var lines = new ArrayList<String>();
        this.address.forEach(lines::add);
        return lines;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
}
