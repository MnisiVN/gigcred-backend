CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    kyc_status VARCHAR(32) NOT NULL,
    risk_tier VARCHAR(32) NOT NULL,
    phone VARCHAR(32) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    provider_ref VARCHAR(128),
    status VARCHAR(32) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    current_balance_cents BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_accounts_user ON accounts(user_id);

CREATE TABLE IF NOT EXISTS beneficiaries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    country_iso VARCHAR(2) NOT NULL,
    method VARCHAR(32) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    phone VARCHAR(32),
    bank_code VARCHAR(32),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_beneficiaries_user ON beneficiaries(user_id);

CREATE TABLE IF NOT EXISTS quotes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    src_cur VARCHAR(3) NOT NULL,
    dst_cur VARCHAR(3) NOT NULL,
    src_amount NUMERIC(18,2) NOT NULL,
    dst_amount NUMERIC(18,2) NOT NULL,
    fx_rate NUMERIC(18,6) NOT NULL,
    provider_fee NUMERIC(18,2) NOT NULL,
    platform_fee NUMERIC(18,2) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_quotes_user ON quotes(user_id);

CREATE TABLE IF NOT EXISTS transfers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quote_id UUID REFERENCES quotes(id),
    user_id UUID NOT NULL REFERENCES users(id),
    source_account_id UUID REFERENCES accounts(id),
    beneficiary_id UUID REFERENCES beneficiaries(id),
    src_amount NUMERIC(18,2) NOT NULL,
    src_cur VARCHAR(3) NOT NULL,
    dst_amount NUMERIC(18,2) NOT NULL,
    dst_cur VARCHAR(3) NOT NULL,
    status VARCHAR(32) NOT NULL,
    provider_ref VARCHAR(128),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_transfers_user ON transfers(user_id);
CREATE INDEX IF NOT EXISTS idx_transfers_status ON transfers(status);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL REFERENCES accounts(id),
    type VARCHAR(32) NOT NULL,
    direction VARCHAR(8) NOT NULL,
    amount_cents BIGINT NOT NULL,
    fee_cents BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL,
    ext_ref VARCHAR(128),
    idempotency_key VARCHAR(128) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    posted_at TIMESTAMPTZ,
    UNIQUE(account_id, idempotency_key)
);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);

CREATE TABLE IF NOT EXISTS balances (
    account_id UUID NOT NULL REFERENCES accounts(id),
    as_of DATE NOT NULL,
    balance_cents BIGINT NOT NULL,
    PRIMARY KEY(account_id, as_of)
);

CREATE TABLE IF NOT EXISTS loans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    principal_cents BIGINT NOT NULL,
    fee_pct NUMERIC(5,2) NOT NULL,
    tenor_days INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    source VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_loans_user ON loans(user_id);

CREATE TABLE IF NOT EXISTS repayments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    loan_id UUID NOT NULL REFERENCES loans(id),
    due_date DATE NOT NULL,
    amount_cents BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_repayments_loan ON repayments(loan_id);

CREATE TABLE IF NOT EXISTS score_snapshots (
    user_id UUID PRIMARY KEY REFERENCES users(id),
    score INT NOT NULL,
    features_json JSONB NOT NULL,
    tier VARCHAR(32) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS idempotency_keys (
    key VARCHAR(128) PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    fingerprint VARCHAR(256) NOT NULL,
    response_hash VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    aggregate VARCHAR(64) NOT NULL,
    aggregate_id VARCHAR(128) NOT NULL,
    type VARCHAR(64) NOT NULL,
    payload_json JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMPTZ,
    tries INT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_outbox_processed ON outbox(processed_at);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    actor VARCHAR(128) NOT NULL,
    action VARCHAR(64) NOT NULL,
    resource VARCHAR(128) NOT NULL,
    correlation_id VARCHAR(128) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    details_json JSONB NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_audit_correlation ON audit_logs(correlation_id);

INSERT INTO users (id, kyc_status, risk_tier, phone, email)
VALUES (uuid_generate_v4(), 'APPROVED', 'LOW', '+27123456789', 'sample@gigcred.com')
ON CONFLICT DO NOTHING;
