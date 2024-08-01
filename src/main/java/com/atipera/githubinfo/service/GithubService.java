package com.atipera.githubinfo.service;

import com.atipera.githubinfo.errorHandler.GithubClientException;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.GithubClient;
import com.atipera.githubinfo.webclient.dto.BranchDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubService {

  private final GithubClient githubClient;

  public List<Repo> getUserRepositories(String username) {
    try {
      List<Repo> repos = githubClient.getUserRepositories(username);

      List<Repo> nonForkRepos = repos.stream()
          .filter(repo -> !repo.isFork())
          .collect(Collectors.toList());

      List<CompletableFuture<Void>> futures = nonForkRepos.stream().map(repo ->
          CompletableFuture.runAsync(() -> {
            List<BranchDto> branches = githubClient.getBranchesForRepo(username, repo.getName());
            repo.setBranches(branches);
          })
      ).collect(Collectors.toList());

      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
      return nonForkRepos;
    } catch (GithubClientException e) {
      log.error("Error fetching repositories: {}", e.getMessage());
      throw e;
    }
  }
}
