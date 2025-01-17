package com.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Table(name = "hobby")
@Entity
@Getter
@Setter
public class Hobby extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "author")
    String author;

    @Column(name = "is_deleted")
    boolean isDeleted;

    public void setToDelete(String author) {
        this.author = author;
        this.isDeleted = true;
    }

    public static Uni<Hobby> findByIdNotDeleted(Integer id) {
        return Hobby.find("SELECT h FROM Hobby h WHERE h.id = ?1 AND h.isDeleted = false", id).firstResult();
    }

    public static Uni<List<Hobby>> findAllNotDeleted() {
        return Hobby.list("SELECT h FROM Hobby h WHERE h.isDeleted = false");
    }

    public static Uni<List<Hobby>> getUnusedHobbies() {
        return Hobby.list("SELECT h FROM Hobby h "
            + "WHERE h.id NOT IN(SELECT h.id FROM Hobby h INNER JOIN PersonHobby ph ON ph.hobby.id = h.id)");
    }
}
