-- Create conversations table
CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

-- Create conversation_participants table
CREATE TABLE conversation_participants (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    last_read_at TIMESTAMP,
    CONSTRAINT uq_conversation_user UNIQUE (conversation_id, user_id)
);

-- Alter messages table: add conversation_id, drop project_id
ALTER TABLE messages ADD COLUMN conversation_id BIGINT REFERENCES conversations(id) ON DELETE CASCADE;
ALTER TABLE messages DROP COLUMN project_id;

-- Add index for fast unread queries
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_conversation_participants_user_id ON conversation_participants(user_id); 