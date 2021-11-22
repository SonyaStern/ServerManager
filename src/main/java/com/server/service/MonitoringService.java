package com.server.service;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private static final String CPU_FILE_PATH = "loadHistory/CpuHistory.cvs";
    private static final String MEMORY_FILE_PATH = "loadHistory/MemoryHistory.cvs";
    private static TreeMap<String, Float> cpuHistory = new TreeMap<>();
    private static TreeMap<String, Float> memoryHistory = new TreeMap<>();
    private final JavaSysMon javaSysMon;

    @PostConstruct
    public void setUp() {
        setUpCvsFile(CPU_FILE_PATH, cpuHistory);
        setUpCvsFile(MEMORY_FILE_PATH, memoryHistory);
//        TODO: clean old values in CVS?
    }

    public SortedMap<String, Float> getCpuHistory() {
        while (cpuHistory.size() > LIST_CAPACITY) {
            cpuHistory.pollFirstEntry();
        }
        log.info("CPU history size: {}", cpuHistory.size());
        return cpuHistory;
    }

    public SortedMap<String, Float> getMemoryHistory() {
        while (memoryHistory.size() > LIST_CAPACITY) {
            memoryHistory.pollFirstEntry();
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
        float cpuUsage = cpuTimes.getCpuUsage(cpuTimes1);
//        TODO: change to debug
        log.info("CPU usage: {}", cpuUsage);
        String key = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        float value = cpuUsage * 100;
        writeToCvs(CPU_FILE_PATH, new String[] {key, String.valueOf(value)});
        cpuHistory.put(key, value);
        return cpuUsage * 100;
    }

    public float getMemoryUsagePercent() {
        MemoryStats ram = javaSysMon.physical();
        String key = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        float value = ((ram.getTotalBytes() - ram.getFreeBytes()) * 100) / ram.getTotalBytes();
        log.info("Memory usage: {}", value);
        writeToCvs(MEMORY_FILE_PATH, new String[] {key, String.valueOf(value)});
        memoryHistory.put(key, value);
        return value;
    }


    private void writeToCvs(String path, String[] line) {
        try (CSVWriter csvWriter = new CSVWriter(
                new FileWriter(path, true))) {
            csvWriter.writeNext(line);
        } catch (IOException e) {
            log.error("Could not write to the CVS file");
        }
    }

    private void setUpCvsFile(String path, SortedMap<String, Float> history) {
        if (!new File(path).exists()) {
            writeToCvs(path, new String[] {"Time", "Load"});
        } else {
            try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(path))
                    .withSkipLines(1)
                    .build()) {
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    history.put(line[0], Float.valueOf(line[1]));
                }
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }
        }
    }
}
