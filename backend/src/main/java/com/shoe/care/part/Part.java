package com.shoe.care.part;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 配件耗材：胶水、鞋线、鞋底、五金等库存。 */
@Entity
@Table(name = "part")
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public String category;
    public Integer stock;
    public String unit;
    public Integer warnMin;
    public LocalDateTime createdAt;
}
