package com.atipera.githubinfo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.atipera.githubinfo.errorHandler.CustomErrorResponse;
import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.service.GithubService;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    mockMvc = MockMvcBuilders.standaloneSetup(githubController).build();
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

    List<Repo> repoList = Arrays.asList(repo1, repo2);
    ResponseEntity<Object> responseEntity = ResponseEntity.ok(repoList);
    when(githubService.getUserRepositories("testUser")).thenReturn(responseEntity);

    // When & Then
    mockMvc.perform(get("/testUser")
            .param("accept", "application/json"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("repo1"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("repo2"));
  }

  @Test
  public void testGetUserRepositories_UserNotFound() throws Exception {
    // Given
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found");
    ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    when(githubService.getUserRepositories("unknownUser")).thenReturn(responseEntity);

    // When & Then
    mockMvc.perform(get("/unknownUser")
            .param("accept", "application/json"))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));
  }

  @Test
  public void testGetUserRepositories_InvalidAcceptHeader() throws Exception {
    // When & Then
    mockMvc.perform(get("/testUser")
            .param("accept", "text/plain"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().string("Accept header must be 'application/json'"));
  }
}