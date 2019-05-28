package com.spring.boot.server.controller;

import com.spring.boot.server.model.ServerInfo;
import com.spring.boot.server.service.ProcessService;
import com.spring.boot.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;

@Controller
@RequiredArgsConstructor
public class ProcessController {

    @Autowired
    private final ProcessService processService;
    @Autowired
    private final ServerService serverService;

//    @GetMapping(value = "/service")
//    @ResponseStatus(HttpStatus.OK)
//    public String starter(Model model) {
//        model.addAttribute("servers", serverService.getServers());
//        model.addAttribute("serverInfo", new ServerInfo());
//        return "startServer";
//    }

    @GetMapping(value = "/start-server/{name}")
    @ResponseStatus(HttpStatus.OK)
    public String startServer(@PathVariable String name, Model model)
            throws IOException {
        ConcurrentSkipListSet<ServerInfo> servers = serverService.getServers();
        System.out.println("name " + name);
        for (ServerInfo server : servers) {
            if (server.getName().equals(name)) {
                System.out.println("jar " + server.getJarDir());
                processService.starter(server);
            } else {
                System.out.println(2);
            }
        }
//        model.addAttribute("serverInfo", serverInfo);
        model.addAttribute("servers", servers);
        return "listServers";
    }

    @GetMapping(value = "/get-all-servers")
    @ResponseStatus(HttpStatus.OK)
    public String getAllServers(Model model) {
//        model.addAttribute("serverInfo", new ServerInfo());
        model.addAttribute("servers", serverService.getServers());
        return "listServers";
    }

    @GetMapping(value = "/stop-server/{pid}")
    @ResponseStatus(HttpStatus.OK)
    public String stopServer(@PathVariable Long pid, Model model) {
        processService.kill(pid);
        model.addAttribute("servers", serverService.getServers());
        return "listServers";
    }

    @GetMapping(value = "/get-info/{name}")
    @ResponseStatus(HttpStatus.OK)
    public String getInfo(@PathVariable String name, Model model) {
        ServerInfo serverInfo = serverService.getServer(name);
        model.addAttribute("serverInfo", serverInfo);
        return "info";
    }

    @GetMapping(value = "/get-log-files/{name}")
    @ResponseStatus(HttpStatus.OK)
    public String getLogFiles(@PathVariable String name, Model model) {
        ServerInfo serverInfo = serverService.getServer(name);
        model.addAttribute("logFiles", serverInfo.getLogFiles());
        return "listLogFiles";
    }


}
