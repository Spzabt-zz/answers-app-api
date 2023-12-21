DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS chat CASCADE;
DROP TABLE IF EXISTS chat_message CASCADE;

create table chat
(
    created_at timestamp(6) with time zone,
    id         bigserial not null,
    user_id    bigint unique,
    primary key (id)
);

create table chat_message
(
    chat_id       bigint,
    id            bigserial not null,
    user_question varchar(1000),
    ai_response   varchar(5000),
    primary key (id)
);

create table users
(
    created_at      timestamp(6) with time zone,
    id              bigserial not null,
    activation_code varchar(255),
    email           varchar(255),
    full_name       varchar(255),
    password        varchar(255),
    phone_number    varchar(255),
    role            varchar(255) check (role in ('USER', 'ADMIN')),
    username        varchar(255),
    primary key (id)
);

alter table if exists chat add constraint chat_user_fk foreign key (user_id) references users;

alter table if exists chat_message add constraint chat_message_chat_fk foreign key (chat_id) references chat;