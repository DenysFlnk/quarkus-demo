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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
public class Person extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "person_age")
    private int age;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "person_hobby",
        joinColumns = @JoinColumn(name = "person_id"),
        inverseJoinColumns = @JoinColumn(name = "hobby_id")
    )
    private List<Hobby> hobbies;

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        if (id != null && !id.isBlank()) {
            this.id = UUID.fromString(id);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    public static Uni<List<Person>> getPersonsByHobby(String hobby) {
        return Person.list("SELECT p FROM Person p JOIN p.hobbies h WHERE h.name = ?1", hobby.toLowerCase());
    }
}
