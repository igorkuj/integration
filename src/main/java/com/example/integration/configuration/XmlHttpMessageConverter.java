package com.example.integration.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

/**
 * Configuration class for  converting {@link com.example.integration.model.XmlMessage} to {@link org.springframework.http.HttpMessage}.
 */
@Configuration
@RequiredArgsConstructor
public class XmlHttpMessageConverter implements HttpMessageConverter<Object> {

    private final XmlMapper xmlMapper;

    @Override
    public boolean canRead(@NonNull Class<?> clazz, org.springframework.http.MediaType mediaType) {
        return mediaType != null && mediaType.includes(MediaType.APPLICATION_XML);
    }

    @Override
    public boolean canWrite(@NonNull Class<?> clazz, org.springframework.http.MediaType mediaType) {
        return mediaType != null && mediaType.includes(MediaType.APPLICATION_XML);
    }

    @Override
    @NonNull
    public Object read(@NonNull Class<?> clazz, @NonNull HttpInputMessage inputMessage) throws IOException {
        Assert.notNull(inputMessage, "HttpInputMessage must not be null");
        InputStream inputStream = inputMessage.getBody();
        return xmlMapper.readValue(inputStream, clazz);
    }

    @Override
    public void write(@NonNull Object t, org.springframework.http.MediaType contentType,
                      HttpOutputMessage outputMessage) throws IOException {
        Assert.notNull(t, "Object must not be null");
        outputMessage.getHeaders().setContentType(contentType);
        xmlMapper.writeValue(outputMessage.getBody(), t);
    }

    @Override
    @NonNull
    public List<MediaType> getSupportedMediaTypes() {
        return List.of(org.springframework.http.MediaType.APPLICATION_XML);
    }
}
