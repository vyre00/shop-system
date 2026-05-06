# 商品交易平台

## 1. 架構說明

### 三層式架構，主要模組如下：
- **controller**
- **service**
- **repository**

---

## 2. 資料庫

**1. 使用者 (`users`)**
| 欄位名稱 | 資料型別 | 屬性 / 約束 | 說明 |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY | 主鍵 |
| `national_id` | VARCHAR(255) | UNIQUE, NOT NULL | 身分證字號 |
| `name` | VARCHAR(255) | NOT NULL | 姓名 |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | 電子信箱 |
| `account` | VARCHAR(255) | UNIQUE, NOT NULL | 登入帳號 |
| `password_hash` | VARCHAR(255) | NOT NULL | 雜湊後的密碼 |
| `role` | VARCHAR(255) | NOT NULL | 角色 |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 建立時間 |
| `updated_at` | TIMESTAMP | NULL | 更新時間 |
| `deleted_at` | TIMESTAMP | NULL | 刪除標記 |

**2. 商品 (`products`)**
| 欄位名稱 | 資料型別 | 屬性 / 約束 | 說明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | 商品 ID |
| `product_name` | VARCHAR(255) | NOT NULL | 商品名稱 |
| `stock_quantity` | INTEGER | NOT NULL | 剩餘庫存 |
| `price` | DECIMAL(10, 2) | NOT NULL | 商品價格 |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 建立時間 |
| `updated_at` | TIMESTAMP | NULL | 更新時間 |
| `deleted_at` | TIMESTAMP | NULL | 刪除標記 |

**3. 訂單 (`orders`)**
| 欄位名稱 | 資料型別 | 屬性 / 約束 | 說明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | 訂單 ID |
| `account` | VARCHAR(255) | NOT NULL | 下單使用者帳號 |
| `total_amount` | NUMERIC(12, 2) | NOT NULL | 訂單總金額 |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 建立時間 |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 更新時間 |

**4. 訂單明細 (`order_items`)**
| 欄位名稱 | 資料型別 | 屬性 / 約束 | 說明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | 明細 ID |
| `order_id` | BIGINT | NOT NULL, REFERENCES orders(id) | 關聯到訂單表 |
| `product_id` | BIGINT | NOT NULL | 商品 ID |
| `product_name` | VARCHAR(255) | NOT NULL | 商品快照名稱 |
| `quantity` | INTEGER | NOT NULL | 購買數量 |
| `unit_price` | NUMERIC(12, 2) | NOT NULL | 快照單價 |
| `total_price` | NUMERIC(12, 2) | NOT NULL | 小計 |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 建立時間 |
