-- add fields
ALTER TABLE "email_confirmation" ADD COLUMN user_id UUID;
ALTER TABLE "password_recovery" ADD COLUMN user_id UUID;


-- add ids from table
UPDATE "email_confirmation" SET user_id = usr.id FROM "user" as usr WHERE usr.email_confirmation_id = "email_confirmation".id;

UPDATE "password_recovery" SET user_id = usr.id FROM "user" as usr WHERE usr.password_recovery_id = "password_recovery".id;

-- add constraint
ALTER TABLE "email_confirmation" ADD CONSTRAINT fk_email_confirmation_user_id_user
    FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE "password_recovery" ADD CONSTRAINT fk_password_recovery_user_id_user
    FOREIGN KEY (user_id) REFERENCES "user" (id);

-- column
ALTER TABLE "user" DROP COLUMN "email_confirmation_id";
ALTER TABLE "user" DROP COLUMN "password_recovery_id";

-- drop fk constraint and column
DO $$
    DECLARE
    v_constraint_name TEXT;
    BEGIN
    SELECT con.constraint_name
    INTO v_constraint_name
    FROM information_schema.table_constraints con
             JOIN information_schema.key_column_usage kcu
                  ON con.constraint_name = kcu.constraint_name
    WHERE con.constraint_type = 'PRIMARY KEY'
      AND kcu.table_name = 'email_confirmation'
      AND kcu.column_name = 'id';
    IF v_constraint_name IS NOT NULL THEN
                EXECUTE format('ALTER TABLE email_confirmation DROP CONSTRAINT %I', v_constraint_name);
    ELSE
                RAISE NOTICE 'No pk constraint found for email_confirmation.email_confirmation_pkey';
    END IF;
END $$;

DO $$
    DECLARE
    v_constraint_name TEXT;
    BEGIN
    SELECT con.constraint_name
    INTO v_constraint_name
    FROM information_schema.table_constraints con
             JOIN information_schema.key_column_usage kcu
                  ON con.constraint_name = kcu.constraint_name
    WHERE con.constraint_type = 'PRIMARY KEY'
      AND kcu.table_name = 'password_recovery'
      AND kcu.column_name = 'id';
    IF v_constraint_name IS NOT NULL THEN
                    EXECUTE format('ALTER TABLE password_recovery DROP CONSTRAINT %I', v_constraint_name);
    ELSE
                    RAISE NOTICE 'No pk constraint found for password_recovery.password_recovery_pkey';
    END IF;
END $$;


ALTER TABLE "email_confirmation" DROP COLUMN id;
ALTER TABLE "password_recovery" DROP COLUMN id;

ALTER TABLE "email_confirmation" ADD PRIMARY KEY (user_id);
ALTER TABLE "password_recovery" ADD PRIMARY KEY (user_id);