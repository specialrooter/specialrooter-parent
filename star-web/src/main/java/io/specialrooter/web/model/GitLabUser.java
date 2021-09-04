package io.specialrooter.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GitLabUser {
    private Integer id;
    private String name;
    private String username;
    private String state;
    private String avatar_url;
    private String web_url;
}
