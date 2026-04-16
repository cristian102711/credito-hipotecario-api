CREATE TABLE IF NOT EXISTS solicitudes_credito (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT              NOT NULL REFERENCES usuarios(id),
    propiedad_id        BIGINT              NOT NULL REFERENCES propiedades(id),
    monto_solicitado    NUMERIC(15,2)       NOT NULL,
    plazo_anos          INTEGER             NOT NULL,
    tasa_interes        NUMERIC(5,4)        NOT NULL,
    dividendo_mensual   NUMERIC(15,2),
    cae                 NUMERIC(5,4),
    estado              VARCHAR(20)         NOT NULL DEFAULT 'BORRADOR',
    observaciones       VARCHAR(500),
    created_at          TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_solicitudes_usuario   ON solicitudes_credito(usuario_id);
CREATE INDEX IF NOT EXISTS idx_solicitudes_propiedad ON solicitudes_credito(propiedad_id);
CREATE INDEX IF NOT EXISTS idx_solicitudes_estado    ON solicitudes_credito(estado);