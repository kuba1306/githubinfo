package com.atipera.githubinfo.service;

import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.dto.BranchDto;
import com.atipera.githubinfo.webclient.GithubClient;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubService {

  private final GithubClient githubClient;

  public ResponseEntity<Object> getUserRepositories(String username) {
    ResponseEntity<Object> responseEntity = githubClient.getUserRepositories(username);
    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      List<Repo> nonForkRepos = ((List<Repo>) responseEntity.getBody())
          .stream()
          .filter(repo -> !repo.isFork())
          .collect(Collectors.toList());

      for (Repo repo : nonForkRepos) {
        ResponseEntity<Object> branchesResponse = githubClient.getBranchesForRepo(username, repo.getName());
        if (branchesResponse.getStatusCode() == HttpStatus.OK) {
          List<BranchDto> branches = (List<BranchDto>) branchesResponse.getBody();
          repo.setBranches(branches);
        } else {
          return branchesResponse;
        }
      }
      return ResponseEntity.ok(nonForkRepos);
    } else {
      return responseEntity;
    }
  }
}