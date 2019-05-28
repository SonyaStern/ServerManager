package com.spring.boot.server.controller;

import com.spring.boot.server.model.ServerInfo;
import com.spring.boot.server.service.FileService;
import com.spring.boot.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.net.MalformedURLException;

@Controller
@RequiredArgsConstructor
public class FileController {

    @Autowired
    private final FileService fileService;
    @Autowired
    private final ServerService serverService;

    @PostMapping("/upload-file")
    @ResponseStatus(HttpStatus.OK)
    public String uploadFile(
            @RequestParam MultipartFile file, Model model)
            throws ParserConfigurationException, SAXException {
        String message;
        ServerInfo serverInfo = fileService.upload(file);
        if (serverInfo == null) {
            message = "You can upload only zip-files";
            model.addAttribute("message", message);
            return "upload";
        } else {
            model.addAttribute("servers", serverService.getServers());
            model.addAttribute("serverInfo", new ServerInfo());
            return "listServers";
        }
    }

    @GetMapping("/upload")
    @ResponseStatus(HttpStatus.OK)
    public String upload() {
        return "upload";
    }

    @GetMapping(value = "/get-log-files/{name}/download/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable String name, @PathVariable String fileName, Model model) throws MalformedURLException {
        ServerInfo serverInfo = serverService.getServer(name);

        Resource file = fileService.download(serverInfo, fileName);
        model.addAttribute("logFiles", serverInfo.getLogFiles());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
