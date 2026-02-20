import { API_BASE_URL, STORAGE_KEYS } from "@/constants/config";
import {
  ApiSuccessResponse,
  AuthUser,
  LoginRequest,
  LoginResponseData,
  SignupRequest,
} from "@/types/auth";
import axios, {
  AxiosError,
  AxiosInstance,
  InternalAxiosRequestConfig,
} from "axios";
import * as SecureStore from "expo-secure-store";

/**
 * Flag to prevent multiple concurrent refresh calls.
 * If a refresh is in-flight, subsequent 401s are queued
 * and resolved/rejected once the refresh settles.
 */
let isRefreshing = false;
let failedQueue: {
  resolve: (token: string) => void;
  reject: (err: unknown) => void;
}[] = [];

function processQueue(error: unknown, token: string | null) {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error);
    } else {
      resolve(token!);
    }
  });
  failedQueue = [];
}

/**
 * Central Axios instance for the ServicePro API.
 *
 * Interceptors handle:
 * - Attaching Bearer token to every request
 * - Automatic silent token refresh on 401 (token rotation)
 * - Queuing concurrent requests while refresh is in-flight
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15_000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  /**
   * withCredentials=true is required so the browser/RN sends the HttpOnly
   * refresh_token cookie on POST /auth/refresh and POST /auth/logout.
   */
  withCredentials: true,
});

// ── Request interceptor — attach access token ──────────────────────────────

apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    const token = await SecureStore.getItemAsync(STORAGE_KEYS.ACCESS_TOKEN);
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ── Response interceptor — silent refresh on 401 ───────────────────────────

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };

    const is401 = error.response?.status === 401;
    const isAuthEndpoint =
      originalRequest?.url?.includes("/auth/refresh") ||
      originalRequest?.url?.includes("/auth/login") ||
      originalRequest?.url?.includes("/auth/signup");
    const alreadyRetried = originalRequest?._retry;

    // Auth endpoints that return 401 as a business error (e.g. invalid credentials)
    // must NOT trigger the silent refresh flow — propagate the error as-is.
    if (!is401 || isAuthEndpoint || alreadyRetried) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      // Queue this request until the in-flight refresh settles
      return new Promise((resolve, reject) => {
        failedQueue.push({
          resolve: (token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(apiClient(originalRequest));
          },
          reject,
        });
      });
    }

    originalRequest._retry = true;
    isRefreshing = true;

    try {
      const { data } = await apiClient.post<
        ApiSuccessResponse<LoginResponseData>
      >("/api/v1/auth/refresh");

      const newToken = data.data.accessToken;
      await SecureStore.setItemAsync(STORAGE_KEYS.ACCESS_TOKEN, newToken);

      processQueue(null, newToken);
      originalRequest.headers.Authorization = `Bearer ${newToken}`;
      return apiClient(originalRequest);
    } catch (refreshError) {
      processQueue(refreshError, null);
      // Clear stored credentials — consumer (AuthContext) will redirect to login
      await SecureStore.deleteItemAsync(STORAGE_KEYS.ACCESS_TOKEN);
      await SecureStore.deleteItemAsync(STORAGE_KEYS.USER);
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  }
);

// ── Auth service methods ────────────────────────────────────────────────────

/**
 * Registers a new user (CLIENT or PROVIDER).
 */
export async function signup(
  payload: SignupRequest
): Promise<ApiSuccessResponse<AuthUser>> {
  const { data } = await apiClient.post<ApiSuccessResponse<AuthUser>>(
    "/api/v1/auth/signup",
    payload
  );
  return data;
}

/**
 * Authenticates the user, persists the access token and returns user data.
 * The refresh token is automatically stored as an HttpOnly cookie by the server.
 *
 * Backend returns: { data: { accessToken, expiresIn } }
 * User data is fetched via GET /auth/me after storing the token.
 */
export async function login(
  payload: LoginRequest
): Promise<{ user: AuthUser; accessToken: string }> {
  const { data } = await apiClient.post<ApiSuccessResponse<LoginResponseData>>(
    "/api/v1/auth/login",
    payload
  );

  const { accessToken } = data.data;
  await SecureStore.setItemAsync(STORAGE_KEYS.ACCESS_TOKEN, accessToken);

  // Fetch user profile now that the token is stored
  const user = await fetchMe();
  await SecureStore.setItemAsync(STORAGE_KEYS.USER, JSON.stringify(user));

  return { user, accessToken };
}

/**
 * Fetches the authenticated user's profile.
 */
export async function fetchMe(): Promise<AuthUser> {
  const { data } =
    await apiClient.get<ApiSuccessResponse<AuthUser>>("/api/v1/auth/me");
  return data.data;
}

/**
 * Clears the access token locally and invalidates the refresh cookie on the server.
 */
export async function logout(): Promise<void> {
  try {
    await apiClient.post("/api/v1/auth/logout");
  } finally {
    await SecureStore.deleteItemAsync(STORAGE_KEYS.ACCESS_TOKEN);
    await SecureStore.deleteItemAsync(STORAGE_KEYS.USER);
  }
}

export default apiClient;

