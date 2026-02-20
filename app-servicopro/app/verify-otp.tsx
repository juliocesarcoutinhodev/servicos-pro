import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { forgotPassword, resetPassword } from "@/services/apiClient";
import { extractApiError } from "@/utils/apiError";
import { LinearGradient } from "expo-linear-gradient";
import { useLocalSearchParams, useRouter } from "expo-router";
import {
  ArrowLeft,
  CheckCircle,
  KeyRound,
  Lock,
  ShieldCheck,
  Zap,
} from "lucide-react-native";
import React, { useEffect, useRef, useState } from "react";
import {
  Animated,
  Keyboard,
  KeyboardAvoidingView,
  LayoutChangeEvent,
  NativeSyntheticEvent,
  Platform,
  ScrollView,
  Text,
  TextInput,
  TextInputKeyPressEventData,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

const OTP_LENGTH = 6;
const RESEND_COOLDOWN = 60;

/**
 * Verify OTP screen — the user enters the 6-digit code received by email
 * along with a new password to complete the password reset flow.
 *
 * Expects query param: email (passed from /forgot-password)
 * Calls: POST /api/v1/auth/reset-password  { email, code, newPassword }
 */
export default function VerifyOtpScreen() {
  const router = useRouter();
  const { email } = useLocalSearchParams<{ email: string }>();

  const scrollRef = useRef<ScrollView>(null);
  const inputRefs = useRef<(TextInput | null)[]>([]);
  const newPasswordRef = useRef<TextInput>(null);
  const confirmPasswordRef = useRef<TextInput>(null);
  const fieldLayoutsRef = useRef<Record<string, number>>({});
  const fadeAnim = useRef(new Animated.Value(0)).current;

  /** Rola o scroll para que o campo recém-focado fique visível acima do teclado */
  function scrollToField(fieldKey: string) {
    const yOffset = fieldLayoutsRef.current[fieldKey];
    if (yOffset !== undefined) {
      scrollRef.current?.scrollTo({ y: yOffset - 20, animated: true });
    }
  }

  function onFieldLayout(fieldKey: string, e: LayoutChangeEvent) {
    fieldLayoutsRef.current[fieldKey] = e.nativeEvent.layout.y;
  }

  const [digits, setDigits] = useState<string[]>(Array(OTP_LENGTH).fill(""));
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isResending, setIsResending] = useState(false);
  const [success, setSuccess] = useState(false);
  const [countdown, setCountdown] = useState(RESEND_COOLDOWN);
  const [errors, setErrors] = useState<{
    code?: string;
    newPassword?: string;
    confirmPassword?: string;
    general?: string;
  }>({});

  // Countdown timer for resend button
  useEffect(() => {
    if (countdown <= 0) return;
    const timer = setInterval(() => setCountdown((c) => c - 1), 1000);
    return () => clearInterval(timer);
  }, [countdown]);

  // ── OTP input helpers ────────────────────────────────────────────────────

  function handleDigitChange(value: string, index: number) {
    const digit = value.replace(/\D/g, "").slice(-1);
    const next = [...digits];
    next[index] = digit;
    setDigits(next);
    if (errors.code) setErrors((e) => ({ ...e, code: undefined }));
    if (digit && index < OTP_LENGTH - 1) {
      inputRefs.current[index + 1]?.focus();
    }
  }

  function handleKeyPress(
    e: NativeSyntheticEvent<TextInputKeyPressEventData>,
    index: number
  ) {
    if (e.nativeEvent.key === "Backspace" && !digits[index] && index > 0) {
      const next = [...digits];
      next[index - 1] = "";
      setDigits(next);
      inputRefs.current[index - 1]?.focus();
    }
  }

  // ── Password strength ────────────────────────────────────────────────────

  function getStrength(pwd: string): number {
    let score = 0;
    if (pwd.length >= 8) score++;
    if (/[A-Z]/.test(pwd)) score++;
    if (/[0-9]/.test(pwd)) score++;
    if (/[^A-Za-z0-9]/.test(pwd)) score++;
    return score;
  }

  const strength = getStrength(newPassword);
  const strengthLabels = ["", "Fraca", "Razoável", "Boa", "Forte"];
  const strengthColors = ["#E2E8F0", "#EF4444", "#F59E0B", "#3B82F6", "#22C55E"];

  // ── Validation ───────────────────────────────────────────────────────────

  function validate(): boolean {
    const newErrors: typeof errors = {};
    const code = digits.join("");

    if (code.length < OTP_LENGTH) {
      newErrors.code = "Digite os 6 dígitos do código";
    }
    if (!newPassword) {
      newErrors.newPassword = "Informe a nova senha";
    } else if (newPassword.length < 8) {
      newErrors.newPassword = "Mínimo de 8 caracteres";
    } else if (newPassword.length > 72) {
      newErrors.newPassword = "Máximo de 72 caracteres";
    }
    if (!confirmPassword) {
      newErrors.confirmPassword = "Confirme a nova senha";
    } else if (newPassword !== confirmPassword) {
      newErrors.confirmPassword = "As senhas não coincidem";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }

  // ── Actions ──────────────────────────────────────────────────────────────

  async function handleReset() {
    Keyboard.dismiss();
    if (!validate()) return;
    setIsSubmitting(true);
    setErrors({});
    try {
      await resetPassword({
        email: email ?? "",
        code: digits.join(""),
        newPassword: newPassword,
      });
      setSuccess(true);
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 400,
        useNativeDriver: true,
      }).start();
    } catch (err) {
      setErrors({ general: extractApiError(err) });
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleResend() {
    if (countdown > 0 || !email) return;
    setIsResending(true);
    try {
      await forgotPassword({ email });
      setCountdown(RESEND_COOLDOWN);
      setDigits(Array(OTP_LENGTH).fill(""));
      inputRefs.current[0]?.focus();
    } catch {
      // ignore — always reset countdown to avoid enumeration
      setCountdown(RESEND_COOLDOWN);
    } finally {
      setIsResending(false);
    }
  }

  // ── Render ───────────────────────────────────────────────────────────────

  return (
    <SafeAreaView className="flex-1" edges={["top"]}>
      <LinearGradient colors={["#1E40AF", "#3B82F6"]} className="absolute inset-0" />

      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        style={{ flex: 1 }}
        keyboardVerticalOffset={Platform.OS === "ios" ? 0 : 24}
      >
        <ScrollView
          ref={scrollRef}
          className="flex-1"
          contentContainerStyle={{ flexGrow: 1, paddingBottom: 60 }}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="handled"
          bounces={false}
        >
          {/* ── Hero ──────────────────────────────────────────────────── */}
          <View className="px-6 pt-10 pb-8 items-center">
            {!success && (
              <TouchableOpacity
                onPress={() => router.back()}
                className="self-start mb-6 w-10 h-10 rounded-full bg-white/20 items-center justify-center"
                activeOpacity={0.7}
              >
                <ArrowLeft size={20} color="#FFFFFF" />
              </TouchableOpacity>
            )}

            <View className="w-20 h-20 rounded-3xl bg-white/20 items-center justify-center mb-6 shadow-lg">
              <Zap size={40} color="#FFFFFF" fill="#FFFFFF" />
            </View>
            <Text className="text-white text-center text-3xl font-bold mb-2">
              Serviços Pro
            </Text>
            <Text className="text-blue-100 text-center text-base">
              {success ? "Senha redefinida" : "Verificação de código"}
            </Text>
          </View>

          {/* ── Card ──────────────────────────────────────────────────── */}
          <View className="bg-[#F8FAFC] rounded-t-[32px] px-6 pt-10 pb-6 flex-1">
            {!success ? (
              <>
                {/* Header */}
                <View className="items-center mb-8">
                  <View className="w-16 h-16 rounded-full bg-blue-50 items-center justify-center mb-4">
                    <KeyRound size={32} color="#3B82F6" />
                  </View>
                  <Text className="text-center text-2xl font-bold text-gray-900 mb-2">
                    Digite o código
                  </Text>
                  <Text className="text-[#64748B] text-center text-sm leading-5 px-2">
                    Enviamos um código de 6 dígitos para
                  </Text>
                  <Text className="text-gray-900 font-semibold text-sm mt-0.5">
                    {email}
                  </Text>
                </View>

                {/* OTP inputs */}
                <View className="flex-row justify-between mb-2 px-2">
                  {Array.from({ length: OTP_LENGTH }).map((_, i) => (
                    <TextInput
                      key={i}
                      ref={(ref) => { inputRefs.current[i] = ref; }}
                      value={digits[i]}
                      onChangeText={(v) => handleDigitChange(v, i)}
                      onKeyPress={(e) => handleKeyPress(e, i)}
                      keyboardType="number-pad"
                      maxLength={1}
                      selectTextOnFocus
                      style={{
                        width: 46,
                        height: 56,
                        borderRadius: 12,
                        borderWidth: 2,
                        borderColor: digits[i]
                          ? "#3B82F6"
                          : errors.code
                          ? "#EF4444"
                          : "#E2E8F0",
                        backgroundColor: digits[i] ? "#EFF6FF" : "#FFFFFF",
                        textAlign: "center",
                        fontSize: 22,
                        fontWeight: "700",
                        color: "#1E40AF",
                      }}
                    />
                  ))}
                </View>

                {errors.code && (
                  <Text className="text-red-500 text-sm text-center mb-3">
                    {errors.code}
                  </Text>
                )}

                {/* Resend */}
                <View className="flex-row justify-center items-center mb-6">
                  <Text className="text-[#64748B] text-sm">
                    Não recebeu?{" "}
                  </Text>
                  {countdown > 0 ? (
                    <Text className="text-[#94A3B8] text-sm font-medium">
                      Reenviar em {countdown}s
                    </Text>
                  ) : (
                    <TouchableOpacity
                      onPress={handleResend}
                      disabled={isResending}
                      activeOpacity={0.7}
                    >
                      <Text className="text-[#3B82F6] text-sm font-semibold">
                        {isResending ? "Enviando..." : "Reenviar código"}
                      </Text>
                    </TouchableOpacity>
                  )}
                </View>

                {/* Divider */}
                <View className="flex-row items-center gap-3 mb-5">
                  <View className="flex-1 h-px bg-gray-200" />
                  <Text className="text-[#94A3B8] text-xs">nova senha</Text>
                  <View className="flex-1 h-px bg-gray-200" />
                </View>

                {/* Nova senha */}
                <View
                  className="relative mb-1"
                  onLayout={(e) => onFieldLayout("newPassword", e)}
                >
                  <View className="absolute left-4 z-10" style={{ top: 38 }}>
                    <Lock size={18} color="#94A3B8" />
                  </View>
                  <Input
                    ref={newPasswordRef}
                    label="Nova senha"
                    placeholder="Mínimo 8 caracteres"
                    secureTextEntry
                    showPasswordToggle
                    value={newPassword}
                    onChangeText={(v) => {
                      setNewPassword(v);
                      if (errors.newPassword)
                        setErrors((e) => ({ ...e, newPassword: undefined }));
                    }}
                    error={errors.newPassword}
                    inputClassName="pl-11"
                    onFocus={() =>
                      setTimeout(() => scrollToField("newPassword"), 200)
                    }
                    returnKeyType="next"
                    onSubmitEditing={() => confirmPasswordRef.current?.focus()}
                  />
                </View>

                {/* Strength bar */}
                {newPassword.length > 0 && (
                  <View className="mb-4 -mt-2">
                    <View className="flex-row gap-1 mb-1">
                      {[1, 2, 3, 4].map((i) => (
                        <View
                          key={i}
                          className="flex-1 h-1.5 rounded-full"
                          style={{
                            backgroundColor:
                              i <= strength ? strengthColors[strength] : "#E2E8F0",
                          }}
                        />
                      ))}
                    </View>
                    {strength > 0 && (
                      <Text
                        className="text-xs font-medium"
                        style={{ color: strengthColors[strength] }}
                      >
                        Senha {strengthLabels[strength]}
                      </Text>
                    )}
                  </View>
                )}

                {/* Confirmar senha */}
                <View
                  className="relative mb-2"
                  onLayout={(e) => onFieldLayout("confirmPassword", e)}
                >
                  <View className="absolute left-4 z-10" style={{ top: 38 }}>
                    <ShieldCheck size={18} color="#94A3B8" />
                  </View>
                  <Input
                    ref={confirmPasswordRef}
                    label="Confirmar nova senha"
                    placeholder="Repita a nova senha"
                    secureTextEntry
                    showPasswordToggle
                    value={confirmPassword}
                    onChangeText={(v) => {
                      setConfirmPassword(v);
                      if (errors.confirmPassword)
                        setErrors((e) => ({ ...e, confirmPassword: undefined }));
                    }}
                    error={errors.confirmPassword}
                    inputClassName="pl-11"
                    onFocus={() =>
                      setTimeout(() => scrollToField("confirmPassword"), 200)
                    }
                    returnKeyType="done"
                    onSubmitEditing={handleReset}
                  />
                </View>

                {/* Match indicator */}
                {confirmPassword.length > 0 && (
                  <View className="flex-row items-center gap-1.5 mb-4 -mt-2">
                    <View
                      className="w-2 h-2 rounded-full"
                      style={{
                        backgroundColor:
                          newPassword === confirmPassword ? "#22C55E" : "#EF4444",
                      }}
                    />
                    <Text
                      className="text-xs font-medium"
                      style={{
                        color:
                          newPassword === confirmPassword ? "#22C55E" : "#EF4444",
                      }}
                    >
                      {newPassword === confirmPassword
                        ? "Senhas coincidem"
                        : "Senhas não coincidem"}
                    </Text>
                  </View>
                )}

                {/* General error */}
                {errors.general && (
                  <View className="mb-5 px-4 py-3 rounded-xl bg-red-50 border border-red-200">
                    <Text className="text-red-700 text-sm text-center">
                      {errors.general}
                    </Text>
                  </View>
                )}

                <Button
                  onPress={handleReset}
                  variant="primary"
                  size="lg"
                  fullWidth
                  className="mb-4"
                  disabled={isSubmitting}
                  loading={isSubmitting}
                >
                  Redefinir senha
                </Button>

                <Button
                  onPress={() => router.push("/login")}
                  variant="ghost"
                  fullWidth
                >
                  Voltar para o login
                </Button>
              </>
            ) : (
              /* ── Success ──────────────────────────────────────────────── */
              <Animated.View
                style={{ opacity: fadeAnim }}
                className="items-center flex-1 justify-center py-8"
              >
                <View className="w-20 h-20 rounded-full bg-green-50 items-center justify-center mb-6">
                  <CheckCircle size={48} color="#22C55E" />
                </View>
                <Text className="text-center text-2xl font-bold text-gray-900 mb-3">
                  Senha redefinida!
                </Text>
                <Text className="text-[#64748B] text-center text-sm leading-6 px-6 mb-10">
                  Sua senha foi alterada com sucesso. Faça login com sua nova
                  senha para continuar.
                </Text>
                <Button
                  onPress={() => router.push("/login")}
                  variant="primary"
                  size="lg"
                  fullWidth
                >
                  Ir para o login
                </Button>
              </Animated.View>
            )}
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

