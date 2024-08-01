package com.atipera.githubinfo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.atipera.githubinfo.errorHandler.GithubClientException;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.GithubClient;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class GithubServiceTest {

  private GithubClient githubClient;
  private GithubService githubService;

  @BeforeEach
  public void setUp() {
    githubClient = mock(GithubClient.class);
    githubService = new GithubService(githubClient);
  }

  @Test
  public void testGetUserRepositories_Success() {
    Repo repo = new Repo();
    repo.setName("test-repo");
    repo.setFork(false);
    List<Repo> repos = Collections.singletonList(repo);

    when(githubClient.getUserRepositories("username")).thenReturn(repos);
    when(githubClient.getBranchesForRepo("username", "test-repo")).thenReturn(Collections.emptyList());

    List<Repo> result = githubService.getUserRepositories("username");

    assertEquals(1, result.size());
    assertEquals("test-repo", result.get(0).getName());
  }

  @Test
  public void testGetUserRepositories_ClientException() {
    when(githubClient.getUserRepositories("username")).thenThrow(new GithubClientException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"));

    try {
      githubService.getUserRepositories("username");
    } catch (GithubClientException e) {
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getStatusCode());
      assertEquals("Internal Server Error", e.getMessage());
    }
  }
}