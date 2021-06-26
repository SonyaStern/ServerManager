package com.server.service;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final JavaSysMon javaSysMon;
    private static List<Float> cpuHistory = new LinkedList<>();
    private static List<Long> memoryHistory = new LinkedList<>();
    private static final int listCapacity = 60;

    public List<Float> getCpuHistory() {
        if (cpuHistory.size() > listCapacity) {
            cpuHistory = cpuHistory.subList(cpuHistory.size() - listCapacity, cpuHistory.size());
        }
        log.info("CPU history size: {}", cpuHistory.size());
        return cpuHistory;
    }

    public List<Long> getMemoryHistory() {
        if (memoryHistory.size() > listCapacity) {
            memoryHistory = memoryHistory.subList(memoryHistory.size() - listCapacity, memoryHistory.size());
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
        cpuHistory.add(cpuUsage1 * 100);
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
