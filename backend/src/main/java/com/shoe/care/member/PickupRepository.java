package com.shoe.care.member;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PickupRepository extends JpaRepository<Pickup, Long> {
    List<Pickup> findByMemberId(Long memberId);
}
