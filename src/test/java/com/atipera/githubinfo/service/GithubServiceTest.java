package com.atipera.githubinfo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.atipera.githubinfo.errorHandler.UserNotFoundException;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.info.dto.BranchDto;
import com.atipera.githubinfo.webclient.info.dto.webclient.info.GithubClient;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GithubServiceTest {

  @Mock
  private GithubClient githubClient;

  @InjectMocks
  private GithubService githubService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetUserRepositories_Success() {
    String username = "testUser";

    Repo repo1 = new Repo();
    repo1.setName("repo1");
    repo1.setFork(false);

    Repo repo2 = new Repo();
    repo2.setName("repo2");
    repo2.setFork(true);

    BranchDto branchDto1 = new BranchDto();
    BranchDto branchDto2 = new BranchDto();

    when(githubClient.getUserRepositories(username)).thenReturn(Arrays.asList(repo1, repo2));
    when(githubClient.getBranchesForRepo(username, "repo1")).thenReturn(Arrays.asList(branchDto1, branchDto2));

    ResponseEntity<Object> response = githubService.getUserRepositories(username);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<Repo> repos = (List<Repo>) response.getBody();
    assertNotNull(repos);
    assertEquals(1, repos.size());
    assertEquals("repo1", repos.get(0).getName());
    assertEquals(2, repos.get(0).getBranches().size());
  }

  @Test
  void testGetUserRepositories_UserNotFound() {
    String username = "unknownUser";

    when(githubClient.getUserRepositories(username)).thenThrow(new UserNotFoundException("User not found"));

    ResponseEntity<Object> response = githubService.getUserRepositories(username);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User not found", response.getBody());
  }

  @Test
  void testGetUserRepositories_InternalServerError() {
    String username = "testUser";

    when(githubClient.getUserRepositories(username)).thenThrow(new RuntimeException("Internal error"));

    ResponseEntity<Object> response = githubService.getUserRepositories(username);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal server error", response.getBody());
  }
}