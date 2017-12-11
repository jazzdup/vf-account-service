package integrationtests;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.model.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@WebAppConfiguration
public class AccountValidationIT {

    private MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON_UTF8.getType(),
                    MediaType.APPLICATION_JSON_UTF8.getSubtype());

    private MockMvc mockMvc;

    @Autowired
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void pathNotFound() throws Exception {
        final String accountId = new Random().nextInt() + "";
        mockMvc.perform(post("/account/" + accountId + "/validation")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void shouldValidateAccountAndReturnOK() throws Exception {
        final String accountId = new Random().nextInt() + "";
        String accountJson = json(new Account.Builder().locale(Locale.UK).accountId(accountId)
                .build());

        mockMvc.perform(post("/accounts/" + accountId + "/validation")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON_UTF8, outputMessage);
        return outputMessage.getBodyAsString();
    }

}
