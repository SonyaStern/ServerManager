package com.server.service;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final JavaSysMon javaSysMon;
    private static SortedMap<LocalDateTime, Float> cpuHistory = new TreeMap<>();
    private static List<Long> memoryHistory = new LinkedList<>();
    private static final int LIST_CAPACITY = 60;

    public SortedMap<LocalDateTime, Float> getCpuHistory() {
//        if (cpuHistory.size() > LIST_CAPACITY) {
//            cpuHistory = cpuHistory.subMap(cpuHistory.size() - LIST_CAPACITY, cpuHistory.size());
//        }
        log.info("CPU history size: {}", cpuHistory.size());
        return cpuHistory;
    }

    public List<Long> getMemoryHistory() {
        if (memoryHistory.size() > LIST_CAPACITY) {
            memoryHistory = memoryHistory.subList(memoryHistory.size() - LIST_CAPACITY, memoryHistory.size());
        }
        log.info("Memory history size: {}", memoryHistory.size());
        return memoryHistory;
    }

    public float getCpuUsagePercent() throws InterruptedException {
        CpuTimes cpuTimes = javaSysMon.cpuTimes();
        Thread.sleep(500);
        CpuTimes cpuTimes1 = javaSysMon.cpuTimes();
        log.info("Number of CPUs: {}", javaSysMon.numCpus());
        log.info("CPU frequency: {} MHz", javaSysMon.cpuFrequencyInHz() / 1000000L);
//        TODO: check 2 CPU
        float cpuUsage = cpuTimes.getCpuUsage(cpuTimes);
        float cpuUsage1 = cpuTimes.getCpuUsage(cpuTimes1);
//        TODO: change to debug
        log.info("CPU usage: {}", cpuUsage);
        log.info("CPU usage: {}", cpuUsage1);
        cpuHistory.put(LocalDateTime.now(), cpuUsage1 * 100);
        return cpuUsage1 * 100;
    }

    public long getMemoryUsagePercent() {
        MemoryStats swap = javaSysMon.swap();
        MemoryStats ram = javaSysMon.physical();
        log.info("SWAP: {}", swap);
        log.info("RAM: {}", ram);
        log.info("Used RAM: {}", (ram.getTotalBytes() - ram.getFreeBytes()) / 1048576L);
//        TODO: refactor duplication
        log.info("Memory usage: {}", ((ram.getTotalBytes() - ram.getFreeBytes()) * 100) / ram.getTotalBytes());
        memoryHistory.add(((ram.getTotalBytes() - ram.getFreeBytes()) * 100) / ram.getTotalBytes());
        return ((ram.getTotalBytes() - ram.getFreeBytes()) * 100) / ram.getTotalBytes();
    }

}
