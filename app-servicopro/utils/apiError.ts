/**
 * Extracts a human-readable error message from an Axios error response.
 *
 * Follows the backend envelope:
 * - Success: { timestamp, status, message, data }
 * - Error:   { timestamp, status, error, message, path, details? }
 */
export function extractApiError(error: unknown): string {
  if (
    error &&
    typeof error === "object" &&
    "response" in error &&
    (error as { response?: { data?: { message?: string } } }).response?.data
      ?.message
  ) {
    return (error as { response: { data: { message: string } } }).response.data
      .message;
  }
  return "Ocorreu um erro inesperado. Tente novamente.";
}

/**
 * Extracts field-level validation errors from the backend `details` map.
 * Returns an object keyed by field name.
 */
export function extractFieldErrors(
  error: unknown
): Record<string, string> | null {
  if (
    error &&
    typeof error === "object" &&
    "response" in error
  ) {
    const details = (
      error as { response?: { data?: { details?: Record<string, string> } } }
    ).response?.data?.details;
    if (details && Object.keys(details).length > 0) return details;
  }
  return null;
}

/** Validates an E.164 phone number format (+5511999999999) */
export function formatPhoneToE164(raw: string): string {
  const digits = raw.replace(/\D/g, "");
  if (digits.startsWith("55") && digits.length >= 12) return `+${digits}`;
  if (digits.length === 11) return `+55${digits}`;
  if (digits.length === 10) return `+55${digits}`;
  return `+${digits}`;
}

