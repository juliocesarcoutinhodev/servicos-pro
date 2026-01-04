import { Stack } from "expo-router";

export default function ProviderLayout() {
  return (
    <Stack
      screenOptions={{
        headerShown: false,
        contentStyle: { backgroundColor: "#F8FAFC" },
      }}
    >
      <Stack.Screen name="home" />
      <Stack.Screen name="requests" />
      <Stack.Screen name="profile" />
    </Stack>
  );
}
