package com.atipera.githubinfo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.atipera.githubinfo.errorHandler.GithubClientException;
import com.atipera.githubinfo.errorHandler.GlobalExceptionHandler;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.service.GithubService;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class GithubControllerTest {

  private MockMvc mockMvc;

  @Mock
  private GithubService githubService;

  @InjectMocks
  private GithubController githubController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(githubController)
        .setControllerAdvice(new GlobalExceptionHandler()) // Dodaj GlobalExceptionHandler
        .build();
  }


  @Test
  public void testGetUserRepositories_Success() throws Exception {
    // Given
    Repo repo1 = new Repo();
    repo1.setName("repo1");
    repo1.setFork(false);

    Repo repo2 = new Repo();
    repo2.setName("repo2");
    repo2.setFork(true);

    List<Repo> repoList = List.of(repo1, repo2);
    when(githubService.getUserRepositories("testUser")).thenReturn(repoList);

    // When & Then
    mockMvc.perform(get("/testUser")
            .param("accept", "application/json"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("repo1"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("repo2"));
  }

  @Test
  public void testGetUserRepositories_InvalidAcceptHeader() throws Exception {
    mockMvc.perform(get("/testUser")
            .param("accept", "text/plain"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Accept header must be 'application/json'"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));
  }

  @Test
  public void testGetUserRepositories_UserNotFound() throws Exception {
    // Given
    when(githubService.getUserRepositories("nonexistentUser"))
        .thenThrow(new GithubClientException(HttpStatus.NOT_FOUND.value(), "Not Found"));

    // When & Then
    mockMvc.perform(get("/nonexistentUser")
            .param("accept", "application/json"))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Not Found"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404));
  }

  @Test
  public void testGetUserRepositories_InternalServerError() throws Exception {
    // Given
    when(githubService.getUserRepositories("testUser"))
        .thenThrow(new GithubClientException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"));

    // When & Then
    mockMvc.perform(get("/testUser")
            .param("accept", "application/json"))
        .andExpect(MockMvcResultMatchers.status().isInternalServerError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Internal Server Error"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(500));
  }

  @Test
  public void testGetUserRepositories_NoReposFound() throws Exception {
    // Given
    when(githubService.getUserRepositories("emptyUser")).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/emptyUser")
            .param("accept", "application/json"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
  }
}
