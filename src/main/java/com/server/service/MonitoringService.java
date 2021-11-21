package com.server.service;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringService {

    private static final int LIST_CAPACITY = 60;
    private static SortedMap<String, Float> cpuHistory = new TreeMap<>();
    private static List<Long> memoryHistory = new LinkedList<>();
    private final JavaSysMon javaSysMon;

    @PostConstruct
    public void setUp() {
        if (!new File("loadHistory/CpuHistory.cvs").exists()) {
            writeToCvs(new String[] {"Time", "Load"});
        }
//        TODO: load from CVS
    }

    public SortedMap<String, Float> getCpuHistory() {
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
//        writeToCvs(new String[] {LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
//                String.valueOf(cpuUsage1 * 100)});
        cpuHistory.put(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), cpuUsage1 * 100);
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


    private void writeToCvs(String[] line) {
        try (CSVWriter csvWriter = new CSVWriter(
                new FileWriter("loadHistory/CpuHistory.cvs", true))) {
            csvWriter.writeNext(line);
        } catch (IOException e) {
            log.error("Could not write to the CVS file");
        }
    }
}
