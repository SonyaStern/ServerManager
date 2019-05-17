package com.spring.boot.server.controller;

import com.spring.boot.server.model.ServerInfo;
import com.spring.boot.server.zip_reader.FileService;
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

@Controller
@RequiredArgsConstructor
public class FileController {

  @Autowired
  private final FileService fileService;

  @PostMapping("/upload-file")
  @ResponseStatus(HttpStatus.OK)
  public String uploadFile(
          @RequestParam MultipartFile file, Model model) {
    fileService.upload(file);
      model.addAttribute("uploadedServers", fileService.getUploadedServers());
      model.addAttribute("serverInfo", new ServerInfo());
      return "startServer";
  }

  @GetMapping("/upload")
  @ResponseStatus(HttpStatus.OK)
  public String upload() {
    return "upload";
  }
}
