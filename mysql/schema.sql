SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- ---------------------------------------------------------------------
-- 修鞋皮具护理坊 · 4 模块业务库
-- 库名 shoe_care；application.yml 里 ddl-auto: none，表结构全部由本文件在 MySQL 镜像构建期导入。
-- 注意：mysql 容器是 UTC，时间戳一律写 NOW()，日期用 DATE_SUB(CURDATE(), INTERVAL N DAY)。
-- 两处 schema（backend/src/main/resources/schema.sql 与 mysql/schema.sql）内容必须完全一致。

CREATE DATABASE IF NOT EXISTS shoe_care
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE shoe_care;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS pickup;
DROP TABLE IF EXISTS part_use;
DROP TABLE IF EXISTS care_order;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS part;
DROP TABLE IF EXISTS leather_good;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------- 1. 皮具台账 ----------------
CREATE TABLE leather_good (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(32) COMMENT '登记编号',
    category     VARCHAR(16) COMMENT '鞋 / 包 / 皮带 / 箱 / 其他',
    brand        VARCHAR(64) COMMENT '品牌',
    material     VARCHAR(32) COMMENT '材质',
    status       VARCHAR(16) COMMENT '待接 / 护理中 / 待取 / 已取',
    customer_name VARCHAR(64) COMMENT '归属客户',
    created_at   DATETIME COMMENT '登记时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 2. 护理工单 ----------------
CREATE TABLE care_order (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    leather_id   BIGINT NOT NULL COMMENT '皮具 id',
    project      VARCHAR(32) COMMENT '清洁保养 / 补色 / 换底 / 换跟 / 缝线',
    price        DECIMAL(10,2) COMMENT '报价',
    start_time   DATETIME COMMENT '开始时间',
    finish_time  DATETIME COMMENT '完成时间',
    status       VARCHAR(16) COMMENT '进行中 / 已完成',
    created_at   DATETIME COMMENT '创建时间',
    CONSTRAINT fk_co_leather FOREIGN KEY (leather_id) REFERENCES leather_good (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 3. 配件耗材 ----------------
CREATE TABLE part (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(128) NOT NULL COMMENT '配件名称',
    category     VARCHAR(16) COMMENT '胶水 / 鞋线 / 鞋底 / 五金 / 其他',
    stock        INT NOT NULL COMMENT '库存',
    unit         VARCHAR(16) COMMENT '单位',
    warn_min     INT NOT NULL COMMENT '预警下限',
    created_at   DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE part_use (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    part_id      BIGINT NOT NULL COMMENT '配件 id',
    qty          INT NOT NULL COMMENT '领用数量',
    use_time     DATETIME COMMENT '领用时间',
    CONSTRAINT fk_pu_part FOREIGN KEY (part_id) REFERENCES part (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 4. 会员与取件 ----------------
CREATE TABLE member (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(64) NOT NULL COMMENT '会员姓名',
    phone        VARCHAR(24) COMMENT '手机',
    points       INT NOT NULL COMMENT '累计积分',
    level        VARCHAR(16) COMMENT '普通 / 银卡 / 金卡 / 钻石',
    created_at   DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pickup (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id    BIGINT NOT NULL COMMENT '会员 id',
    leather_id   BIGINT NOT NULL COMMENT '皮具 id',
    amount       DECIMAL(10,2) COMMENT '消费金额',
    pickup_time  DATETIME COMMENT '取件时间',
    CONSTRAINT fk_pk_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_pk_leather FOREIGN KEY (leather_id) REFERENCES leather_good (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 种子数据（含异常态，用于真实触发业务拒绝分支）
--   异常态 1：L3 状态为「待取」，但其工单 O1 仍为「进行中」→ 取件接口拦截
--   异常态 2：O2 报价为 0 → 完成工单接口拦截（报价必须 > 0）
--   异常态 3：P1 库存 0 且 P3 库存低于预警下限 → 领用接口拦截
--   异常态 4：L4 已为「已取」→ 状态回退接口拦截（已取不可回退）
-- ---------------------------------------------------------------------

INSERT IGNORE INTO leather_good (code, category, brand, material, status, customer_name, created_at) VALUES
('LG2026001', '鞋',   'Nike',    '牛皮', '待接', '张伟', DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('LG2026002', '包',   'Coach',   '羊皮', '护理中', '李娜', DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
('LG2026003', '鞋',   'Adidas',  '帆布', '待取', '王芳', DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
('LG2026004', '皮带', '七匹狼',  '牛皮', '已取', '赵强', DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
('LG2026005', '箱',   '新秀丽',  '涤纶', '待接', '孙莉', DATE_SUB(CURDATE(), INTERVAL 1 DAY));

INSERT IGNORE INTO care_order (leather_id, project, price, start_time, finish_time, status, created_at) VALUES
(3, '补色',   120.00, DATE_SUB(CURDATE(), INTERVAL 2 DAY), NULL, '进行中', DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(2, '换底',   0.00,   DATE_SUB(CURDATE(), INTERVAL 3 DAY), NULL, '进行中', DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(4, '清洁保养', 80.00, DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), '已完成', DATE_SUB(CURDATE(), INTERVAL 12 DAY)),
(1, '缝线',   50.00,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), NULL, '进行中', DATE_SUB(CURDATE(), INTERVAL 5 DAY));

INSERT IGNORE INTO part (name, category, stock, unit, warn_min, created_at) VALUES
('强力胶水',     '胶水', 0,  '瓶', 20, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
('进口牛皮鞋线', '鞋线', 50, '卷', 10, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('橡胶牛筋底',   '鞋底', 5,  '双', 10, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('铜质五金扣',   '五金', 30, '个', 5,  DATE_SUB(CURDATE(), INTERVAL 15 DAY));

INSERT IGNORE INTO member (name, phone, points, level, created_at) VALUES
('张伟', '13800000001', 0,   '普通', DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
('李娜', '13900000002', 150, '银卡', DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
('王芳', '13700000003', 600, '金卡', DATE_SUB(CURDATE(), INTERVAL 30 DAY));

INSERT IGNORE INTO pickup (member_id, leather_id, amount, pickup_time) VALUES
(2, 4, 80.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY));
