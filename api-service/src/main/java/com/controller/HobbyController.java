package com.controller;

import com.quarkus.api.HobbyControllerApi;
import com.quarkus.model.Hobby;
import com.quarkus.model.HobbyCreateRequest;
import com.service.HobbyService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Authenticated
public class HobbyController implements HobbyControllerApi {

    @Inject
    HobbyService hobbyService;

    @Override
    public CompletionStage<Hobby> getHobby(Integer id) {
        return hobbyService.getHobby(id).subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<List<Hobby>> getAllHobbies() {
        return hobbyService.getAllHobbies().subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<Void> updateHobby(Integer id, Hobby hobby) {
        return hobbyService.updateHobby(id, hobby).subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<Hobby> createHobby(HobbyCreateRequest createRequest) {
        return hobbyService.createHobby(createRequest).subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<Void> deleteHobby(Integer id) {
        return hobbyService.deleteHobby(id).subscribeAsCompletionStage();
    }
}
