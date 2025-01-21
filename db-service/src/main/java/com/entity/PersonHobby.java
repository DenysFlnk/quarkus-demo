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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "person_hobby")
@Entity
@Getter
@Setter
public class PersonHobby extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hobby_id")
    private Hobby hobby;

    @Column(name = "author")
    private String author;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public void setToDelete(String author) {
        this.author = author;
        this.isDeleted = true;
    }

    public void setToRestore(String author) {
        this.author = author;
        this.isDeleted = false;
    }

    public static Uni<PersonHobby> findByIdNotDeleted(Integer id) {
        return PersonHobby.find("SELECT ph FROM PersonHobby ph WHERE ph.id=?1 AND ph.isDeleted=false", id)
            .firstResult();
    }
}
