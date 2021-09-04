package io.specialrooter.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GitLabGroup {
    private Integer id;
    private String name;
    private String path;
    private String visibility;
    private String lfs_enabled;
    private String full_name;
    private String full_path;
    private String description;
    private String web_url;
}
