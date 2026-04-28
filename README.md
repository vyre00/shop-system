# 商品交易平台後端服務 - 技術設計文件

## 1. 架構說明

### 專案分層結構
  專案使用常見的分層模式,主要模組如下：
- **controller**
- **service**
- **repository**
- **entity**
- **dto**
- **config**
- **exception**
- **util**

### 設計原因
  因依目前經驗碰過的專案都是以此架構設計，分層很清楚也好維護

---

## 2. 資料庫設計 (Database Design)

### **Table Schema**

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

### **為什麼這樣設計？**
1. 將使用者ID使用UUID儲存，資料安全性
2. 在明細透過 `product_name` 跟 `unit_price` 紀錄下單當下資料，避免未來商品名稱或售價調整時，才不會參照到錯誤的資料

### **建立哪些 Index？為什麼？**
  有對以下欄位建立了Index確保查詢效率：
- **`orders(account)`**：
  提供使用者在查詢訂單時，不需要全表掃描
- **`order_items(order_id)`**：
  為了在查詢明細時加快訂單與明細的 Join 速度

---

## 3. 取捨與架構思考 (Trade-off)

### **為什麼不是悲觀鎖？**
  交易平台這種會有高併發問題情況下，悲觀鎖會把那筆商品資料鎖死，導致其他人的請求只能排隊乾等，會拖垮資料庫效能，
  所以透過 SQL 使用條件判斷 `UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?`。雖然在高併發下，晚一步的請求會因為庫存不足直接失敗，但在「搶購」的真實情境下，先搶先贏，才是最合理且效率最好的做法

### **為什麼要用 Soft Delete？**
  在 `users` 和 `products` 表中，我都加上了 `deleted_at` 欄位
  如果今天商品下架了就直接把它 `DELETE` 掉，那過去買過這個商品的歷史訂單在做 Join 查詢時就會報錯，或是缺資料。所以這樣做的目的是可以確保參照資料完整性

### **如果流量增加 100 倍，哪裡會先垮？我會怎麼改？**
- **資料庫連線數被打爆**：
  所有動作都卡在資料庫上，導致資料庫連線數被佔滿
- **執行緒卡死**：
  API 等待 DB 回應的時間變長，導致後端的 Thread Pool 被佔滿，後續請求都會 Timeout
- **我會怎麼改：**
  透過 Redis 查詢並預減庫存，再將商品資訊丟到 Message Queue，就可以先回傳訊息給前端，再繼續由後端繼續寫 DB 入的動作，達到保護資料庫不會因為瞬間流量被塞爆的問題

---

## 4. AI 使用聲明與驗證方法

  這次的題目開發時，因為個人開發經驗的不足，且為了提高開發效率，就透過Gemini來輔助開發

### **主要輔助範圍包含**
1. **語法生成**：透過描述功能與邏輯，快速產的相關功能語法
2. **疑難雜症除錯**：在 `Order` 與 `OrderItem` 的 `@OneToMany` 關聯時，遇到 `order_id` 寫入DB報出 Null 的錯誤，也透過 AI 解決了 Hibernate 單向/雙向關聯的儲存順序問題，並改用雙向關聯解決問題
3. **測試靈感**：透過描述情境，請 AI 產出高併發的測試

### **我是如何驗證 AI 產出的正確性？**
  我知道 AI 給的程式碼很多時候不能直接複製貼上使用，所以我的驗證方式是：
1. 自己也看過一次，檢查邏輯是否符合我要的需求。因為 AI 常會寫出語法沒錯但邏輯錯誤的問題
2. 透過 IDE 的檢查，也可以馬上就可以知道是哪裡有問題
3. 直接透過 API 測試或是由 AI 寫測試來驗證產出的正確性