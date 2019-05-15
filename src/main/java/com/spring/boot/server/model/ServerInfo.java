package com.spring.boot.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ServerInfo {

    String name;
    String port;
    String userLogin;
    String userPass;
    Process process;
    String jarDir;
    String rscDir;
    String libDir;

    public ServerInfo(String name, String port, String userLogin, String userPass,
            Process process) {
        this.name = name;
        this.port = port;
        this.userLogin = userLogin;
        this.userPass = userPass;
        this.process = process;
    }

}
