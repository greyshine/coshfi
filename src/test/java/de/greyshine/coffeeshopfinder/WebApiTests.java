package de.greyshine.coffeeshopfinder;

import de.greyshine.coffeeshopfinder.web.MapController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers=MapController.class)
@Slf4j
public class WebApiTests {

    static {
        WebSecurityConfig.allowAll = true;
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test() throws Exception {

        this.mockMvc.perform(

                post("/test")

        ).andDo((response) -> {


            log.info("resp[{}] {}", response.getResponse().getStatus(), response.getResponse().getContentAsString());

        }).andExpect(
            status().isOk()
        );

    }

}
