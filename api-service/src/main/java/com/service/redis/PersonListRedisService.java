package com.service.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mapper.PersonMapper;
import com.model.Person;
import com.model.PersonDTO;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.hash.ReactiveHashCommands;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PersonListRedisService {

    public static final String PERSON_LIST_KEY = "PERSON_LIST";

    private static final String MY_KEY = "personListCache";

    private static final PersonMapper mapper = PersonMapper.INSTANCE;

    private final ReactiveHashCommands<String, String, List<PersonDTO>> commands;

    public PersonListRedisService(ReactiveRedisDataSource ds) {
        this.commands = ds.hash(new TypeReference<>() {}, new TypeReference<>() {}, new TypeReference<>() {});
    }

    public void save(String field, List<Person> persons) {
        commands.hset(MY_KEY, field, mapper.toDtoList(persons))
            .subscribe()
            .with(result -> System.out.println("Successful saved to cache: " + result),
            failure -> System.out.println("Saving to cache failed with: " + failure.getMessage()));
    }

    public Uni<List<Person>> retrieve(String field) {
        return commands.hget(MY_KEY, field)
            .onItem()
            .transform(mapper::toEntityList);
    }

    public void invalidateCache(String field) {
        commands.hdel(MY_KEY, field)
            .subscribe()
            .with(result -> System.out.println("Cache invalidated: " + result),
                failure -> System.out.println("Saving to cache failed with: " + failure.getMessage()));;
    }
}
