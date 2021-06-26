package com.server.controller;

import com.server.model.RequestModel;
import com.server.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

//TODO: is it?
@Deprecated
@Controller
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping(value = "/get-all-requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestModel> getAllRequests() {
         return requestService.getAllRequests();
    }
}
