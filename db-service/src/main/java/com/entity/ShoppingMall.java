package com.entity;

import com.vividsolutions.jts.geom.Point;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "shopping_mall_hobby",
        joinColumns = @JoinColumn(name = "shopping_mall_id"),
        inverseJoinColumns = @JoinColumn(name = "hobby_id")
    )
    private List<Hobby> hobbies;
}
