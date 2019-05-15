package com.spring.boot.server.controller;

import com.spring.boot.server.zip_reader.FileService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
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
          @RequestParam MultipartFile file)
      throws IOException {
//    ModelAndView modelAndView = new ModelAndView();
//    modelAndView.setViewName("index");
//    modelAndView.addObject("file", file);
//    model.addAttribute("file", file);
    fileService.upload(file);
    return "index";
  }
}
