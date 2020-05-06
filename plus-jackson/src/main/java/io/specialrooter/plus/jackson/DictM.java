package io.specialrooter.plus.jackson;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DictM {
    private String dict;
    private String field;
    private String mapper;
    private boolean leaf=true;
    Dict.Result res;
}
