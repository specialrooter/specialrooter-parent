package io.specialrooter.plus.mybatisplus.basic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class Constant {
    public static String ID_STRATEGY;

    //注入
    @Autowired(required = false)
    public void setUploadPath(@Value("${specialrooter.id-strategy:snow}")String ID_STRATEGY) {
        Constant.ID_STRATEGY = ID_STRATEGY;
    }
}
