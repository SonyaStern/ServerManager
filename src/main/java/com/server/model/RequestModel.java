package com.server.model;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class RequestModel {

    private String user;
    private String serverName;

//    public RequestModel(String user, String serverName, String port, LocalDateTime dateTime, String action, Double duration) {
//        this.user = user;
//        this.serverName = serverName;
//        this.port = port;
//        this.dateTime = dateTime;
//        this.action = action;
//        this.duration = duration;
//    }

    private String port;
    private LocalDateTime dateTime;
    private String action;
    private Double duration;

    public RequestModel(ServerInfo serverInfo, String action, Double duration) {
        this.user = serverInfo.getUserLogin();
        this.serverName = serverInfo.getName();
        this.port = serverInfo.getPort();
        this.dateTime = LocalDateTime.now();
        this.action = action;
        this.duration = duration;
    }
}
