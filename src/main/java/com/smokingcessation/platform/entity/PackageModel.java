package com.smokingcessation.platform.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.smokingcessation.platform.enums.PackageType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "packages")
public class PackageModel extends BaseModel {
    private String name;
    private String description;
    @Lob
    @Column
    private String featured;
    private int totalDays;
    private String imageUrl;
    private int price;
    private int salePrice;
    @Column(columnDefinition = "int default 0")
    private int limitMessages = 0;
    @Column(name = "is_default", columnDefinition = "bit default 0")
    public boolean isDefault = false;
    private Double rating;
    private int totalRating;
    private boolean isActive = true;
    private PackageType packageType = PackageType.BEGINNER;

    @OneToMany(
            mappedBy = "packageModel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonBackReference
    List<UserPackageModel> userPackages = new ArrayList<>();

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

}
