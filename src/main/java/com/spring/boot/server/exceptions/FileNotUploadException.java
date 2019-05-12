package com.spring.boot.server.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FileNotUploadException extends RuntimeException {

  public FileNotUploadException(String message) {
    super(message);
  }
}
