package com.spring.boot.server.controller;

import com.spring.boot.server.model.ServerInfo;
import com.spring.boot.server.service.FileService;
import com.spring.boot.server.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProcessController {

    @Autowired
    private final ProcessService processService;
    @Autowired
    private final FileService fileService;

    @GetMapping(value = "/service")
    @ResponseStatus(HttpStatus.OK)
    public String starter(Model model) {
        model.addAttribute("uploadedServers", fileService.getUploadedServers());
        model.addAttribute("serverInfo", new ServerInfo());
        return "startServer";
    }

    @PostMapping(value = "/start-server")
    @ResponseStatus(HttpStatus.OK)
    public String startServer(@RequestBody @ModelAttribute ServerInfo serverInfo, Model model)
            throws IOException, ParserConfigurationException, SAXException {
        model.addAttribute("serverInfo", serverInfo);
        List<ServerInfo> uploadedServers = fileService.getUploadedServers();
        for (ServerInfo server : uploadedServers) {
            if (server.getJarDir().equals(serverInfo.getJarDir())) {
                processService.starter(server);

            }
        }
        model.addAttribute("runningServers", processService.servers);
        return "listRunningServers";
    }

    @GetMapping(value = "/get-running-servers")
    @ResponseStatus(HttpStatus.OK)
    public String getRunningServers(Model model) {
        model.addAttribute("runningServers", processService.servers);
        return "listRunningServers";
    }

    @GetMapping(value = "/stop-server/{pid}")
    @ResponseStatus(HttpStatus.OK)
    public String stopServer(@PathVariable Long pid, Model model) {
        processService.kill(pid);
        model.addAttribute("runningServers", processService.servers);
        return "listRunningServers";
    }

    @GetMapping(value = "/get-info/{pid}")
    @ResponseStatus(HttpStatus.OK)
    public String getInfo(@PathVariable Long pid, Model model) {
        ServerInfo serverInfo = processService.getInfo(pid);
        model.addAttribute("serverInfo", serverInfo);
        return "info";
    }
}
