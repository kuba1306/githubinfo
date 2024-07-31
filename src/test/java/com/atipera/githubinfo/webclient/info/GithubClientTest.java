package com.atipera.githubinfo.webclient.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.atipera.githubinfo.errorHandler.CustomErrorResponse;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.info.dto.BranchDto;
import com.atipera.githubinfo.webclient.info.dto.CommitDto;
import com.atipera.githubinfo.webclient.info.dto.webclient.info.GithubClient;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class GithubClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private GithubClient githubClient;

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

    Repo[] repos = {repo1, repo2};
    ResponseEntity<Repo[]> responseEntity = new ResponseEntity<>(repos, HttpStatus.OK);
    when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .thenReturn(responseEntity);

    // When
    ResponseEntity<Object> response = githubClient.getUserRepositories("testUser");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<Repo> repoList = (List<Repo>) response.getBody();
    assertEquals(2, repoList.size());
    assertEquals("repo1", repoList.get(0).getName());
  }

  @Test
  public void testGetUserRepositories_UserNotFound() {
    // Given
    when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    // When
    ResponseEntity<Object> response = githubClient.getUserRepositories("unknownUser");

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    CustomErrorResponse errorResponse = (CustomErrorResponse) response.getBody();
    assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
    assertEquals("User not found", errorResponse.getMessage());
  }

  @Test
  public void testGetUserRepositories_Error() {
    // Given
    when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    // When
    ResponseEntity<Object> response = githubClient.getUserRepositories("testUser");

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    CustomErrorResponse errorResponse = (CustomErrorResponse) response.getBody();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
    assertEquals("An error occurred", errorResponse.getMessage());
  }

  @Test
  public void testGetBranchesForRepo_Success() {
    // Given
    BranchDto[] branches = {new BranchDto("main",new CommitDto())};
    ResponseEntity<BranchDto[]> responseEntity = new ResponseEntity<>(branches, HttpStatus.OK);
    when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .thenReturn(responseEntity);

    // When
    ResponseEntity<Object> response = githubClient.getBranchesForRepo("testUser", "testRepo");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<BranchDto> branchList = (List<BranchDto>) response.getBody();
    assertEquals(1, branchList.size());
    assertEquals("main", branchList.get(0).getName());
  }

  @Test
  public void testGetBranchesForRepo_Error() {
    // Given
    when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    // When
    ResponseEntity<Object> response = githubClient.getBranchesForRepo("testUser", "testRepo");

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    CustomErrorResponse errorResponse = (CustomErrorResponse) response.getBody();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
    assertEquals("Error fetching branches", errorResponse.getMessage());
  }
}
