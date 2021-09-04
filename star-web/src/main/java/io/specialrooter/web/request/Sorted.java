package io.specialrooter.web.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sorted {
    String name;
    SortedEnum sort;
}

enum SortedEnum {
    ASC,DESC
}