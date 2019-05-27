package com.spring.boot.server.service;

import com.spring.boot.server.model.ServerInfo;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class ServerService {

    @Getter
    public ConcurrentSkipListSet<ServerInfo> servers = new ConcurrentSkipListSet<>();

    boolean contains(ServerInfo serverInfo) {

        for (ServerInfo server : servers) {
            if (server.getJarDir().equals(serverInfo.getJarDir())) {
                return true;
            }
        }
        return false;
    }
}
