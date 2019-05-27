package com.spring.boot.server.controller;

import com.spring.boot.server.model.ServerInfo;
import com.spring.boot.server.service.ProcessService;
import com.spring.boot.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;

@Controller
@RequiredArgsConstructor
public class ProcessController {

    @Autowired
    private final ProcessService processService;
    @Autowired
    private final ServerService serverService;

    @GetMapping(value = "/service")
    @ResponseStatus(HttpStatus.OK)
    public String starter(Model model) {
        model.addAttribute("uploadedServers", serverService.getServers());
        model.addAttribute("serverInfo", new ServerInfo());
        return "startServer";
    }

    @PostMapping(value = "/start-server")
    @ResponseStatus(HttpStatus.OK)
    public String startServer(@RequestBody @ModelAttribute ServerInfo serverInfo, Model model)
            throws IOException, ParserConfigurationException, SAXException {
        model.addAttribute("serverInfo", serverInfo);
        ConcurrentSkipListSet<ServerInfo> uploadedServers = serverService.getServers();
        for (ServerInfo server : uploadedServers) {
            if (server.getJarDir().equals(serverInfo.getJarDir())) {
                processService.starter(server);
            }
        }
        model.addAttribute("servers", serverService.getServers());
        return "listServers";
    }

    @GetMapping(value = "/get-all-servers")
    @ResponseStatus(HttpStatus.OK)
    public String getAllServers(Model model) {
        model.addAttribute("servers", serverService.getServers());
        return "listServers";
    }

    @GetMapping(value = "/stop-server/{pid}")
    @ResponseStatus(HttpStatus.OK)
    public String stopServer(@PathVariable Long pid, Model model) {
        processService.kill(pid);
        model.addAttribute("runningServers", serverService.getServers());
        return "listServers";
    }

    @GetMapping(value = "/get-info/{pid}")
    @ResponseStatus(HttpStatus.OK)
    public String getInfo(@PathVariable Long pid, Model model) {
        ServerInfo serverInfo = processService.getInfo(pid);
        model.addAttribute("serverInfo", serverInfo);
        return "info";
    }
}
