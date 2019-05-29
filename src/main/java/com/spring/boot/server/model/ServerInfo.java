package com.spring.boot.server.model;

import java.io.File;
import java.util.concurrent.ConcurrentSkipListMap;
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
    ConcurrentSkipListMap<File, String> logFiles = new ConcurrentSkipListMap<>();

//    public ServerInfo(String name, String port, String userLogin, String userPass) {
//        this.name = name;
//        this.port = port;
//        this.userLogin = userLogin;
//        this.userPass = userPass;
//    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

}
