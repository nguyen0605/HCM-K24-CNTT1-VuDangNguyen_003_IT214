package org.example.courseservice.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "courses")
public class Course implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseName;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal courseFee;

    @Column(nullable = false)
    private Integer stock;

    public Course() {
    }

    public Course(Long id, String courseName, String description, BigDecimal courseFee, Integer stock) {
        this.id = id;
        this.courseName = courseName;
        this.description = description;
        this.courseFee = courseFee;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getCourseFee() { return courseFee; }
    public void setCourseFee(BigDecimal courseFee) { this.courseFee = courseFee; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
