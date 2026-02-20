/**
 * Auth-related DTO types that mirror the backend API contract.
 */

export type UserRole = "CLIENT" | "PROVIDER";

export interface SignupRequest {
  name: string;
  email: string;
  phone: string;
  password: string;
  role: UserRole;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthUser {
  id: string;
  name: string;
  email: string;
  phone: string;
  role: UserRole;
  createdAt: string;
  active: boolean;
}

export interface LoginResponseData {
  accessToken: string;
  expiresIn: number;
}

/** Generic backend envelope: { timestamp, status, message, data } */
export interface ApiSuccessResponse<T> {
  timestamp: string;
  status: number;
  message: string;
  data: T;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

/** Generic backend error envelope: { timestamp, status, error, message, path, details? } */
export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: Record<string, string>;
}

