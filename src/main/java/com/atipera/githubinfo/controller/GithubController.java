package com.atipera.githubinfo.controller;

import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.service.GithubService;

import java.util.List;

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
  public List<Repo> getUserRepositories(
      @PathVariable String username,
      @RequestParam(value = "accept", defaultValue = "application/json") String accept) {

    if (!"application/json".equals(accept)) {
      // Return an empty list or throw an exception
      throw new IllegalArgumentException("Accept header must be 'application/json'");
    }

    return githubService.getUserRepositories(username);
  }
}
