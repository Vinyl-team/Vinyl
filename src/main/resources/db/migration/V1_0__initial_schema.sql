create table users
(
    id         serial        not null
        constraint users_pkey
            primary key,
    email      varchar(500)  not null
        constraint unique_email
            unique,
    salt       varchar(500)  not null,
    iterations integer       not null,
    password   varchar(1000) not null
        constraint unique_password
            unique,
    status     boolean       not null,
    role       varchar(50)   not null
        constraint either_of_two_roles
            check (((role)::text = 'USER'::text) OR ((role)::text = 'ADMIN'::text)),
    discogs_user_name      varchar(500)
        constraint unique_discogs_user_name
            unique
);

create table unique_vinyls
(
    id            bigint        not null
        constraint unique_id
            unique,
    release       varchar(200)  not null,
    artist        varchar(200)  not null,
    full_name     varchar(400)  not null,
    link_to_image varchar(1000) not null
);

create table shops
(
    id                serial       not null
        constraint shops_pkey
            primary key,
    link_to_main_page varchar(500) not null,
    link_to_image     varchar(500) not null,
    name              varchar(500) not null
);

create table user_posts
(
    id      serial        not null
        constraint user_posts_pkey
            primary key,
    user_id integer       not null
        constraint user_id_fk
            references users,
    email   varchar(100)  not null,
    name    varchar(500)  not null,
    theme   varchar(500)  not null,
    message varchar(1000) not null
);

create table confirmation_links
(
    id                serial       not null
        constraint confirmation_links_pkey
            primary key,
    user_id           integer      not null
        constraint user_id_fk
            references users,
    confirmation_link varchar(500) not null,
    date_and_time timestamp without time zone not null
);

create table vinyls
(
    id              serial        not null
        constraint vinyls_pkey
            primary key,
    release         varchar(200)  not null,
    artist          varchar(200)  not null,
    full_name       varchar(400)  not null,
    genre           varchar(100),
    price           double precision not null,
    currency        character varying(50) COLLATE pg_catalog."default" NOT NULL,
    link_to_vinyl   varchar(1000) not null,
    link_to_image   varchar(1000) not null,
    shop_id         integer       not null
        constraint shop_id_fk
            references shops
        constraint chk_shop_id
            check (shop_id > 0),
    unique_vinyl_id integer       not null
        constraint unique_vinyl_id_fk
            references unique_vinyls (id)
        constraint chk_unique_vinyl_id
            check (unique_vinyl_id > 0)
);

create table vinyls_browsing_history
(
    id              serial  not null
        constraint vinyls_browsing_history_pkey
            primary key,
    user_id         integer not null
        constraint user_id_fk
            references users,
    unique_vinyl_id integer not null
        constraint unique_vinyls_id_fk
            references unique_vinyls (id)
);

insert into shops (link_to_main_page, link_to_image, name)
values ('http://vinyl.ua/', 'img/shops/Vinyl_ua_logo.png', 'VinylUa');

insert into shops (link_to_main_page, link_to_image, name)
values ('https://www.juno.co.uk/', 'https://www.logosvgpng.com/wp-content/uploads/2018/04/juno-records-logo-vector.png', 'JunoCoUk');

CREATE OR REPLACE FUNCTION public.system_rows(
    internal)
    RETURNS tsm_handler
    LANGUAGE 'c'
    COST 1
    VOLATILE STRICT
AS '$libdir/tsm_system_rows', 'tsm_system_rows_handler'
;



