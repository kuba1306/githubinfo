package com.atipera.githubinfo.service;

import com.atipera.githubinfo.errorHandler.UserNotFoundException;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.info.dto.BranchDto;
import com.atipera.githubinfo.webclient.info.dto.webclient.info.GithubClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubService {

  private final GithubClient githubClient;

  public ResponseEntity<Object> getUserRepositories(String username) {
    try {
      List<Repo> nonForkRepos = githubClient.getUserRepositories(username)
          .stream()
          .filter(repo -> !repo.isFork())
          .collect(Collectors.toList());

      for (Repo repo : nonForkRepos) {
        List<BranchDto> branches = githubClient.getBranchesForRepo(username, repo.getName());
        repo.setBranches(branches);
      }

      return ResponseEntity.ok(nonForkRepos);
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      log.error("Error occurred while fetching user repositories", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
  }
}