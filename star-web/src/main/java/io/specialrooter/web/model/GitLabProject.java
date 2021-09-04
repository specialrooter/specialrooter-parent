package io.specialrooter.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GitLabProject {
    private Integer id;
    private String name;
    private String description;
    private String name_with_namespace;
    private String path;
    private String path_with_namespace;
    private String web_url;
    private String readme_url;
    private String ssh_url_to_repo;
}
