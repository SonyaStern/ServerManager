package com.spring.boot.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ServerInfo implements Comparable {

    Long pid;
    String name;
    String port;
    String userLogin;
    String userPass;
    Process process;
    String jarDir;
    String rscDir;
    String libDir;

    public ServerInfo(String name, String port, String userLogin, String userPass) {
        this.name = name;
        this.port = port;
        this.userLogin = userLogin;
        this.userPass = userPass;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public int compareTo(ServerInfo serverInfo) {
        return serverInfo.getJarDir().equals(this.getJarDir()) ? 1 : 0;
    }
}
