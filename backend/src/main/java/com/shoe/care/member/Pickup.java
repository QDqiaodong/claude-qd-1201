package com.shoe.care.member;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 取件记录：哪个会员取走哪件皮具、取件时间、消费金额。 */
@Entity
@Table(name = "pickup")
public class Pickup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long memberId;
    public Long leatherId;
    public Double amount;
    public LocalDateTime pickupTime;
}
