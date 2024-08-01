package com.atipera.githubinfo.errorHandler;


public class GithubClientException extends RuntimeException {
  private final int statusCode;

  public GithubClientException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}