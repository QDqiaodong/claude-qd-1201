package com.shoe.care.leather;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 皮具台账：登记待护理 / 已接的皮具（鞋、包、皮带等）。 */
@Entity
@Table(name = "leather_good")
public class LeatherGood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String code;
    public String category;
    public String brand;
    public String material;
    public String status;
    public String customerName;
    public LocalDateTime createdAt;
}
