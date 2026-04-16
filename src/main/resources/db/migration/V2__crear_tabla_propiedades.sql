CREATE TABLE IF NOT EXISTS propiedades (
    id              BIGSERIAL PRIMARY KEY,
    titulo          VARCHAR(200)        NOT NULL,
    descripcion     VARCHAR(1000)       NOT NULL,
    direccion       VARCHAR(255)        NOT NULL,
    comuna          VARCHAR(100)        NOT NULL,
    region          VARCHAR(100)        NOT NULL,
    precio          NUMERIC(15,2)       NOT NULL,
    superficie_m2   DOUBLE PRECISION    NOT NULL,
    dormitorios     INTEGER             NOT NULL,
    banos           INTEGER             NOT NULL,
    tipo            VARCHAR(30)         NOT NULL,
    estado          VARCHAR(20)         NOT NULL DEFAULT 'DISPONIBLE',
    created_at      TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_propiedades_estado  ON propiedades(estado);
CREATE INDEX IF NOT EXISTS idx_propiedades_comuna  ON propiedades(comuna);
CREATE INDEX IF NOT EXISTS idx_propiedades_tipo    ON propiedades(tipo);
CREATE INDEX IF NOT EXISTS idx_propiedades_precio  ON propiedades(precio);