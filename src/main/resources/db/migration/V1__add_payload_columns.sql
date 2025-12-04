ALTER TABLE integration_events ADD COLUMN payload CLOB;
ALTER TABLE integration_events ADD COLUMN payload_format VARCHAR(10);
ALTER TABLE integration_events ADD COLUMN retry_count INTEGER DEFAULT 0;
ALTER TABLE integration_events ADD COLUMN retry_history CLOB;
ALTER TABLE integration_events ADD COLUMN original_payload CLOB;
