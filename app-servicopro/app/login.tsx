import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { useAuth } from "@/context/AuthContext";
import { extractApiError } from "@/utils/apiError";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { Lock, Mail, Zap } from "lucide-react-native";
import React, { useEffect, useRef, useState } from "react";
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
 * Login screen — authenticates via POST /api/v1/auth/login.
 * The backend returns the user's role inside the JWT; the route guard
 * in AuthContext redirects to the correct home automatically.
 */
export default function LoginScreen() {
  const router = useRouter();
  const { signIn, isLoading: authLoading } = useAuth();
  const scrollViewRef = useRef<ScrollView>(null);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isKeyboardVisible, setIsKeyboardVisible] = useState(false);
  const [errors, setErrors] = useState<{
    email?: string;
    password?: string;
    general?: string;
  }>({});

  function validate(): boolean {
    const newErrors: typeof errors = {};
    if (!email.trim()) {
      newErrors.email = "Informe seu email";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      newErrors.email = "Email inválido";
    }
    if (!password) {
      newErrors.password = "Informe sua senha";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }

  const handleLogin = async () => {
    if (!validate()) return;
    setIsSubmitting(true);
    setErrors({});
    try {
      await signIn({ email: email.trim(), password });
    } catch (err) {
      setErrors({ general: extractApiError(err) });
    } finally {
      setIsSubmitting(false);
    }
  };

  useEffect(() => {
    const show = Keyboard.addListener("keyboardDidShow", () => {
      setIsKeyboardVisible(true);
      setTimeout(
        () => scrollViewRef.current?.scrollToEnd({ animated: true }),
        200
      );
    });
    const hide = Keyboard.addListener("keyboardDidHide", () => {
      setIsKeyboardVisible(false);
      setTimeout(
        () => scrollViewRef.current?.scrollTo({ y: 0, animated: true }),
        100
      );
    });
    return () => {
      show.remove();
      hide.remove();
    };
  }, []);

  return (
    <SafeAreaView className="flex-1" edges={["top"]}>
      <LinearGradient
        colors={["#1E40AF", "#3B82F6"]}
        className="absolute inset-0"
      />
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        className="flex-1"
        keyboardVerticalOffset={Platform.OS === "ios" ? 0 : 0}
      >
        <ScrollView
          ref={scrollViewRef}
          className="flex-1"
          contentContainerStyle={{
            flexGrow: 1,
            paddingBottom: isKeyboardVisible
              ? Platform.OS === "ios"
                ? 300
                : 350
              : 20,
          }}
          showsVerticalScrollIndicator={false}
          style={{ backgroundColor: "transparent" }}
          keyboardShouldPersistTaps="handled"
          bounces={false}
        >
          {/* ── Hero ──────────────────────────────────────────────────────── */}
          <View className="px-6 pt-12 pb-10 items-center">
            <View className="w-20 h-20 rounded-3xl bg-white/20 items-center justify-center mb-6 shadow-lg">
              <Zap size={40} color="#FFFFFF" fill="#FFFFFF" />
            </View>
            <Text className="text-white text-center text-3xl font-bold mb-2">
              Serviços Pro
            </Text>
            <Text className="text-blue-100 text-center text-base">
              Profissionais qualificados na sua região
            </Text>
          </View>

          {/* ── Form Card ─────────────────────────────────────────────────── */}
          <View className="bg-[#F8FAFC] rounded-t-[32px] px-6 pt-10 pb-6 flex-1">
            <Text className="text-center text-2xl font-bold mb-1">
              Bem-vindo!
            </Text>
            <Text className="text-[#64748B] text-center mb-8">
              Entre com sua conta para continuar
            </Text>

            {/* Email */}
            <View className="relative mb-1">
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

            {/* Senha */}
            <View className="relative mb-2">
              <View className="absolute left-4 z-10" style={{ top: 38 }}>
                <Lock size={18} color="#94A3B8" />
              </View>
              <Input
                label="Senha"
                placeholder="Digite sua senha"
                secureTextEntry
                showPasswordToggle
                value={password}
                onChangeText={(v) => {
                  setPassword(v);
                  if (errors.password)
                    setErrors((e) => ({ ...e, password: undefined }));
                }}
                error={errors.password}
                inputClassName="pl-11"
                onFocus={() => {
                  setTimeout(
                    () => scrollViewRef.current?.scrollToEnd({ animated: true }),
                    200
                  );
                }}
              />
            </View>

            {/* Esqueci a senha — placeholder para futuro */}
            <TouchableOpacity className="self-end mb-6 -mt-1">
              <Text className="text-[#3B82F6] text-sm font-medium">
                Esqueci minha senha
              </Text>
            </TouchableOpacity>

            {/* Erro geral da API */}
            {errors.general && (
              <View className="mb-5 px-4 py-3 rounded-xl bg-red-50 border border-red-200">
                <Text className="text-red-700 text-sm text-center">
                  {errors.general}
                </Text>
              </View>
            )}

            <Button
              onPress={handleLogin}
              variant="primary"
              size="lg"
              fullWidth
              className="mb-4"
              disabled={isSubmitting || authLoading}
              loading={isSubmitting}
            >
              Entrar
            </Button>

            <View className="flex-row items-center gap-4 mb-6">
              <View className="flex-1 h-px bg-gray-200" />
              <Text className="text-[#94A3B8] text-sm">ou</Text>
              <View className="flex-1 h-px bg-gray-200" />
            </View>

            <Button
              onPress={() => router.push("/signup")}
              variant="outline"
              fullWidth
            >
              Criar nova conta
            </Button>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
