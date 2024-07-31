package com.atipera.githubinfo.webclient.info.dto.webclient.info;

import com.atipera.githubinfo.errorHandler.CustomErrorResponse;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.info.dto.BranchDto;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubClient {

  private final RestTemplate restTemplate;

  @Autowired
  public GithubClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ResponseEntity<Object> getUserRepositories(String username) {
    String reposUrl = "https://api.github.com/users/" + username + "/repos";
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Accept", "application/json");
      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<Repo[]> response = restTemplate.exchange(reposUrl, HttpMethod.GET, entity, Repo[].class);

      List<Repo> repoList = Arrays.asList(response.getBody());
      return ResponseEntity.ok(repoList);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
      } else {
        CustomErrorResponse errorResponse = new CustomErrorResponse(e.getStatusCode().value(), "An error occurred");
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
      }
    }
  }

  public ResponseEntity<Object> getBranchesForRepo(String username, String repoName) {
    String branchesUrl = "https://api.github.com/repos/" + username + "/" + repoName + "/branches";
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Accept", "application/json");
      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<BranchDto[]> response = restTemplate.exchange(branchesUrl, HttpMethod.GET, entity, BranchDto[].class);

      List<BranchDto> branchList = Arrays.asList(response.getBody());
      return ResponseEntity.ok(branchList);
    } catch (HttpClientErrorException e) {
      CustomErrorResponse errorResponse = new CustomErrorResponse(e.getStatusCode().value(), "Error fetching branches");
      return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }
  }
}