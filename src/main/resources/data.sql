INSERT INTO GENRES (GENRE_NAME)
SELECT g.genre_name
FROM (VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик')
) AS g (genre_name)
WHERE NOT EXISTS (
    SELECT 1
    FROM GENRES
    WHERE GENRE_NAME = g.genre_name
);

INSERT INTO MPA (MPA_NAME)
SELECT m.mpa_name
FROM (VALUES
       ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17')
) AS m (mpa_name)
WHERE NOT EXISTS (
    SELECT 1
    FROM MPA
    WHERE MPA_NAME = m.mpa_name
       );