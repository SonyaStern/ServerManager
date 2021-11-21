package com.server.controller;

import com.server.service.MonitoringService;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedMap;

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
    public long getMemoryUsage() {
        return monitoringService.getMemoryUsagePercent();
    }

    @GetMapping(value = "/get-memory-history")
    @ResponseStatus(HttpStatus.OK)
    public List<Long> getMemoryHistory() {
        return monitoringService.getMemoryHistory();
    }
}
