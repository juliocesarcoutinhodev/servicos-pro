import { AuthProvider } from "@/context/AuthContext";
import { Stack } from "expo-router";
import { LogBox } from "react-native";
import { SafeAreaProvider } from "react-native-safe-area-context";
import "../global.css";

// Warning emitido internamente pelo expo-router/react-navigation que ainda usa
// o SafeAreaView do react-native. Não é código nosso — pode ser ignorado com segurança.
LogBox.ignoreLogs([
  "SafeAreaView has been deprecated",
]);

export default function RootLayout() {
  return (
    <SafeAreaProvider>
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
    </SafeAreaProvider>
  );
}
