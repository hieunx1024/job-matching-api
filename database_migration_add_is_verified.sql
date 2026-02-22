-- Migration: Add is_verified column to companies table
-- Purpose: Track whether a company has been verified/approved by admin
-- Date: 2026-02-17

-- Add is_verified column with default value false
ALTER TABLE companies 
ADD COLUMN is_verified BOOLEAN DEFAULT FALSE;

-- Update existing companies to unverified status (optional, already default)
-- UPDATE companies SET is_verified = FALSE WHERE is_verified IS NULL;

-- Add index for faster queries on verification status
CREATE INDEX idx_companies_is_verified ON companies(is_verified);

-- Optional: Add comment to column for documentation
COMMENT ON COLUMN companies.is_verified IS 'Indicates whether the company has been verified/approved by an administrator';
