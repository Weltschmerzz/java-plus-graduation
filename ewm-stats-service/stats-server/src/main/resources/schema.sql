CREATE TABLE IF NOT EXISTS endpoint_hit
(
    id       BIGSERIAL PRIMARY KEY,
    app      VARCHAR(255)                NOT NULL,
    uri      VARCHAR(1024)               NOT NULL,
    ip       VARCHAR(64)                 NOT NULL,
    hit_time TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Индексы для быстрых запросов статистики по периоду и URI
CREATE INDEX IF NOT EXISTS idx_endpoint_hit_time
    ON endpoint_hit (hit_time);

CREATE INDEX IF NOT EXISTS idx_endpoint_hit_uri_time
    ON endpoint_hit (uri, hit_time);

CREATE INDEX IF NOT EXISTS idx_endpoint_hit_app_uri_time
    ON endpoint_hit (app, uri, hit_time);
