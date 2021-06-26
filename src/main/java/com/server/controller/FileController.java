package com.server.controller;

import com.server.service.FileService;
import com.server.model.ServerInfo;
import com.server.service.RequestService;
import com.server.service.ServerService;
import lombok.RequiredArgsConstructor;
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
import java.io.IOException;
import java.net.MalformedURLException;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final ServerService serverService;
    private final RequestService requestService;


    @PostMapping("/upload-file")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public String uploadFile(
            @RequestParam MultipartFile file, Model model)
            throws ParserConfigurationException, SAXException, IOException {
        String message;
        ServerInfo serverInfo = fileService.upload(file);
        model.addAttribute("requests", requestService.getAllRequests());
        model.addAttribute("servers", serverService.getServers());
        if (serverInfo == null) {
            message = "You can upload only zip-files";
            model.addAttribute("message", message);
        } else {
            model.addAttribute("serverInfo", new ServerInfo());
        }
        return "redirect:/main";
    }

    @GetMapping("/upload")
    @ResponseStatus(HttpStatus.OK)
    public String upload() {
        return "main";
    }

    @GetMapping(value = "/get-log-files/{name}/download/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable String name, @PathVariable String fileName, Model model) throws IOException {
        ServerInfo serverInfo = serverService.getServer(name);

        Resource file = fileService.download(serverInfo, fileName);
        model.addAttribute("logFiles", serverInfo.getLogFiles());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
