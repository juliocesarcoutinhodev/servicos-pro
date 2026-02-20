import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { forgotPassword } from "@/services/apiClient";
import { extractApiError } from "@/utils/apiError";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { ArrowLeft, CheckCircle, Mail, Zap } from "lucide-react-native";
import React, { useRef, useState } from "react";
import {
  Animated,
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
 * The backend always returns 202 regardless of whether the email exists,
 * so we show a generic success state to avoid user enumeration.
 */
export default function ForgotPasswordScreen() {
  const router = useRouter();
  const scrollViewRef = useRef<ScrollView>(null);
  const fadeAnim = useRef(new Animated.Value(0)).current;

  const [email, setEmail] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [errors, setErrors] = useState<{ email?: string; general?: string }>(
    {}
  );

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
      setSubmitted(true);
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 400,
        useNativeDriver: true,
      }).start();
    } catch (err) {
      // Even if the server returns an error other than rate-limit,
      // show the generic success to avoid enumeration — unless it's 429.
      const message = extractApiError(err);
      if (message.startsWith("Muitas tentativas")) {
        setErrors({ general: message });
      } else {
        // Show success anyway (security best-practice)
        setSubmitted(true);
        Animated.timing(fadeAnim, {
          toValue: 1,
          duration: 400,
          useNativeDriver: true,
        }).start();
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <SafeAreaView className="flex-1" edges={["top"]}>
      <LinearGradient
        colors={["#1E40AF", "#3B82F6"]}
        className="absolute inset-0"
      />

      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        className="flex-1"
      >
        <ScrollView
          ref={scrollViewRef}
          className="flex-1"
          contentContainerStyle={{ flexGrow: 1, paddingBottom: 32 }}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="handled"
          bounces={false}
        >
          {/* ── Hero ──────────────────────────────────────────────────── */}
          <View className="px-6 pt-10 pb-8 items-center">
            {/* Back button */}
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
            {!submitted ? (
              <>
                {/* Icon */}
                <View className="items-center mb-6">
                  <View className="w-16 h-16 rounded-full bg-blue-50 items-center justify-center mb-4">
                    <Mail size={32} color="#3B82F6" />
                  </View>
                  <Text className="text-center text-2xl font-bold text-gray-900 mb-2">
                    Esqueceu sua senha?
                  </Text>
                  <Text className="text-[#64748B] text-center text-sm leading-5 px-4">
                    Não se preocupe! Informe o email cadastrado e enviaremos um
                    link para criar uma nova senha.
                  </Text>
                </View>

                {/* Email field */}
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
                      if (errors.email)
                        setErrors((e) => ({ ...e, email: undefined }));
                    }}
                    error={errors.email}
                    inputClassName="pl-11"
                  />
                </View>

                {/* General error (rate-limit, etc.) */}
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
                  Enviar link de recuperação
                </Button>

                <Button
                  onPress={() => router.back()}
                  variant="ghost"
                  fullWidth
                >
                  Voltar para o login
                </Button>
              </>
            ) : (
              /* ── Success state ──────────────────────────────────────── */
              <Animated.View
                style={{ opacity: fadeAnim }}
                className="items-center flex-1 justify-center py-8"
              >
                <View className="w-20 h-20 rounded-full bg-green-50 items-center justify-center mb-6">
                  <CheckCircle size={48} color="#22C55E" />
                </View>

                <Text className="text-center text-2xl font-bold text-gray-900 mb-3">
                  Email enviado!
                </Text>
                <Text className="text-[#64748B] text-center text-sm leading-6 px-6 mb-2">
                  Se o endereço{" "}
                  <Text className="font-semibold text-gray-800">{email}</Text>{" "}
                  estiver cadastrado, você receberá um email com as instruções
                  para redefinir sua senha.
                </Text>
                <Text className="text-[#94A3B8] text-center text-xs leading-5 px-8 mb-10">
                  Não esqueça de verificar sua caixa de spam caso não encontre
                  o email na caixa de entrada.
                </Text>

                <Button
                  onPress={() => router.push("/login")}
                  variant="primary"
                  size="lg"
                  fullWidth
                  className="mb-3"
                >
                  Voltar para o login
                </Button>

                <TouchableOpacity
                  onPress={() => {
                    setSubmitted(false);
                    fadeAnim.setValue(0);
                  }}
                  activeOpacity={0.7}
                >
                  <Text className="text-[#3B82F6] text-sm font-medium">
                    Tentar com outro email
                  </Text>
                </TouchableOpacity>
              </Animated.View>
            )}
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

