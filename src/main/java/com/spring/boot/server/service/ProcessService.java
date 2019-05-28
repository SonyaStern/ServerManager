package com.spring.boot.server.service;

import com.spring.boot.server.model.ServerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
@RequiredArgsConstructor
public class ProcessService {

    @Autowired
    private final ServerService serverService;

    public Process starter(ServerInfo serverInfo)
            throws IOException {

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
                int ch;
                byte[] buffer = new byte[1024];
                File file = new File("logs/" + serverInfo.getName() + "/" + serverInfo.getName() + "Error" + LocalDate
                        .now().toString() + ".txt");
                serverInfo.getLogFiles().add(file);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                while ((ch = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, ch);
                }
                out.close();
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                InputStream in = server.getInputStream();
                int ch;
                byte[] buffer = new byte[1024];
                File file = new File("logs/" + serverInfo.getName() + "/" + serverInfo.getName() + LocalDate.now()
                        .toString() + ".txt");
                serverInfo.getLogFiles().add(file);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                while ((ch = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, ch);
                }
                out.close();
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return server;
    }

    public void kill(Long pid) {
        ConcurrentSkipListSet<ServerInfo> servers = serverService.getServers();
        for (ServerInfo serverInfo : servers) {
            if (serverInfo.getPid().equals(pid)) {
                serverInfo.getProcess().destroy();
                servers.remove(serverInfo);
                serverInfo.setPid(null);
                servers.add(serverInfo);
            }
        }
        System.out.println("Program completed");
    }


}
