package com.atipera.githubinfo.errorHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CustomErrorResponse {
  private int status;
  private String message;
  }