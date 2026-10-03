package com.shoe.care.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CareOrderRepository extends JpaRepository<CareOrder, Long> {
    List<CareOrder> findByLeatherId(Long leatherId);
}
