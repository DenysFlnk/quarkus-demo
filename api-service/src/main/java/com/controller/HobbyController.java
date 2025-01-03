package com.controller;

import com.quarkus.api.HobbyControllerApi;
import com.quarkus.model.Hobby;
import com.quarkus.model.HobbyCreateRequest;
import com.service.HobbyService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import java.util.List;

@Authenticated
public class HobbyController implements HobbyControllerApi {

    @Inject
    HobbyService hobbyService;

    @Override
    public Uni<Hobby> getHobby(Integer id) {
        return hobbyService.getHobby(id);
    }

    @Override
    public Uni<List<Hobby>> getAllHobbies() {
        return hobbyService.getAllHobbies();
    }

    @Override
    public Uni<Void> updateHobby(Integer id, Hobby hobby) {
        return hobbyService.updateHobby(id, hobby);
    }

    @Override
    public Uni<Hobby> createHobby(HobbyCreateRequest createRequest) {
        return hobbyService.createHobby(createRequest);
    }

    @Override
    public Uni<Void> deleteHobby(Integer id) {
        return hobbyService.deleteHobby(id);
    }
}
