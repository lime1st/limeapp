INSERT INTO LA_MEMBER(MEMBER_ID, EMAIL, USERNAME,  PASSWORD, ENABLED, ROLE, CREATED_AT, UPDATED_AT)
    VALUES ('99', 'alice@mail.com', 'alice', 'password', true, 'USER', NOW(), NOW());
INSERT INTO LA_MEMBER(MEMBER_ID, EMAIL, USERNAME,  PASSWORD, ENABLED, ROLE, CREATED_AT, UPDATED_AT)
    VALUES ('100', 'bob@mail.com', 'bob', '1234', true, 'GUEST', NOW(), NOW());
INSERT INTO LA_MEMBER(MEMBER_ID, EMAIL, USERNAME,  PASSWORD, ENABLED, ROLE, CREATED_AT, UPDATED_AT)
    VALUES ('101', 'john@mail.com', 'john', '5678', false, 'ADMIN', NOW(), NOW());


INSERT INTO LA_BLOG(ID, TITLE, CONTENT, CREATED_AT, UPDATED_AT, AUTHOR)
    VALUES (11, 'title1', 'content1', NOW(), NOW(), 'alice');
INSERT INTO LA_BLOG(ID, TITLE, CONTENT, CREATED_AT, UPDATED_AT, AUTHOR)
    VALUES (12, 'title2', 'content2', NOW(), NOW(), 'alice');
INSERT INTO LA_BLOG(ID, TITLE, CONTENT, CREATED_AT, UPDATED_AT, AUTHOR)
    VALUES (13, 'title3', 'content3', NOW(), NOW(), 'alice');
INSERT INTO LA_BLOG(ID, TITLE, CONTENT, CREATED_AT, UPDATED_AT, AUTHOR)
    VALUES (14, 'title1', 'content1', NOW(), NOW(), 'bob');
INSERT INTO LA_BLOG(ID, TITLE, CONTENT, CREATED_AT, UPDATED_AT, AUTHOR)
    VALUES (15, 'title2', 'content2', NOW(), NOW(), 'bob');
INSERT INTO LA_BLOG(ID, TITLE, CONTENT, CREATED_AT, UPDATED_AT, AUTHOR)
    VALUES (16, 'title1', 'content1', NOW(), NOW(), 'john');