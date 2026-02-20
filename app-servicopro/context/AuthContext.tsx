import { STORAGE_KEYS } from "@/constants/config";
import { fetchMe, login, logout, signup } from "@/services/apiClient";
import { AuthUser, LoginRequest, SignupRequest } from "@/types/auth";
import * as SecureStore from "expo-secure-store";
import { useRouter, useSegments } from "expo-router";
import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from "react";

// ── Types ───────────────────────────────────────────────────────────────────

interface AuthState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

interface AuthContextValue extends AuthState {
  signIn: (payload: LoginRequest) => Promise<void>;
  signUp: (payload: SignupRequest) => Promise<void>;
  signOut: () => Promise<void>;
}

// ── Context ─────────────────────────────────────────────────────────────────

const AuthContext = createContext<AuthContextValue | null>(null);

// ── Route guard ─────────────────────────────────────────────────────────────

/**
 * Redirects the user based on authentication state and their role.
 * - Unauthenticated users are sent to /login
 * - Authenticated CLIENTs → /(client)/home
 * - Authenticated PROVIDERs → /(provider)/home
 */
function useProtectedRoute(user: AuthUser | null, isLoading: boolean) {
  const segments = useSegments();
  const router = useRouter();

  useEffect(() => {
    if (isLoading) return;

    const inAuthGroup =
      segments[0] === "login" ||
      segments[0] === "signup" ||
      segments[0] === undefined;

    if (!user && !inAuthGroup) {
      router.replace("/login");
    } else if (user) {
      const targetGroup =
        user.role === "CLIENT" ? "(client)" : "(provider)";
      const inCorrectGroup = segments[0] === targetGroup;

      if (inAuthGroup || !inCorrectGroup) {
        router.replace(
          user.role === "CLIENT" ? "/(client)/home" : "/(provider)/home"
        );
      }
    }
  }, [user, segments, isLoading, router]);
}

// ── Provider ─────────────────────────────────────────────────────────────────

/**
 * AuthProvider bootstraps the session from SecureStore and exposes
 * authentication actions (signIn, signUp, signOut) to the entire app.
 */
export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AuthState>({
    user: null,
    isAuthenticated: false,
    isLoading: true,
  });

  // Bootstrap: restore session from secure storage
  useEffect(() => {
    async function bootstrap() {
      try {
        const [storedToken, storedUser] = await Promise.all([
          SecureStore.getItemAsync(STORAGE_KEYS.ACCESS_TOKEN),
          SecureStore.getItemAsync(STORAGE_KEYS.USER),
        ]);

        if (storedToken && storedUser) {
          const user: AuthUser = JSON.parse(storedUser);
          // Re-validate with the server to ensure the session is still active
          try {
            const freshUser = await fetchMe();
            setState({ user: freshUser, isAuthenticated: true, isLoading: false });
          } catch {
            // Token may be expired — try silent refresh (handled by interceptor)
            // If refresh also fails the interceptor clears storage
            setState({ user, isAuthenticated: true, isLoading: false });
          }
        } else {
          setState((s) => ({ ...s, isLoading: false }));
        }
      } catch {
        setState((s) => ({ ...s, isLoading: false }));
      }
    }
    bootstrap();
  }, []);

  useProtectedRoute(state.user, state.isLoading);

  // ── Actions ────────────────────────────────────────────────────────────────

  const signIn = useCallback(async (payload: LoginRequest) => {
    const { user } = await login(payload);
    await SecureStore.setItemAsync(STORAGE_KEYS.USER, JSON.stringify(user));
    setState({ user, isAuthenticated: true, isLoading: false });
  }, []);

  const signUp = useCallback(async (payload: SignupRequest) => {
    await signup(payload);
    // After signup, automatically log in
    await signIn({ email: payload.email, password: payload.password });
  }, [signIn]);

  const signOut = useCallback(async () => {
    await logout();
    setState({ user: null, isAuthenticated: false, isLoading: false });
  }, []);

  return (
    <AuthContext.Provider
      value={{
        ...state,
        signIn,
        signUp,
        signOut,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

// ── Hook ─────────────────────────────────────────────────────────────────────

/**
 * useAuth provides access to authentication state and actions.
 * Must be used inside <AuthProvider>.
 */
export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an <AuthProvider>");
  }
  return context;
}

