/**
 * Extracts a human-readable error message from an Axios error response.
 *
 * Follows the backend envelope:
 * - Success: { timestamp, status, message, data }
 * - Error:   { timestamp, status, error, message, path, details? }
 *
 * Handles 429 rate-limit responses with Retry-After context.
 */
export function extractApiError(error: unknown): string {
  if (error && typeof error === "object" && "response" in error) {
    const axiosError = error as {
      response?: {
        status?: number;
        headers?: Record<string, string>;
        data?: { message?: string };
      };
    };

    if (axiosError.response?.status === 429) {
      const retryAfter = axiosError.response.headers?.["retry-after"];
      if (retryAfter) {
        return `Muitas tentativas. Aguarde ${retryAfter} segundo(s) e tente novamente.`;
      }
      return "Muitas tentativas. Aguarde alguns instantes e tente novamente.";
    }

    const message = axiosError.response?.data?.message;
    if (message) return message;
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
  if (error && typeof error === "object" && "response" in error) {
    const details = (
      error as { response?: { data?: { details?: Record<string, string> } } }
    ).response?.data?.details;
    if (details && Object.keys(details).length > 0) return details;
  }
  return null;
}

/**
 * Returns true if the error is a 429 Too Many Requests response.
 */
export function isRateLimitError(error: unknown): boolean {
  return (
    error != null &&
    typeof error === "object" &&
    "response" in error &&
    (error as { response?: { status?: number } }).response?.status === 429
  );
}

/**
 * Applies a Brazilian phone mask on-the-fly as the user types.
 * Input: raw digits string  →  Output: "(11) 99999-9999" or "(11) 9999-9999"
 */
export function formatPhoneMask(value: string): string {
  const digits = value.replace(/\D/g, "").slice(0, 11);
  if (digits.length <= 2) return digits.length ? `(${digits}` : "";
  if (digits.length <= 6) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
  if (digits.length <= 10) {
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
  }
  // 11 digits — mobile
  return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`;
}

/**
 * Converts a masked/raw phone string to E.164 format (+5511...).
 */
export function formatPhoneToE164(raw: string): string {
  const digits = raw.replace(/\D/g, "");
  if (digits.startsWith("55") && digits.length >= 12) return `+${digits}`;
  if (digits.length === 11) return `+55${digits}`;
  if (digits.length === 10) return `+55${digits}`;
  return `+${digits}`;
}

