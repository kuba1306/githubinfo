package com.atipera.githubinfo.webclient.info.dto.webclient.info;

import com.atipera.githubinfo.errorHandler.UserNotFoundException;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.info.dto.BranchDto;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GithubClient {

  private final RestTemplate restTemplate;

  private static final String BASE_URL = "https://api.github.com";

  public List<Repo> getUserRepositories(String username) {
    String reposUrl = String.format("%s/users/%s/repos", BASE_URL, username);
    try {
      ResponseEntity<Repo[]> response = restTemplate.getForEntity(reposUrl, Repo[].class, createJsonHeaders());
      return Arrays.asList(response.getBody());
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new UserNotFoundException("User " + username + " not found");
      }
      throw e;
    }
  }

  public List<BranchDto> getBranchesForRepo(String username, String repoName) {
    String branchesUrl = String.format("%s/repos/%s/%s/branches", BASE_URL, username, repoName);
    try {
      ResponseEntity<BranchDto[]> response = restTemplate.getForEntity(branchesUrl, BranchDto[].class, createJsonHeaders());
      return Arrays.asList(response.getBody());
    } catch (HttpClientErrorException e) {
      // Optional: Handle specific cases for branch fetching errors
      throw e;
    }
  }

  private HttpHeaders createJsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json");
    return headers;
  }
}