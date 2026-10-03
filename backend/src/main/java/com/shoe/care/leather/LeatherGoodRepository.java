package com.shoe.care.leather;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeatherGoodRepository extends JpaRepository<LeatherGood, Long> {
    List<LeatherGood> findByStatus(String status);
}
