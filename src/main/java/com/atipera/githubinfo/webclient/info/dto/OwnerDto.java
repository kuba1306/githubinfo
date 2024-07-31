package com.atipera.githubinfo.webclient.info.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class OwnerDto {

  private String login;
}