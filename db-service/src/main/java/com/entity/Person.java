package com.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Table(name = "person")
@Entity
@Getter
@Setter
public class Person extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "person_age")
    private int age;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    private List<PersonHobby> hobbies;

    @Column(name = "author")
    private String author;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        if (id != null && !id.isBlank()) {
            this.id = UUID.fromString(id);
        }
    }

    public void setToDelete(String author) {
        this.author = author;
        this.isDeleted = true;
    }

    public void setToRestore(String author) {
        this.author = author;
        this.isDeleted = false;
    }

    public static Uni<Person> findByIdFetchHobbies(UUID id) {
        return Person.find("SELECT p FROM Person p JOIN FETCH p.hobbies h WHERE p.id=?1", id).firstResult();
    }

    public static Uni<Person> findByIdFetchHobbiesNotDeleted(UUID id) {
        return Person.find("SELECT p FROM Person p JOIN FETCH p.hobbies h WHERE p.id=?1 AND p.isDeleted = false "
                + "AND h.isDeleted=false AND h.hobby.isDeleted=false", id).firstResult();
    }

    public static Uni<List<Person>> findAllFetchHobbies() {
        return Person.list("SELECT p FROM Person p JOIN FETCH p.hobbies h");
    }

    public static Uni<List<Person>> findAllFetchHobbiesNotDeleted() {
        return Person.list("SELECT p FROM Person p JOIN FETCH p.hobbies h WHERE p.isDeleted = false "
            + "AND h.isDeleted=false AND h.hobby.isDeleted=false");
    }

    public static Uni<List<Person>> findByHobby(String hobby) {
        return Person.list("SELECT p FROM Person p JOIN p.hobbies h WHERE h.hobby.name = ?1", hobby.toLowerCase());
    }

    public static Uni<List<Person>> findByHobbyNotDeleted(String hobby) {
        return Person.list("SELECT p FROM Person p JOIN p.hobbies h WHERE p.isDeleted=false "
            + "AND h.isDeleted=false AND h.hobby.name=?1 AND h.hobby.isDeleted=false", hobby.toLowerCase());
    }

    public static Uni<List<Person>> findByHobbyFetchHobbies(String hobby) {
        return Person.list("SELECT p FROM Person p JOIN FETCH p.hobbies h WHERE h.hobby.name = ?1",
            hobby.toLowerCase());
    }

    public static Uni<List<Person>> findByHobbyFetchHobbiesNotDeleted(String hobby) {
        return Person.list("SELECT p FROM Person p JOIN FETCH p.hobbies h WHERE p.isDeleted=false "
                + "AND h.isDeleted=false AND h.hobby.name = ?1 AND h.hobby.isDeleted=false", hobby.toLowerCase());
    }

    public static Uni<Person> findByIdNotDeleted(UUID id) {
        return Person.find("SELECT p FROM Person p WHERE p.id=?1 AND p.isDeleted = false", id).firstResult();
    }

    public static Uni<List<Person>> findAllNotDeleted() {
        return Person.list("SELECT p FROM Person p WHERE p.isDeleted = false");
    }
}
