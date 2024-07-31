package com.atipera.githubinfo.webclient.info;

import com.atipera.githubinfo.errorHandler.UserNotFoundException;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.webclient.info.dto.BranchDto;
import com.atipera.githubinfo.webclient.info.dto.webclient.info.GithubClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GithubClientTest {

  private RestTemplate restTemplate;
  private GithubClient githubClient;

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
    githubClient = new GithubClient(restTemplate);
  }

  @Test
  void testGetUserRepositories_Success() {
    String username = "testUser";
    Repo[] repos = {new Repo(), new Repo()};
    ResponseEntity<Repo[]> response = new ResponseEntity<>(repos, HttpStatus.OK);
    when(restTemplate.getForEntity(anyString(), eq(Repo[].class), any(HttpHeaders.class)))
        .thenReturn(response);

    List<Repo> result = githubClient.getUserRepositories(username);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(restTemplate, times(1)).getForEntity(anyString(), eq(Repo[].class), any(HttpHeaders.class));
  }

  @Test
  void testGetUserRepositories_UserNotFound() {
    String username = "unknownUser";
    when(restTemplate.getForEntity(anyString(), eq(Repo[].class), any(HttpHeaders.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
      githubClient.getUserRepositories(username);
    });

    assertEquals("User unknownUser not found", thrown.getMessage());
  }

  @Test
  void testGetBranchesForRepo_Success() {
    String username = "testUser";
    String repoName = "testRepo";
    BranchDto[] branches = {new BranchDto(), new BranchDto()}; // Assuming BranchDto has a default constructor
    ResponseEntity<BranchDto[]> response = new ResponseEntity<>(branches, HttpStatus.OK);
    when(restTemplate.getForEntity(anyString(), eq(BranchDto[].class), any(HttpHeaders.class)))
        .thenReturn(response);

    List<BranchDto> result = githubClient.getBranchesForRepo(username, repoName);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(restTemplate, times(1)).getForEntity(anyString(), eq(BranchDto[].class), any(HttpHeaders.class));
  }

  @Test
  void testGetBranchesForRepo_Error() {
    String username = "testUser";
    String repoName = "testRepo";
    when(restTemplate.getForEntity(anyString(), eq(BranchDto[].class), any(HttpHeaders.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> {
      githubClient.getBranchesForRepo(username, repoName);
    });

    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
  }
}