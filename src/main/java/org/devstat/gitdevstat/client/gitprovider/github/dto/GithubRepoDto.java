/* OpenSource 2023 */
package org.devstat.gitdevstat.client.gitprovider.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepoDto(int id, String name, @JsonProperty("full_name") String fullName) {}
