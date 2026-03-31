package com.marcus.electronics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String slug;

    @ManyToOne
    @JoinColumn(name = "parentId")
    private Category parent;

    @Column(name = "isActive")
    @JsonProperty("isActive")
    private Boolean isActive;

    // public boolean isActive() {
    // return Boolean.TRUE.equals(this.isActive);
    // }

    // public void setIsActive(Boolean isActive) {
    // this.isActive = isActive;
    // }
}