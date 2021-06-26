package com.server.model;

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
public class ServerInfo implements Comparable<ServerInfo> {

    private Long pid;
    private String name;
    private String port;
    private String userLogin;
    private String userPass;
    private Process process;
    private String jarDir;
    private String rscDir;
    private String libDir;
    private ConcurrentSkipListMap<File, String> logFiles = new ConcurrentSkipListMap<>();

//    TODO: rething this
    @Override
    public int compareTo(ServerInfo o) {
        return 0;
    }

}
