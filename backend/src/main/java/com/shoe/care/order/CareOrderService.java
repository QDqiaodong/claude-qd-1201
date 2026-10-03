package com.shoe.care.order;

import com.shoe.care.common.BizException;
import com.shoe.care.common.Dict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CareOrderService {

    private final CareOrderRepository repo;
    private final com.shoe.care.leather.LeatherGoodRepository leatherRepo;

    public CareOrderService(CareOrderRepository repo,
                            com.shoe.care.leather.LeatherGoodRepository leatherRepo) {
        this.repo = repo;
        this.leatherRepo = leatherRepo;
    }

    public List<CareOrder> list(Long leatherId) {
        if (leatherId != null) {
            return repo.findByLeatherId(leatherId);
        }
        return repo.findAll();
    }

    public CareOrder create(CareOrder in) {
        if (in.leatherId == null || !leatherRepo.existsById(in.leatherId)) {
            throw new BizException("护理工单必须关联一张已登记的皮具");
        }
        if (!Dict.isValidCareProject(in.project)) {
            throw new BizException("护理项目必须是：清洁保养 / 补色 / 换底 / 换跟 / 缝线");
        }
        if (in.price == null || in.price <= 0) {
            throw new BizException("护理报价必填且必须大于 0");
        }
        CareOrder o = new CareOrder();
        o.leatherId = in.leatherId;
        o.project = in.project;
        o.price = in.price;
        o.startTime = (in.startTime == null) ? LocalDateTime.now() : in.startTime;
        o.finishTime = null;
        o.status = "进行中";
        o.createdAt = LocalDateTime.now();
        return repo.save(o);
    }

    public CareOrder update(Long id, CareOrder in) {
        CareOrder o = repo.findById(id).orElseThrow(() -> new BizException("护理工单不存在"));
        if (in.project != null) {
            if (!Dict.isValidCareProject(in.project)) {
                throw new BizException("护理项目必须是：清洁保养 / 补色 / 换底 / 换跟 / 缝线");
            }
            o.project = in.project;
        }
        if (in.price != null) {
            if (in.price <= 0) {
                throw new BizException("护理报价必须大于 0");
            }
            o.price = in.price;
        }
        if (in.startTime != null) {
            o.startTime = in.startTime;
        }
        return repo.save(o);
    }

    /** 完成工单：必须已填项目且报价有效。 */
    public CareOrder complete(Long id) {
        CareOrder o = repo.findById(id).orElseThrow(() -> new BizException("护理工单不存在"));
        if (o.project == null || o.project.isBlank()) {
            throw new BizException("未填护理项目，无法完成工单");
        }
        if (o.price == null || o.price <= 0) {
            throw new BizException("报价无效（必须 > 0），无法完成工单");
        }
        o.status = "已完成";
        o.finishTime = LocalDateTime.now();
        return repo.save(o);
    }
}
