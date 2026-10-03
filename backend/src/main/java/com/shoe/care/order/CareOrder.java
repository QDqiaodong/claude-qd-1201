package com.shoe.care.order;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 护理工单：每张皮具对应护理项目（清洁保养 / 补色 / 换底 / 换跟 / 缝线）。 */
@Entity
@Table(name = "care_order")
public class CareOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long leatherId;
    public String project;
    public Double price;
    public LocalDateTime startTime;
    public LocalDateTime finishTime;
    public String status;
    public LocalDateTime createdAt;
}
