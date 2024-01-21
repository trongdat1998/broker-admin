package io.bhex.broker.admin.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.config
 * @Author: ming.xu
 * @CreateDate: 2019/2/15 5:21 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                //.enable(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false)
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)

                .registerModule(new SimpleModule()
                        // BigDecimal 设置固定的scale 并写成string
                        .addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
                            @Override
                            public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
                                    throws IOException {
                                gen.writeString(value.stripTrailingZeros().toPlainString());
                            }
                        })
                        // long to string
                        .addSerializer(Long.class, ToStringSerializer.instance)
                        .addSerializer(Long.TYPE, ToStringSerializer.instance));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        converters.add(converter);
        converters.add(new StringHttpMessageConverter());
    }
}
