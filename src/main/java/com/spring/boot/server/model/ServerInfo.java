package com.spring.boot.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerInfo {

  String name;
  String port;
  String userLogin;
  String userPass;
  Process process;

}
