package com.atipera.githubinfo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atipera.githubinfo.model.Repo;
import com.atipera.githubinfo.service.GithubService;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(GithubController.class)
class GithubControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GithubService githubService;


  @Test
  void testGetUserRepositories_Success() throws Exception {
    String username = "testUser";
    List<Repo> repos = Arrays.asList(new Repo(), new Repo()); // Assuming Repo has a default constructor
    when(githubService.getUserRepositories(username))
        .thenReturn(ResponseEntity.ok(repos));

    mockMvc.perform(get("/" + username)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void testGetUserRepositories_InternalServerError() throws Exception {
    String username = "testUser";
    when(githubService.getUserRepositories(username))
        .thenThrow(new RuntimeException("Internal Server Error"));

    mockMvc.perform(get("/" + username)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }

}