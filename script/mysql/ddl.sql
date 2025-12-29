create table book (
    id bigint auto_increment primary key,
    title varchar(255) not null,
    author varchar(255) not null ,
    status varchar(30) not null,
    category_id bigint not null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp
);

create table category (
    id bigint auto_increment primary key,
    name varchar(255) not null,
    status varchar(30) not null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp
);

alter table book add constraint fk_book_category foreign key (category_id) references category(id);

alter table book add constraint uk_book_title unique (title, author);
alter table category add constraint uk_category_name unique (name);

alter table book add index idx_book_category_created_id (category_id, created_at);
alter table book add index idx_book_status_created_id (status, created_at);
alter table category add index idx_category_status (status);
