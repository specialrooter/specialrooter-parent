package io.specialrooter.web.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 逻辑删除标识
 */
public class TableLogic {

    private List<String> aliasList = new ArrayList<>();

    public void logic(String... alias){
        aliasList.addAll(Arrays.asList(alias));
    }

    public List<String> aliasAll(){
        return this.aliasList;
    }
}
