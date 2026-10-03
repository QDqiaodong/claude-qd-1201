package com.shoe.care.common;

import java.util.List;
import java.util.Map;

/**
 * 修鞋皮具护理坊业务词典与规则参数表：皮具品类 / 皮具状态流转、护理工单项目、
 * 配件耗材类别、会员等级与积分规则。规则集中在此，便于出题时被要求调整或扩展。
 */
public final class Dict {

    private Dict() {
    }

    // ---------------- 皮具台账 ----------------
    /** 鞋 / 包 / 皮带 / 箱 / 其他 */
    public static final List<String> LEATHER_CATEGORIES = List.of("鞋", "包", "皮带", "箱", "其他");

    /** 待接 / 护理中 / 待取 / 已取 */
    public static final List<String> LEATHER_STATUS = List.of("待接", "护理中", "待取", "已取");

    /** 皮具状态机前进：待接→护理中→待取→已取；已取为终态不可回退 */
    public static final Map<String, List<String>> LEATHER_FLOW = Map.of(
            "待接", List.of("护理中"),
            "护理中", List.of("待取"),
            "待取", List.of("已取"),
            "已取", List.of());

    // ---------------- 护理工单 ----------------
    /** 清洁保养 / 补色 / 换底 / 换跟 / 缝线 */
    public static final List<String> CARE_PROJECTS = List.of("清洁保养", "补色", "换底", "换跟", "缝线");

    /** 进行中 / 已完成 */
    public static final List<String> ORDER_STATUS = List.of("进行中", "已完成");

    // ---------------- 配件耗材 ----------------
    /** 胶水 / 鞋线 / 鞋底 / 五金 / 其他 */
    public static final List<String> PART_CATEGORIES = List.of("胶水", "鞋线", "鞋底", "五金", "其他");

    // ---------------- 会员与积分 ----------------
    /** 每消费 10 元累计 1 积分 */
    public static final int POINT_RATE = 10;

    /** 会员等级阈值（按累计积分）：普通 < 银卡 < 金卡 < 钻石 */
    public static final int LEVEL_SILVER = 100;
    public static final int LEVEL_GOLD = 500;
    public static final int LEVEL_DIAMOND = 1000;

    // ---------------- 取值方法 ----------------

    public static boolean isValidLeatherCategory(String c) {
        return c != null && LEATHER_CATEGORIES.contains(c);
    }

    public static boolean isValidLeatherStatus(String s) {
        return s != null && LEATHER_STATUS.contains(s);
    }

    public static boolean isValidCareProject(String p) {
        return p != null && CARE_PROJECTS.contains(p);
    }

    public static boolean isValidPartCategory(String c) {
        return c != null && PART_CATEGORIES.contains(c);
    }

    /**
     * 皮具状态流转校验：已取不可回退；否则必须按 LEATHER_FLOW 前进。
     */
    public static boolean canTransferLeather(String from, String to) {
        if (!isValidLeatherStatus(from) || !isValidLeatherStatus(to)) {
            return false;
        }
        if ("已取".equals(from) && !"已取".equals(to)) {
            return false;
        }
        List<String> next = LEATHER_FLOW.get(from);
        return next != null && next.contains(to);
    }

    /**
     * 根据累计积分计算会员等级。
     */
    public static String computeLevel(int points) {
        if (points >= LEVEL_DIAMOND) {
            return "钻石";
        }
        if (points >= LEVEL_GOLD) {
            return "金卡";
        }
        if (points >= LEVEL_SILVER) {
            return "银卡";
        }
        return "普通";
    }
}
