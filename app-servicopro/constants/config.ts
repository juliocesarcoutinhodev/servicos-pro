/**
 * Application-wide configuration constants.
 * Uses production base URL as local backend is not running.
 */
export const API_BASE_URL = "http://vps7348.integrator.host:8080";

export const API_ROUTES = {
  AUTH: {
    SIGNUP: "/api/v1/auth/signup",
    LOGIN: "/api/v1/auth/login",
    REFRESH: "/api/v1/auth/refresh",
    LOGOUT: "/api/v1/auth/logout",
    ME: "/api/v1/auth/me",
  },
} as const;

/** Access token TTL in seconds (15 min) — mirrors backend config */
export const ACCESS_TOKEN_TTL_SECONDS = 900;

/** Secure storage keys */
export const STORAGE_KEYS = {
  ACCESS_TOKEN: "servicepro_access_token",
  USER: "servicepro_user",
} as const;

