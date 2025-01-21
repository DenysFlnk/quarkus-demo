package com.entity;

import com.vividsolutions.jts.geom.Point;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Table(name = "shopping_mall")
@Entity
@Getter
@Setter
public class ShoppingMall extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "location", columnDefinition = "geography(Point,4326)")
    private Point location;

    @Column(name = "operational_status")
    @Enumerated(EnumType.STRING)
    private OperationalStatus operationalStatus;

    @OneToMany(mappedBy = "shoppingMall", fetch = FetchType.LAZY)
    private List<ShoppingMallHobby> hobbies;

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

    public static Uni<List<ShoppingMall>> findAllFetchHobbiesExcept(List<Integer> mallIds) {
        return ShoppingMall.list("SELECT s FROM ShoppingMall s JOIN FETCH s.hobbies h WHERE h.id NOT IN(?1)", mallIds);
    }

    public static Uni<List<ShoppingMall>> findAllNotDeletedFetchHobbiesExcept(List<Integer> mallIds) {
        return ShoppingMall.list("SELECT s FROM ShoppingMall s JOIN FETCH s.hobbies h WHERE s.isDeleted=false"
            + "AND h.id NOT IN(?1) AND h.isDeleted=false AND h.hobby.isDeleted=false", mallIds);
    }

    public static Uni<List<ShoppingMall>> findAllFetchHobbies() {
        return ShoppingMall.list("SELECT s FROM ShoppingMall s JOIN FETCH s.hobbies");
    }

    public static Uni<List<ShoppingMall>> findAllNotDeletedFetchHobbies() {
        return ShoppingMall.list("SELECT s FROM ShoppingMall s JOIN FETCH s.hobbies h WHERE s.isDeleted=false "
            + "AND h.isDeleted=false AND h.hobby.isDeleted=false");
    }

    public static Uni<ShoppingMall> findByIdFetchHobby(Integer id) {
        return ShoppingMall.find("SELECT s FROM ShoppingMall s JOIN FETCH s.hobbies h WHERE h.id = ?1", id)
            .firstResult();
    }

    public static Uni<ShoppingMall> findByIdNotDeletedFetchHobby(Integer id) {
        return ShoppingMall.find("SELECT s FROM ShoppingMall s JOIN FETCH s.hobbies h WHERE s.isDeleted=false "
            + "AND h.id = ?1 AND h.isDeleted=false AND h.hobby.isDeleted=false", id).firstResult();
    }

    public static Uni<ShoppingMall> findByIdNotDeleted(Integer id) {
        return ShoppingMall.find("SELECT s FROM ShoppingMall s WHERE s.id=?1 AND s.isDeleted=false", id)
            .firstResult();
    }
}
