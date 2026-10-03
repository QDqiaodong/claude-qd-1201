package com.shoe.care.part;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 配件领用记录：领用减库存。 */
@Entity
@Table(name = "part_use")
public class PartUse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long partId;
    public Integer qty;
    public LocalDateTime useTime;
}
