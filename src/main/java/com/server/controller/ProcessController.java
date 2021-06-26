package com.server.controller;

import com.server.model.ServerInfo;
import com.server.service.ProcessService;
import com.server.service.RequestService;
import com.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Controller
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;
    private final ServerService serverService;
    private final RequestService requestService;


    @GetMapping(value = "/start-server/{name}")
    @ResponseStatus(HttpStatus.SEE_OTHER)
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
        model.addAttribute("servers", servers);
        model.addAttribute("requests", requestService.getAllRequests());
        return "redirect:/main";
    }

    @GetMapping(value = "/main")
    @ResponseStatus(HttpStatus.OK)
    public String getAllServers(Model model) {
        model.addAttribute("servers", serverService.getServers());
        model.addAttribute("requests", requestService.getAllRequests());
        return "main";
    }

    @GetMapping(value = "/stop-server/{pid}")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public String stopServer(@PathVariable Long pid, Model model) throws IOException {
        processService.kill(pid);
        model.addAttribute("servers", serverService.getServers());
        model.addAttribute("requests", requestService.getAllRequests());
        return "redirect:/main";
    }

    @GetMapping(value = "/get-info/{name}")
    @ResponseStatus(HttpStatus.OK)
    public String getInfo(@PathVariable String name, Model model) {
        ServerInfo serverInfo = serverService.getServer(name);
        model.addAttribute("serverInfo", serverInfo);
        return "info";
    }

    @GetMapping(value = "/get-log-files")
    @ResponseStatus(HttpStatus.OK)
    public String getAllLogFiles(Model model) {
        Map<File, String> logFiles = new ConcurrentSkipListMap<>();
        for (ServerInfo serverInfo : serverService.getServers()) {
            logFiles.putAll(serverInfo.getLogFiles());
        }
        model.addAttribute("logFiles", logFiles);
        return "listLogFiles";
    }

    @GetMapping(value = "/get-log-files/{name}")
    @ResponseStatus(HttpStatus.OK)
    public String getLogFiles(@PathVariable String name, Model model) {
        ServerInfo serverInfo = serverService.getServer(name);
        model.addAttribute("logFiles", serverInfo.getLogFiles());
        return "listLogFiles";
    }


}
