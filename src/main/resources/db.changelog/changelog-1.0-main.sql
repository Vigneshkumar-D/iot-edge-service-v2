-- Enable UUID extension if not exists
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DO
$$
DECLARE
    super_admin_role_uuid    UUID := uuid_generate_v4();
    admin_role_uuid          UUID := uuid_generate_v4();
    manager_role_uuid        UUID := uuid_generate_v4();
    operator_role_uuid       UUID := uuid_generate_v4();
    guest_role_uuid          UUID := uuid_generate_v4();

    super_admin_user_uuid    UUID := uuid_generate_v4();
    admin_user_uuid          UUID := uuid_generate_v4();
    manager_user_uuid        UUID := uuid_generate_v4();
    operator_user_uuid       UUID := uuid_generate_v4();
    guest_user_uuid          UUID := uuid_generate_v4();

BEGIN
    -- Insert Roles

    CREATE TABLE user_roles (
        id          BIGSERIAL PRIMARY KEY,
        uuid        UUID NOT NULL UNIQUE,
        role_name   VARCHAR(100) NOT NULL,
        active      BOOLEAN DEFAULT true,
        updated_on  TIMESTAMP DEFAULT current_timestamp,
        created_by VARCHAR(255),
        created_at  TIMESTAMP DEFAULT current_timestamp,
        last_modified_by VARCHAR(255),
        last_modified_at TIMESTAMP DEFAULT now()
    );

    -- Create user table
    CREATE TABLE user_details (
        id BIGSERIAL PRIMARY KEY,
        uuid UUID NOT NULL UNIQUE,
        username VARCHAR(255),
        password VARCHAR(255),
        email VARCHAR(255),
        is_email_verified BOOLEAN DEFAULT false,
        is_contact_no_verified BOOLEAN DEFAULT false,
        mobile_number VARCHAR(20),
        role_uuid UUID,
        is_active BOOLEAN DEFAULT true,
        last_login TIMESTAMP,
        avatar_url VARCHAR(500),
        created_by VARCHAR(255),
        created_at  TIMESTAMP DEFAULT current_timestamp,
        last_modified_by VARCHAR(255),
        last_modified_at TIMESTAMP DEFAULT now()
    );

    INSERT INTO user_roles (uuid, role_name, active, created_at, created_by, last_modified_by,  last_modified_at)
    VALUES
        (super_admin_role_uuid, 'Super Admin', true, now(), 'SYSTEM', 'SYSTEM', now()),
        (admin_role_uuid,       'Admin',       true, now(), 'SYSTEM', 'SYSTEM', now()),
        (manager_role_uuid,     'Manager',     true, now(), 'SYSTEM', 'SYSTEM', now()),
        (operator_role_uuid,    'Operator',    true, now(), 'SYSTEM', 'SYSTEM', now()),
        (guest_role_uuid,       'Guest',       true, now(), 'SYSTEM', 'SYSTEM', now());

    -- Insert Users (sample passwords and dummy values used)
   INSERT INTO user_details (uuid, username, password, email, is_email_verified, is_contact_no_verified, mobile_number, role_uuid, is_active, last_login, avatar_url, created_at, created_by, last_modified_by,  last_modified_at)
       VALUES
           (super_admin_user_uuid, 'Super Admin', '{bcrypt}$2a$10$QH3dPNp5Y/rZfsI0KbqSTO4EyGyPO9HclF0TYT2r1Gi.Hfg2zB5Wy', 'super@demo.com', false, false, '+917000000001', super_admin_role_uuid, true, now(), 'https://github.com/shadcn.png', now(), 'SYSTEM', 'SYSTEM', now()),
           (admin_user_uuid,       'Admin',      '{bcrypt}$2a$10$QH3dPNp5Y/rZfsI0KbqSTO4EyGyPO9HclF0TYT2r1Gi.Hfg2zB5Wy', 'admin@demo.com', false, false, '+917000000002', admin_role_uuid, true, now(), 'https://github.com/shadcn.png', now(), 'SYSTEM', 'SYSTEM', now()),
           (manager_user_uuid,     'Manager',    '{bcrypt}$2a$10$QH3dPNp5Y/rZfsI0KbqSTO4EyGyPO9HclF0TYT2r1Gi.Hfg2zB5Wy', 'manager@demo.com', false, false, '+917000000003', manager_role_uuid, true, now(), 'https://github.com/shadcn.png', now(), 'SYSTEM', 'SYSTEM', now()),
           (operator_user_uuid,    'Operator',   '{bcrypt}$2a$10$QH3dPNp5Y/rZfsI0KbqSTO4EyGyPO9HclF0TYT2r1Gi.Hfg2zB5Wy', 'operator@demo.com', false, false, '+917000000004', operator_role_uuid, true, now(), 'https://github.com/shadcn.png', now(), 'SYSTEM', 'SYSTEM', now()),
           (guest_user_uuid,       'Guest',      '{bcrypt}$2a$10$QH3dPNp5Y/rZfsI0KbqSTO4EyGyPO9HclF0TYT2r1Gi.Hfg2zB5Wy', 'guest@demo.com', false, false, '+917000000005', guest_role_uuid, true, now(), 'https://github.com/shadcn.png', now(), 'SYSTEM', 'SYSTEM', now());

   create table system_configuration (
               id               bigserial    not null
                                constraint system_configuration_pk primary key,
               uuid             uuid         not null,
               key              varchar(255) not null,
               value            varchar(255) not null,
               is_deleted       boolean   default false,
               deleted_at       timestamp,
               created_at       timestamp default current_timestamp,
               created_by       varchar(255),
               last_modified_at timestamp,
               last_modified_by varchar(255)
           );

           insert into system_configuration (uuid, key, value, created_at, created_by, last_modified_at, last_modified_by)
           values (uuid_generate_v4(), 'OTP_MAX_RETRY_LIMIT', '3', current_timestamp, 'SYSTEM',
                   current_timestamp, 'SYSTEM');

END;
$$;
