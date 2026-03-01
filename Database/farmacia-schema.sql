USE farmacia;

BEGIN;

CREATE TABLE IF NOT EXISTS public."user"
(
    uuid     uuid                     NOT NULL,
    username character varying(30)   NOT NULL,
    email    character varying(50)   NOT NULL,
    password character varying(100)  NOT NULL,
    enabled  boolean                  NOT NULL DEFAULT TRUE,
    PRIMARY KEY (uuid),
    CONSTRAINT "users-username" UNIQUE (username),
    CONSTRAINT "users-email"    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.role
(
    uuid uuid                   NOT NULL,
    name character varying(50) NOT NULL,
    PRIMARY KEY (uuid),
    CONSTRAINT "roles-name" UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.user_role
(
    user_uuid uuid NOT NULL,
    role_uuid uuid NOT NULL,
    PRIMARY KEY (user_uuid, role_uuid)
);

CREATE TABLE IF NOT EXISTS public.person
(
    id         bigint                 NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name  character varying(60),
    cpf        character(11)          NOT NULL,
    user_uuid  uuid,
    PRIMARY KEY (id),
    UNIQUE (user_uuid)
);

CREATE TABLE IF NOT EXISTS public.employee
(
    id               bigint         NOT NULL,
    hiring_date      date           NOT NULL,
    termination_date date,
    salary           numeric(10, 2) NOT NULL,
    person_id        bigint         NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (person_id)
);

CREATE TABLE IF NOT EXISTS public.customer
(
    id                bigint NOT NULL,
    registration_date date   NOT NULL,
    person_id         bigint NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (person_id)
);

CREATE TABLE IF NOT EXISTS public.category
(
    id   bigint                 NOT NULL,
    name character varying(40) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.product
(
    id             bigint                 NOT NULL,
    name           character varying(50) NOT NULL,
    unit_price     numeric(10, 2)         NOT NULL,
    barcode        character varying(20) NOT NULL,
    category_id    bigint                 NOT NULL,
    quantity_stock bigint                 NOT NULL DEFAULT 0,
    due_date       date,
    path_image     character varying(300),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.sale
(
    id             bigint                NOT NULL,
    total_price    numeric(10, 2)        NOT NULL,
    discount       numeric(10, 2),
    payment_method payment_method        NOT NULL,
    customer_id    bigint,
    employee_id    bigint                NOT NULL,
    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS public.sale_product
(
    id         bigint     NOT NULL,
    sale_id    bigint         NOT NULL,
    product_id bigint         NOT NULL,
    quantity   bigint         NOT NULL,
    unit_price numeric(10, 2) NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.user_role
    ADD FOREIGN KEY (user_uuid)
    REFERENCES public."user" (uuid)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.user_role
    ADD FOREIGN KEY (role_uuid)
    REFERENCES public.role (uuid)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.person
    ADD FOREIGN KEY (user_uuid)
    REFERENCES public."user" (uuid)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.employee
    ADD FOREIGN KEY (person_id)
    REFERENCES public.person (id)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.customer
    ADD FOREIGN KEY (person_id)
    REFERENCES public.person (id)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.product
    ADD FOREIGN KEY (category_id)
    REFERENCES public.category (id)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.sale
    ADD FOREIGN KEY (employee_id)
    REFERENCES public.employee (id)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.sale
    ADD FOREIGN KEY (customer_id)
    REFERENCES public.customer (id)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.sale_product
    ADD FOREIGN KEY (sale_id)
    REFERENCES public.sale (id)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.sale_product
    ADD FOREIGN KEY (product_id)
    REFERENCES public.product (id)
    ON UPDATE NO ACTION ON DELETE NO ACTION;

END;
