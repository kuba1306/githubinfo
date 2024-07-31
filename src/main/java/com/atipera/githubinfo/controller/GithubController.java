package com.atipera.githubinfo.controller;

import com.atipera.githubinfo.service.GithubService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GithubController {

  private final GithubService githubService;

  @GetMapping("/{username}")
  public ResponseEntity<Object> getUserRepositories(
      @PathVariable String username,
      @RequestParam(value = "accept", defaultValue = "application/json") String accept) {

    if (!"application/json".equals(accept)) {
      return ResponseEntity.badRequest().body("Accept header must be 'application/json'");
    }

    return githubService.getUserRepositories(username);
  }
}