CREATE TABLE IF NOT EXISTS public.blog
(
    name character varying(500) COLLATE pg_catalog."default",
    description character varying COLLATE pg_catalog."default",
    website_url character varying(400) COLLATE pg_catalog."default",
    is_membership boolean,
    created_at timestamp with time zone,
    user_id uuid,
    id uuid NOT NULL,
    CONSTRAINT blog_pkey PRIMARY KEY (id)
);