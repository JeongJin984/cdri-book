CREATE SCHEMA IF NOT EXISTS cdri_books;
SET SCHEMA cdri_books;

CREATE TABLE book (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      status VARCHAR(30) NOT NULL,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE book_category_map (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   book_id BIGINT NOT NULL,
                                   category_id BIGINT NOT NULL,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   CONSTRAINT fk_book_category_map_book FOREIGN KEY (book_id) REFERENCES book(id),
                                   CONSTRAINT fk_book_category_map_category FOREIGN KEY (category_id) REFERENCES category(id),
                                   CONSTRAINT uk_book_category_map UNIQUE (book_id, category_id)
);

ALTER TABLE book ADD CONSTRAINT uk_book_title UNIQUE (title, author);
ALTER TABLE category ADD CONSTRAINT uk_category_name UNIQUE (name);

CREATE INDEX idx_book_category_map_book_id ON book_category_map(book_id, created_at);
CREATE INDEX idx_book_created_id ON book(created_at, id);
CREATE INDEX idx_book_status_created_id ON book(status, created_at, id);
CREATE INDEX idx_category_status ON category(status);
CREATE INDEX idx_bcm_category_book ON book_category_map(category_id, book_id);