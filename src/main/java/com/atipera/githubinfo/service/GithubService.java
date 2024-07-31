package com.atipera.githubinfo.service;

import com.atipera.githubinfo.errorHandler.UserNotFoundException;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.info.dto.BranchDto;

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

  private final RestTemplate restTemplate;

  public ResponseEntity<Object> getUserRepositories(String username) {
    String reposUrl = "https://api.github.com/users/" + username + "/repos";

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Accept", "application/json");
      ResponseEntity<Repo[]> response = restTemplate.getForEntity(reposUrl, Repo[].class, headers);

      if (response.getStatusCode() == HttpStatus.OK) {
        List<Repo> nonForkRepos = filterNonForks(response.getBody());

        for (Repo repo : nonForkRepos) {
          List<BranchDto> branches = getBranchesForRepo(username, repo.getName());
          repo.setBranches(branches);
        }

        return ResponseEntity.ok(nonForkRepos);
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new UserNotFoundException("User " + username + " not found");
      }
      log.error("Error occurred while fetching user repositories", e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
  }

  private List<BranchDto> getBranchesForRepo(String username, String repoName) {
    String branchesUrl = "https://api.github.com/repos/" + username + "/" + repoName + "/branches";
    try {
      ResponseEntity<BranchDto[]> response = restTemplate.getForEntity(branchesUrl, BranchDto[].class);
      return Arrays.asList(response.getBody());
    } catch (HttpClientErrorException e) {
      log.error("Error fetching branches for repo: " + repoName, e);
      return Collections.emptyList();
    }
  }

  private List<Repo> filterNonForks(Repo[] repositories) {
    return Arrays.stream(repositories)
        .filter(repo -> !repo.isFork())
        .collect(Collectors.toList());
  }
}