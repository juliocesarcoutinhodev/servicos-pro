import { API_BASE_URL, STORAGE_KEYS } from "@/constants/config";
import {
  ApiSuccessResponse,
  AuthUser,
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponseData,
  ResetPasswordRequest,
  SignupRequest,
} from "@/types/auth";
import {
  CreateProviderServiceRequest,
  CreateServiceCategoryRequest,
  Page,
  ProviderProfile,
  ProviderReview,
  ProviderService,
  ProviderSummary,
  ServiceCategory,
  UpdateProviderServiceRequest,
} from "@/types";
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

/**
 * Sends a password reset link to the provided email address.
 * Backend always returns 202 regardless of whether the email exists (security).
 */
export async function forgotPassword(
  payload: ForgotPasswordRequest
): Promise<void> {
  await apiClient.post("/api/v1/auth/forgot-password", payload);
}

/**
 * Resets the user's password using the token received via email link.
 */
export async function resetPassword(
  payload: ResetPasswordRequest
): Promise<void> {
  await apiClient.post("/api/v1/auth/reset-password", payload);
}

// ── Service Categories ──────────────────────────────────────────────────────

/**
 * Returns all available service categories (public endpoint).
 */
export async function listServiceCategories(): Promise<ServiceCategory[]> {
  const { data } = await apiClient.get<ApiSuccessResponse<ServiceCategory[]>>(
    "/api/v1/services/categories"
  );
  return data.data;
}

/**
 * Creates a new service category (requires authentication).
 */
export async function createServiceCategory(
  payload: CreateServiceCategoryRequest
): Promise<ServiceCategory> {
  const { data } = await apiClient.post<ApiSuccessResponse<ServiceCategory>>(
    "/api/v1/services/categories",
    payload
  );
  return data.data;
}

// ── Provider Services ───────────────────────────────────────────────────────

/**
 * Returns the authenticated provider's registered services.
 */
export async function listMyProviderServices(): Promise<ProviderService[]> {
  const { data } = await apiClient.get<ApiSuccessResponse<ProviderService[]>>(
    "/api/v1/providers/services"
  );
  return data.data;
}

/**
 * Creates a new service for the authenticated provider.
 */
export async function createProviderService(
  payload: CreateProviderServiceRequest
): Promise<ProviderService> {
  const { data } = await apiClient.post<ApiSuccessResponse<ProviderService>>(
    "/api/v1/providers/services",
    payload
  );
  return data.data;
}

/**
 * Updates an existing provider service by id.
 */
export async function updateProviderService(
  id: string,
  payload: UpdateProviderServiceRequest
): Promise<ProviderService> {
  const { data } = await apiClient.put<ApiSuccessResponse<ProviderService>>(
    `/api/v1/providers/services/${id}`,
    payload
  );
  return data.data;
}

/**
 * Deletes a provider service by id.
 */
export async function deleteProviderService(id: string): Promise<void> {
  await apiClient.delete(`/api/v1/providers/services/${id}`);
}

// ── Public Provider Listing ─────────────────────────────────────────────────

/**
 * Lists active providers, optionally filtered by categoryId.
 * Used by the client home screen and professional listing.
 */
export async function listProviders(params?: {
  categoryId?: string;
  page?: number;
  size?: number;
}): Promise<Page<ProviderSummary>> {
  const { data } = await apiClient.get<ApiSuccessResponse<Page<ProviderSummary>>>(
    "/api/v1/providers",
    { params: { page: 0, size: 10, ...params } }
  );
  return data.data;
}

/**
 * Returns the full public profile of a provider, including their services list.
 */
export async function getProviderProfile(id: string): Promise<ProviderProfile> {
  const { data } = await apiClient.get<ApiSuccessResponse<ProviderProfile>>(
    `/api/v1/providers/${id}`
  );
  return data.data;
}

/**
 * Returns paginated reviews for a provider.
 */
export async function listProviderReviews(
  id: string,
  params?: { page?: number; size?: number }
): Promise<Page<ProviderReview>> {
  const { data } = await apiClient.get<ApiSuccessResponse<Page<ProviderReview>>>(
    `/api/v1/providers/${id}/reviews`,
    { params: { page: 0, size: 10, ...params } }
  );
  return data.data;
}

export default apiClient;

