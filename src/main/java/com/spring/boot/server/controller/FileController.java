package com.spring.boot.server.controller;

import com.spring.boot.server.model.ServerInfo;
import com.spring.boot.server.service.FileService;
import com.spring.boot.server.service.ServerService;
import javax.xml.parsers.ParserConfigurationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

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
}
