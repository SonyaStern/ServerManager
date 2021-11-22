package com.server.controller;

import com.server.service.MonitoringService;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;

    @GetMapping(value = "/get-cpu-usage")
    @ResponseStatus(HttpStatus.OK)
    public float getCpuUsage() throws InterruptedException {
        return monitoringService.getCpuUsagePercent();
    }

    @GetMapping(value = "/get-cpu-history")
    @ResponseStatus(HttpStatus.OK)
    public SortedMap<String, Float> getCpuHistory() {
        return monitoringService.getCpuHistory();
    }

    @GetMapping(value = "/get-memory-usage")
    @ResponseStatus(HttpStatus.OK)
    public float getMemoryUsage() {
        return monitoringService.getMemoryUsagePercent();
    }

    @GetMapping(value = "/get-memory-history")
    @ResponseStatus(HttpStatus.OK)
    public SortedMap<String, Float> getMemoryHistory() {
        return monitoringService.getMemoryHistory();
    }
}
