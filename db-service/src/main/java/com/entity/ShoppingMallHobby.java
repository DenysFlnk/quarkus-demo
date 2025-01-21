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

@Entity
@Table(name = "shopping_mall_hobby")
@Getter
@Setter
public class ShoppingMallHobby extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "shopping_mall_id")
    private ShoppingMall shoppingMall;

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

    public static Uni<ShoppingMallHobby> findByIdNotDeleted(Integer id) {
        return ShoppingMallHobby.find("SELECT sh FROM ShoppingMallHobby sh WHERE sh.id=?1 AND sh.isDeleted=false",
            id).firstResult();
    }
}
