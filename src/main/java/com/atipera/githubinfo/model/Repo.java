package com.atipera.githubinfo.model;

import com.atipera.githubinfo.webclient.dto.BranchDto;
import com.atipera.githubinfo.webclient.dto.OwnerDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties
public class Repo {

  private String name;

  private OwnerDto owner;

  @JsonIgnore
  private boolean fork;

  private List<BranchDto> branches;

  public boolean isFork() {
    return fork;
  }
}