package io.specialrooter;

import io.specialrooter.model.GeneratorRequestModel;
import io.specialrooter.message.MessageResponse;

public class TestMessageResponse {

    public static void main(String[] args) {
        GeneratorRequestModel generatorRequestModel = new GeneratorRequestModel();
        MessageResponse success = MessageResponse.success(generatorRequestModel);
        System.out.println(success);
    }
}
