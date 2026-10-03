package com.shoe.care.member;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 会员信息：积分（消费累计）、会员等级。 */
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public String phone;
    public Integer points;
    public String level;
    public LocalDateTime createdAt;
}
