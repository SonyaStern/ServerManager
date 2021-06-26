package com.server.service;

import com.server.model.RequestModel;
import com.server.model.ServerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ServerService serverService;
    private final RequestService requestService;
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    public Process starter(ServerInfo serverInfo)
            throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Process server = new ProcessBuilder(
                "java", "-cp", "\"",
                serverInfo.getLibDir(), ";", serverInfo.getRscDir(), "\"",
                "-jar",
                serverInfo.getJarDir()).start();

        serverInfo.setProcess(server);
        serverInfo.setPid(server.pid());
        new Thread(() -> {
            try {
                InputStream in = server.getErrorStream();
                byte[] buffer = new byte[1024];
                File file = new File(
                        "logs/" + serverInfo.getName() + "/" + serverInfo.getName() + "Error_"
                                + ZonedDateTime
                                .now().toEpochSecond() + ".txt");
                writeLogFile(serverInfo, in, buffer, file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                InputStream in = server.getInputStream();
                byte[] buffer = new byte[1024];
                File file = new File(
                        "logs/" + serverInfo.getName() + "/" + serverInfo.getName() + "_"
                                + ZonedDateTime.now().toEpochSecond()
                                + ".txt");
                writeLogFile(serverInfo, in, buffer, file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        stopWatch.stop();
        RequestModel requestModel = new RequestModel(serverInfo, "Server start", stopWatch.getTotalTimeSeconds());
        requestService.saveRequest(requestModel);
        return server;
    }

    private void writeLogFile(ServerInfo serverInfo, InputStream in, byte[] buffer, File file)
            throws IOException {
        int ch;
        serverInfo.getLogFiles().put(file,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        while ((ch = in.read(buffer)) >= 0) {
            out.write(buffer, 0, ch);
        }
        out.close();
        in.close();
    }

    public void kill(Long pid) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ConcurrentSkipListSet<ServerInfo> servers = serverService.getServers();
        for (ServerInfo serverInfo : servers) {
            if (serverInfo.getPid().equals(pid)) {
                serverInfo.getProcess().destroy();
                servers.remove(serverInfo);
                serverInfo.setPid(null);
                servers.add(serverInfo);
                stopWatch.stop();
                RequestModel requestModel = new RequestModel(serverInfo, "Server named " + serverInfo.getName() + " was stopped", stopWatch.getTotalTimeSeconds());
                requestService.saveRequest(requestModel);
                logger.info("Server named {} was stopped", serverInfo.getName());
            }
        }
    }


}
