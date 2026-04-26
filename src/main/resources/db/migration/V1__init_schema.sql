CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       national_id VARCHAR(255) UNIQUE NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       account VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP,
                       deleted_at TIMESTAMP
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          product_name VARCHAR(255) NOT NULL,
                          stock_quantity INTEGER NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP,
                          deleted_at TIMESTAMP
);