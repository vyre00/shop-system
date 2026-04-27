CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        account VARCHAR(255) NOT NULL,
                        total_amount NUMERIC(12, 2) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id),
                             product_id BIGINT NOT NULL,
                             product_name VARCHAR(255) NOT NULL,
                             quantity INTEGER NOT NULL,
                             unit_price NUMERIC(12, 2) NOT NULL,
                             total_price NUMERIC(12, 2) NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX index_orders_account ON orders(account);
CREATE INDEX index_order_items_order_id ON order_items(order_id);