package com.atipera.githubinfo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.atipera.githubinfo.errorHandler.CustomErrorResponse;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.GithubClient;
import com.atipera.githubinfo.webclient.dto.BranchDto;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GithubServiceTest {

  @Mock
  private GithubClient githubClient;

  @InjectMocks
  private GithubService githubService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetUserRepositories_Success() {
    // Given
    Repo repo1 = new Repo();
    repo1.setName("repo1");
    repo1.setFork(false);

    Repo repo2 = new Repo();
    repo2.setName("repo2");
    repo2.setFork(true);

    List<Repo> repoList = List.of(repo1, repo2);
    when(githubClient.getUserRepositories(anyString())).thenReturn(ResponseEntity.ok(repoList));

    BranchDto branch1 = new BranchDto();
    branch1.setName("main");

    List<BranchDto> branches = List.of(branch1);
    when(githubClient.getBranchesForRepo(anyString(), anyString())).thenReturn(ResponseEntity.ok(branches));

    // When
    ResponseEntity<Object> response = githubService.getUserRepositories("testUser");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<Repo> nonForkRepos = (List<Repo>) response.getBody();
    assertEquals(1, nonForkRepos.size());
    assertEquals("repo1", nonForkRepos.get(0).getName());
    assertEquals(branches, nonForkRepos.get(0).getBranches());
  }

  @Test
  public void testGetUserRepositories_UserNotFound() {
    // Given
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found");
    when(githubClient.getUserRepositories(anyString())).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));

    // When
    ResponseEntity<Object> response = githubService.getUserRepositories("unknownUser");

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(errorResponse, response.getBody());
  }

  @Test
  public void testGetUserRepositories_BranchesNotFound() {
    // Given
    Repo repo = new Repo();
    repo.setName("repo1");
    repo.setFork(false);

    List<Repo> repoList = List.of(repo);
    when(githubClient.getUserRepositories(anyString())).thenReturn(ResponseEntity.ok(repoList));

    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Branches not found");
    when(githubClient.getBranchesForRepo(anyString(), anyString())).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));

    // When
    ResponseEntity<Object> response = githubService.getUserRepositories("testUser");

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(errorResponse, response.getBody());
  }

  @Test
  public void testGetUserRepositories_InternalServerError() {
    // Given
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
    when(githubClient.getUserRepositories(anyString())).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));

    // When
    ResponseEntity<Object> response = githubService.getUserRepositories("testUser");

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(errorResponse, response.getBody());
  }
}