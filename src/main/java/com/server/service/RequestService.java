package com.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.server.model.RequestModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {

    private final ObjectMapper objectMapper;
    private final Environment env;
//    private ObjectWriter writer;
    private final static String FILE_NAME = "requests.json";
    private File file;
    private List<RequestModel> requests;

    @PostConstruct
    public void setUp() throws IOException {
        requests = new ArrayList<>();
        objectMapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.registerModule(new JavaTimeModule());
//        writer = objectMapper.writer();

        file = new File(System.getProperty("user.dir") + File.separator + env
                .getProperty("paths.requests") + File.separator + FILE_NAME);
        if (file.exists() && file.length() > 0) {
            requests.addAll(objectMapper.readValue(file, new TypeReference<List<RequestModel>>() {
            }));
        }
    }

    public RequestModel saveRequest(RequestModel requestModel) throws IOException {
        requests.add(requestModel);
        objectMapper.writeValue(file, requests);
//        writer.writeValue(file, objectMapper.writeValueAsString(requests));

        return requestModel;
    }

    public List<RequestModel> getAllRequests() {
        return requests;
    }

}
