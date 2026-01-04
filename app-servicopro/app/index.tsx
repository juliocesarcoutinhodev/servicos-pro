import { Redirect } from "expo-router";

export default function Index() {
  // Redirect to login screen as entry point
  return <Redirect href="/login" />;
}
