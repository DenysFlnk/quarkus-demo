package com.service;

import com.entity.Hobby;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HobbyScheduleService {

    private static final String AUTHOR = HobbyScheduleService.class.getSimpleName();

    @Scheduled(cron = "{hobby-schedule-service.cron.expression}")
    public Uni<Void> deleteUnusedHobbies() {
        Log.info("HobbyScheduleService.deleteUnusedHobbies() is up.");
        return Panache.withTransaction(() -> Hobby.getUnusedHobbies()
            .onItem()
            .ifNotNull()
            .invoke(hobbies -> hobbies.forEach(hobby -> hobby.setToDelete(AUTHOR))))
            .replaceWithVoid();
    }
}
