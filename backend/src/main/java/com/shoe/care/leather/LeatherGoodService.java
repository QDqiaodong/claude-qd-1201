package com.shoe.care.leather;

import com.shoe.care.common.BizException;
import com.shoe.care.common.Dict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeatherGoodService {

    private final LeatherGoodRepository repo;

    public LeatherGoodService(LeatherGoodRepository repo) {
        this.repo = repo;
    }

    public List<LeatherGood> list() {
        return repo.findAll();
    }

    public LeatherGood create(LeatherGood in) {
        if (!Dict.isValidLeatherCategory(in.category)) {
            throw new BizException("皮具品类必须是：鞋 / 包 / 皮带 / 箱 / 其他");
        }
        LeatherGood g = new LeatherGood();
        g.code = (in.code == null || in.code.isBlank()) ? "" : in.code;
        g.category = in.category;
        g.brand = (in.brand == null || in.brand.isBlank()) ? "未知" : in.brand;
        g.material = (in.material == null || in.material.isBlank()) ? "未知" : in.material;
        g.status = (in.status == null || in.status.isBlank()) ? "待接" : in.status;
        if (!Dict.isValidLeatherStatus(g.status)) {
            throw new BizException("皮具状态非法");
        }
        g.customerName = (in.customerName == null || in.customerName.isBlank()) ? "散客" : in.customerName;
        g.createdAt = LocalDateTime.now();
        return repo.save(g);
    }

    public LeatherGood update(Long id, LeatherGood in) {
        LeatherGood g = repo.findById(id).orElseThrow(() -> new BizException("皮具不存在"));
        if (in.code != null) {
            g.code = in.code;
        }
        if (in.category != null) {
            if (!Dict.isValidLeatherCategory(in.category)) {
                throw new BizException("皮具品类必须是：鞋 / 包 / 皮带 / 箱 / 其他");
            }
            g.category = in.category;
        }
        if (in.brand != null && !in.brand.isBlank()) {
            g.brand = in.brand;
        }
        if (in.material != null && !in.material.isBlank()) {
            g.material = in.material;
        }
        if (in.status != null && !in.status.isBlank()) {
            if (!Dict.canTransferLeather(g.status, in.status)) {
                throw new BizException("皮具状态流转非法：已取不可回退，且须按顺序流转");
            }
            g.status = in.status;
        }
        if (in.customerName != null && !in.customerName.isBlank()) {
            g.customerName = in.customerName;
        }
        return repo.save(g);
    }

    /** 状态流转（已取不可回退）。 */
    public LeatherGood updateStatus(Long id, String to) {
        LeatherGood g = repo.findById(id).orElseThrow(() -> new BizException("皮具不存在"));
        if (!Dict.canTransferLeather(g.status, to)) {
            throw new BizException("皮具状态流转非法：已取不可回退，且须按顺序流转");
        }
        g.status = to;
        return repo.save(g);
    }
}
