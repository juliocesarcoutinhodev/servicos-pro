import { AuthProvider } from "@/context/AuthContext";
import { Stack } from "expo-router";
import "../global.css";

export default function RootLayout() {
  return (
    <AuthProvider>
      <Stack
        screenOptions={{
          headerShown: false,
          contentStyle: { backgroundColor: "#F8FAFC" },
        }}
      >
        <Stack.Screen name="index" />
        <Stack.Screen name="login" />
        <Stack.Screen name="signup" />
        <Stack.Screen name="forgot-password" />
        <Stack.Screen name="verify-otp" />
        <Stack.Screen name="reset-password" />
        <Stack.Screen name="(client)" />
        <Stack.Screen name="(provider)" />
      </Stack>
    </AuthProvider>
  );
}
