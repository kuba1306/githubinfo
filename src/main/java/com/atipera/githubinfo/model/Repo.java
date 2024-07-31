package com.atipera.githubinfo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repo {

  private String name;

  private Owner owner;

  private boolean fork;

  private List<Branch> branches;

  public boolean isFork() {
    return fork;
  }
}