package com.atipera.githubinfo.webclient.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atipera.githubinfo.errorHandler.GithubClientException;
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
    Repo[] repos = { new Repo() };
    when(restTemplate.getForEntity(anyString(), eq(Repo[].class), anyString()))
        .thenReturn(new ResponseEntity<>(repos, HttpStatus.OK));

    // When
    List<Repo> result = githubClient.getUserRepositories("testUser");

    // Then
    assertEquals(1, result.size());
    verify(restTemplate).getForEntity(anyString(), eq(Repo[].class), anyString());
  }

  @Test
  public void testGetUserRepositories_NotFound() {
    // Given
    HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
    when(restTemplate.getForEntity(anyString(), eq(Repo[].class), anyString()))
        .thenThrow(exception);

    // When & Then
    GithubClientException thrown = assertThrows(GithubClientException.class, () -> {
      githubClient.getUserRepositories("nonExistentUser");
    });

    assertEquals(404, thrown.getStatusCode());
    assertEquals("Not Found", thrown.getMessage());
  }

  @Test
  public void testGetUserRepositories_InternalServerError() {
    // Given
    HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    when(restTemplate.getForEntity(anyString(), eq(Repo[].class), anyString()))
        .thenThrow(exception);

    // When & Then
    GithubClientException thrown = assertThrows(GithubClientException.class, () -> {
      githubClient.getUserRepositories("someUser");
    });

    assertEquals(500, thrown.getStatusCode());
    assertTrue(thrown.getMessage().contains("500 INTERNAL_SERVER_ERROR"));
  }

  @Test
  public void testGetBranchesForRepo_Success() {
    // Given
    BranchDto[] branches = { new BranchDto() };
    when(restTemplate.getForEntity(anyString(), eq(BranchDto[].class), anyString(), anyString()))
        .thenReturn(new ResponseEntity<>(branches, HttpStatus.OK));

    // When
    List<BranchDto> result = githubClient.getBranchesForRepo("testUser", "testRepo");

    // Then
    assertEquals(1, result.size());
    verify(restTemplate).getForEntity(anyString(), eq(BranchDto[].class), anyString(), anyString());
  }

  @Test
  public void testGetBranchesForRepo_NotFound() {
    // Given
    HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
    when(restTemplate.getForEntity(anyString(), eq(BranchDto[].class), anyString(), anyString()))
        .thenThrow(exception);

    // When & Then
    GithubClientException thrown = assertThrows(GithubClientException.class, () -> {
      githubClient.getBranchesForRepo("nonExistentUser", "nonExistentRepo");
    });

    assertEquals(404, thrown.getStatusCode());
    assertEquals("Not Found", thrown.getMessage());
  }
}