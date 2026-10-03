package com.shoe.care.part;

import com.shoe.care.common.BizException;
import com.shoe.care.common.Dict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartService {

    private final PartRepository repo;
    private final PartUseRepository useRepo;

    public PartService(PartRepository repo, PartUseRepository useRepo) {
        this.repo = repo;
        this.useRepo = useRepo;
    }

    public List<Part> list() {
        return repo.findAll();
    }

    public List<PartUse> listUses() {
        return useRepo.findAll();
    }

    public Part create(Part in) {
        if (!Dict.isValidPartCategory(in.category)) {
            throw new BizException("配件类别必须是：胶水 / 鞋线 / 鞋底 / 五金 / 其他");
        }
        Part p = new Part();
        p.name = (in.name == null || in.name.isBlank()) ? "未命名配件" : in.name;
        p.category = in.category;
        p.stock = (in.stock == null) ? 0 : in.stock;
        p.unit = (in.unit == null || in.unit.isBlank()) ? "个" : in.unit;
        p.warnMin = (in.warnMin == null) ? 0 : in.warnMin;
        p.createdAt = LocalDateTime.now();
        return repo.save(p);
    }

    public Part update(Long id, Part in) {
        Part p = repo.findById(id).orElseThrow(() -> new BizException("配件不存在"));
        if (in.name != null && !in.name.isBlank()) {
            p.name = in.name;
        }
        if (in.category != null) {
            if (!Dict.isValidPartCategory(in.category)) {
                throw new BizException("配件类别必须是：胶水 / 鞋线 / 鞋底 / 五金 / 其他");
            }
            p.category = in.category;
        }
        if (in.stock != null) {
            p.stock = in.stock;
        }
        if (in.unit != null && !in.unit.isBlank()) {
            p.unit = in.unit;
        }
        if (in.warnMin != null) {
            p.warnMin = in.warnMin;
        }
        return repo.save(p);
    }

    /** 领用：库存为负或低于预警下限时拦截；领用减库存。 */
    public PartUse pickup(Long id, Integer qty) {
        Part p = repo.findById(id).orElseThrow(() -> new BizException("配件不存在"));
        if (qty == null || qty <= 0) {
            throw new BizException("领用数量必须大于 0");
        }
        if (qty > p.stock) {
            throw new BizException("库存不足，当前库存 " + p.stock + "，无法领用 " + qty);
        }
        if (p.stock - qty < p.warnMin) {
            throw new BizException("领用后库存将低于预警下限 " + p.warnMin + "，禁止领用");
        }
        p.stock = p.stock - qty;
        repo.save(p);

        PartUse u = new PartUse();
        u.partId = id;
        u.qty = qty;
        u.useTime = LocalDateTime.now();
        return useRepo.save(u);
    }
}
