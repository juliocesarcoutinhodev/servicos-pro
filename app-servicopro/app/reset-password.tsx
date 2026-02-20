import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { resetPassword } from "@/services/apiClient";
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
 * Reset Password screen — receives the `token` from the deep link query param
 * and calls POST /api/v1/auth/reset-password.
 *
 * Expected deep link: servicepro://reset-password?token=<token>
 * or web redirect: /reset-password?token=<token>
 */
export default function ResetPasswordScreen() {
  const router = useRouter();
  const { token } = useLocalSearchParams<{ token?: string }>();
  const scrollViewRef = useRef<ScrollView>(null);
  const fadeAnim = useRef(new Animated.Value(0)).current;

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);
  const [errors, setErrors] = useState<{
    newPassword?: string;
    confirmPassword?: string;
    general?: string;
  }>({});

  /** Password strength: 0–4 */
  function getPasswordStrength(pwd: string): number {
    let score = 0;
    if (pwd.length >= 8) score++;
    if (/[A-Z]/.test(pwd)) score++;
    if (/[0-9]/.test(pwd)) score++;
    if (/[^A-Za-z0-9]/.test(pwd)) score++;
    return score;
  }

  const strength = getPasswordStrength(newPassword);
  const strengthLabels = ["", "Fraca", "Razoável", "Boa", "Forte"];
  const strengthColors = [
    "#E2E8F0",
    "#EF4444",
    "#F59E0B",
    "#3B82F6",
    "#22C55E",
  ];

  function validate(): boolean {
    const newErrors: typeof errors = {};

    if (!token) {
      newErrors.general =
        "Token de redefinição inválido. Solicite um novo link.";
    }

    if (!newPassword) {
      newErrors.newPassword = "Informe a nova senha";
    } else if (newPassword.length < 8) {
      newErrors.newPassword = "A senha deve ter no mínimo 8 caracteres";
    } else if (newPassword.length > 72) {
      newErrors.newPassword = "A senha deve ter no máximo 72 caracteres";
    }

    if (!confirmPassword) {
      newErrors.confirmPassword = "Confirme a nova senha";
    } else if (newPassword !== confirmPassword) {
      newErrors.confirmPassword = "As senhas não coincidem";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }

  async function handleReset() {
    Keyboard.dismiss();
    if (!validate()) return;
    setIsSubmitting(true);
    setErrors({});
    try {
      await resetPassword({ token: token!, newPassword });
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
          contentContainerStyle={{ flexGrow: 1, paddingBottom: 40 }}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="handled"
          bounces={false}
        >
          {/* ── Hero ──────────────────────────────────────────────────── */}
          <View className="px-6 pt-10 pb-8 items-center">
            {/* Back button */}
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
              {success ? "Senha atualizada" : "Criar nova senha"}
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
                    Nova senha
                  </Text>
                  <Text className="text-[#64748B] text-center text-sm leading-5 px-4">
                    Escolha uma senha segura de pelo menos 8 caracteres para
                    proteger sua conta.
                  </Text>
                </View>

                {/* Token missing warning */}
                {!token && (
                  <View className="mb-5 px-4 py-3 rounded-xl bg-amber-50 border border-amber-200">
                    <Text className="text-amber-700 text-sm text-center">
                      Link de redefinição inválido ou expirado. Solicite um novo
                      através da tela de login.
                    </Text>
                  </View>
                )}

                {/* Nova senha */}
                <View className="relative mb-1">
                  <View className="absolute left-4 z-10" style={{ top: 38 }}>
                    <Lock size={18} color="#94A3B8" />
                  </View>
                  <Input
                    label="Nova senha"
                    placeholder="Mínimo 8 caracteres"
                    secureTextEntry
                    showPasswordToggle
                    value={newPassword}
                    onChangeText={(v) => {
                      setNewPassword(v);
                      if (errors.newPassword)
                        setErrors((e) => ({
                          ...e,
                          newPassword: undefined,
                        }));
                    }}
                    error={errors.newPassword}
                    inputClassName="pl-11"
                  />
                </View>

                {/* Password strength bar */}
                {newPassword.length > 0 && (
                  <View className="mb-4 -mt-2">
                    <View className="flex-row gap-1 mb-1">
                      {[1, 2, 3, 4].map((i) => (
                        <View
                          key={i}
                          className="flex-1 h-1.5 rounded-full"
                          style={{
                            backgroundColor:
                              i <= strength
                                ? strengthColors[strength]
                                : "#E2E8F0",
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
                <View className="relative mb-2">
                  <View className="absolute left-4 z-10" style={{ top: 38 }}>
                    <ShieldCheck size={18} color="#94A3B8" />
                  </View>
                  <Input
                    label="Confirmar nova senha"
                    placeholder="Repita a nova senha"
                    secureTextEntry
                    showPasswordToggle
                    value={confirmPassword}
                    onChangeText={(v) => {
                      setConfirmPassword(v);
                      if (errors.confirmPassword)
                        setErrors((e) => ({
                          ...e,
                          confirmPassword: undefined,
                        }));
                    }}
                    error={errors.confirmPassword}
                    inputClassName="pl-11"
                  />
                </View>

                {/* Password match indicator */}
                {confirmPassword.length > 0 && (
                  <View className="flex-row items-center gap-1.5 mb-4 -mt-2">
                    <View
                      className="w-2 h-2 rounded-full"
                      style={{
                        backgroundColor:
                          newPassword === confirmPassword
                            ? "#22C55E"
                            : "#EF4444",
                      }}
                    />
                    <Text
                      className="text-xs font-medium"
                      style={{
                        color:
                          newPassword === confirmPassword
                            ? "#22C55E"
                            : "#EF4444",
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
                  disabled={isSubmitting || !token}
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
              /* ── Success state ──────────────────────────────────────── */
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

