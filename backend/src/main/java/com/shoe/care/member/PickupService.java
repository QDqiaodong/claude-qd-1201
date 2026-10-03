package com.shoe.care.member;

import com.shoe.care.common.BizException;
import com.shoe.care.common.Dict;
import com.shoe.care.leather.LeatherGood;
import com.shoe.care.leather.LeatherGoodRepository;
import com.shoe.care.order.CareOrder;
import com.shoe.care.order.CareOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PickupService {

    private final PickupRepository pickupRepo;
    private final MemberRepository memberRepo;
    private final LeatherGoodRepository leatherRepo;
    private final CareOrderRepository orderRepo;

    public PickupService(PickupRepository pickupRepo,
                         MemberRepository memberRepo,
                         LeatherGoodRepository leatherRepo,
                         CareOrderRepository orderRepo) {
        this.pickupRepo = pickupRepo;
        this.memberRepo = memberRepo;
        this.leatherRepo = leatherRepo;
        this.orderRepo = orderRepo;
    }

    public List<Pickup> list(Long memberId) {
        if (memberId != null) {
            return pickupRepo.findByMemberId(memberId);
        }
        return pickupRepo.findAll();
    }

    /**
     * 取件：皮具须为「待取」且所有护理工单已完成；取件后皮具转「已取」，
     * 会员按消费金额累计积分并刷新等级。
     */
    public Pickup create(Long memberId, Long leatherId, Double amount) {
        if (memberId == null || !memberRepo.existsById(memberId)) {
            throw new BizException("取件必须指定一个有效会员");
        }
        if (leatherId == null || !leatherRepo.existsById(leatherId)) {
            throw new BizException("取件必须指定一件已登记的皮具");
        }
        if (amount == null || amount < 0) {
            throw new BizException("取件金额必须 >= 0");
        }
        LeatherGood g = leatherRepo.findById(leatherId).get();
        if (!"待取".equals(g.status)) {
            throw new BizException("皮具尚未到达「待取」状态，无法取件");
        }
        List<CareOrder> orders = orderRepo.findByLeatherId(leatherId);
        boolean allDone = orders.stream().allMatch(o -> "已完成".equals(o.status));
        if (!allDone) {
            throw new BizException("存在未完成的护理工单，无法取件");
        }

        g.status = "已取";
        leatherRepo.save(g);

        Member m = memberRepo.findById(memberId).get();
        m.points = m.points + (int) Math.floor(amount / Dict.POINT_RATE);
        m.level = Dict.computeLevel(m.points);
        memberRepo.save(m);

        Pickup p = new Pickup();
        p.memberId = memberId;
        p.leatherId = leatherId;
        p.amount = amount;
        p.pickupTime = LocalDateTime.now();
        return pickupRepo.save(p);
    }
}
