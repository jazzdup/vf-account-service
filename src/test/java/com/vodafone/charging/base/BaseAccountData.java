package com.vodafone.charging.base;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = AccountServiceApplication.class)
//public class BaseAccountData {
//
//    @Autowired
//    private HttpMessageConverter mappingJackson2HttpMessageConverter;
//
//    protected String toJson(Object o) throws IOException {
//        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
//        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON_UTF8, outputMessage);
//        return outputMessage.getBodyAsString();
//    }
//
//    protected Object fromJson(Class<?> clazz, String json) throws IOException {
//        MockHttpInputMessage input = new MockHttpInputMessage(json.getBytes());
//        return mappingJackson2HttpMessageConverter.read(clazz, input);
//    }
//
//}
