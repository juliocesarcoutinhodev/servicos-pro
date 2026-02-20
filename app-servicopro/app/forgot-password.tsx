import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { forgotPassword } from "@/services/apiClient";
import { extractApiError } from "@/utils/apiError";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { ArrowLeft, Mail, Zap } from "lucide-react-native";
import React, { useState } from "react";
import {
  Keyboard,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

/**
 * Forgot Password screen — collects the user's email and calls
 * POST /api/v1/auth/forgot-password.
 *
 * On success, navigates to /verify-otp passing the email so the user
 * can enter the 6-digit OTP code received by email.
 */
export default function ForgotPasswordScreen() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errors, setErrors] = useState<{ email?: string; general?: string }>({});

  function validate(): boolean {
    const newErrors: typeof errors = {};
    if (!email.trim()) {
      newErrors.email = "Informe seu email";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      newErrors.email = "Email inválido";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }

  async function handleSubmit() {
    Keyboard.dismiss();
    if (!validate()) return;
    setIsSubmitting(true);
    setErrors({});
    try {
      await forgotPassword({ email: email.trim().toLowerCase() });
      // Navigate to OTP screen passing the email for the reset step
      router.push({
        pathname: "/verify-otp",
        params: { email: email.trim().toLowerCase() },
      });
    } catch (err) {
      const message = extractApiError(err);
      if (message.startsWith("Muitas tentativas")) {
        setErrors({ general: message });
      } else {
        // Show success anyway to avoid email enumeration (security best-practice)
        router.push({
          pathname: "/verify-otp",
          params: { email: email.trim().toLowerCase() },
        });
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <SafeAreaView className="flex-1" edges={["top"]}>
      <LinearGradient colors={["#1E40AF", "#3B82F6"]} className="absolute inset-0" />

      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        className="flex-1"
      >
        <ScrollView
          className="flex-1"
          contentContainerStyle={{ flexGrow: 1, paddingBottom: 32 }}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="handled"
          bounces={false}
        >
          {/* ── Hero ──────────────────────────────────────────────────── */}
          <View className="px-6 pt-10 pb-8 items-center">
            <TouchableOpacity
              onPress={() => router.back()}
              className="self-start mb-6 w-10 h-10 rounded-full bg-white/20 items-center justify-center"
              activeOpacity={0.7}
            >
              <ArrowLeft size={20} color="#FFFFFF" />
            </TouchableOpacity>

            <View className="w-20 h-20 rounded-3xl bg-white/20 items-center justify-center mb-6 shadow-lg">
              <Zap size={40} color="#FFFFFF" fill="#FFFFFF" />
            </View>
            <Text className="text-white text-center text-3xl font-bold mb-2">
              Serviços Pro
            </Text>
            <Text className="text-blue-100 text-center text-base">
              Recuperação de senha
            </Text>
          </View>

          {/* ── Card ──────────────────────────────────────────────────── */}
          <View className="bg-[#F8FAFC] rounded-t-[32px] px-6 pt-10 pb-6 flex-1">
            <View className="items-center mb-8">
              <View className="w-16 h-16 rounded-full bg-blue-50 items-center justify-center mb-4">
                <Mail size={32} color="#3B82F6" />
              </View>
              <Text className="text-center text-2xl font-bold text-gray-900 mb-2">
                Esqueceu sua senha?
              </Text>
              <Text className="text-[#64748B] text-center text-sm leading-5 px-4">
                Informe seu email e enviaremos um código de 6 dígitos para
                redefinir sua senha.
              </Text>
            </View>

            <View className="relative mb-4">
              <View className="absolute left-4 z-10" style={{ top: 38 }}>
                <Mail size={18} color="#94A3B8" />
              </View>
              <Input
                label="Email"
                placeholder="seu@email.com"
                keyboardType="email-address"
                autoCapitalize="none"
                autoComplete="email"
                value={email}
                onChangeText={(v) => {
                  setEmail(v);
                  if (errors.email) setErrors((e) => ({ ...e, email: undefined }));
                }}
                error={errors.email}
                inputClassName="pl-11"
              />
            </View>

            {errors.general && (
              <View className="mb-5 px-4 py-3 rounded-xl bg-red-50 border border-red-200">
                <Text className="text-red-700 text-sm text-center">
                  {errors.general}
                </Text>
              </View>
            )}

            <Button
              onPress={handleSubmit}
              variant="primary"
              size="lg"
              fullWidth
              className="mb-4"
              disabled={isSubmitting}
              loading={isSubmitting}
            >
              Enviar código
            </Button>

            <Button onPress={() => router.back()} variant="ghost" fullWidth>
              Voltar para o login
            </Button>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
