package com.atipera.githubinfo.webclient;

import com.atipera.githubinfo.errorHandler.GithubClientException;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.dto.BranchDto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubClient {

  private static final String BASE_URL = "https://api.github.com/";
  private final RestTemplate restTemplate;

  @Autowired
  public GithubClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Repo> getUserRepositories(String username) {
    return callGetMethod("users/{username}/repos", Repo[].class, username);
  }

  public List<BranchDto> getBranchesForRepo(String username, String repoName) {
    return callGetMethod("repos/{username}/{repoName}/branches", BranchDto[].class, username, repoName);
  }

  private <T> List<T> callGetMethod(String url, Class<T[]> responseType, Object... uriVariables) {
    try {
      ResponseEntity<T[]> response = restTemplate.getForEntity(BASE_URL + url, responseType, uriVariables);
      return List.of(response.getBody());
    } catch (HttpClientErrorException e) {
      throw new GithubClientException(e.getStatusCode().value(), extractErrorMessage(e));
    }
  }

  private String extractErrorMessage(HttpClientErrorException e) {
    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
      return "Not Found";
    }

    return e.getMessage();
  }
}
