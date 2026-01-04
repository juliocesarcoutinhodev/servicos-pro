import { Stack } from "expo-router";

export default function ClientLayout() {
  return (
    <Stack
      screenOptions={{
        headerShown: false,
        contentStyle: { backgroundColor: "#F8FAFC" },
      }}
    >
      <Stack.Screen name="home" />
      <Stack.Screen name="categories" />
      <Stack.Screen name="professionals" />
      <Stack.Screen name="professional-profile" />
      <Stack.Screen name="service-request" />
      <Stack.Screen name="payment" />
    </Stack>
  );
}
