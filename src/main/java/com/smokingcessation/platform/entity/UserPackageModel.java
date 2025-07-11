package com.smokingcessation.platform.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user_package")
public class UserPackageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean isActive = true;
    private boolean isDone = false;
    private double rating = 0.0;
    private String review = "";
    @OneToOne(
            mappedBy = "userPackage",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonBackReference
    private OrderModel order;

    @ManyToOne()
    @JsonBackReference
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne()
    @JsonManagedReference
    @JoinColumn(name = "package_id", nullable = false)
    private PackageModel packageModel;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;




}
