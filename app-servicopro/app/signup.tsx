import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { useAuth } from "@/context/AuthContext";
import { UserRole } from "@/types/auth";
import {
  extractApiError,
  extractFieldErrors,
  formatPhoneMask,
  formatPhoneToE164,
} from "@/utils/apiError";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { Briefcase, ChevronLeft, Lock, Mail, Phone, User } from "lucide-react-native";
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
 * Signup screen — registers a new user via POST /api/v1/auth/signup.
 * On success, auto-signs in and the route guard redirects to the correct home.
 */
export default function SignupScreen() {
  const router = useRouter();
  const { signUp } = useAuth();
  const scrollViewRef = useRef<ScrollView>(null);

  const [role, setRole] = useState<UserRole | null>(null);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [acceptedTerms, setAcceptedTerms] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isKeyboardVisible, setIsKeyboardVisible] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  function clearFieldError(field: string) {
    setErrors((prev) => {
      const next = { ...prev };
      delete next[field];
      return next;
    });
  }

  function validate(): boolean {
    const newErrors: Record<string, string> = {};

    if (!role) newErrors.role = "Selecione o tipo de conta";
    if (!name.trim()) newErrors.name = "Informe seu nome completo";
    if (!email.trim()) {
      newErrors.email = "Informe seu email";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      newErrors.email = "Email inválido";
    }
    const digits = phone.replace(/\D/g, "");
    if (!digits) {
      newErrors.phone = "Informe seu telefone";
    } else if (digits.length < 10 || digits.length > 11) {
      newErrors.phone = "Telefone inválido. Ex: (11) 99999-9999";
    }
    if (!password) {
      newErrors.password = "Informe uma senha";
    } else if (password.length < 8) {
      newErrors.password = "A senha deve ter pelo menos 8 caracteres";
    } else if (password.length > 72) {
      newErrors.password = "A senha deve ter no máximo 72 caracteres";
    }
    if (!confirmPassword) {
      newErrors.confirmPassword = "Confirme sua senha";
    } else if (password !== confirmPassword) {
      newErrors.confirmPassword = "As senhas não coincidem";
    }
    if (!acceptedTerms) {
      newErrors.terms = "Aceite os termos para continuar";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }

  const handleSignup = async () => {
    if (!validate()) return;

    setIsSubmitting(true);
    try {
      await signUp({
        name: name.trim(),
        email: email.trim(),
        phone: formatPhoneToE164(phone),
        password,
        role: role!,
      });
      // Navigation handled by the route guard in AuthContext
    } catch (err) {
      const fieldErrors = extractFieldErrors(err);
      if (fieldErrors) {
        setErrors(fieldErrors);
      } else {
        setErrors({ general: extractApiError(err) });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  useEffect(() => {
    const show = Keyboard.addListener("keyboardDidShow", () => {
      setIsKeyboardVisible(true);
      setTimeout(() => scrollViewRef.current?.scrollToEnd({ animated: true }), 200);
    });
    const hide = Keyboard.addListener("keyboardDidHide", () => {
      setIsKeyboardVisible(false);
      setTimeout(() => scrollViewRef.current?.scrollTo({ y: 0, animated: true }), 100);
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
        keyboardVerticalOffset={Platform.OS === "ios" ? 0 : 20}
      >
        <ScrollView
          ref={scrollViewRef}
          className="flex-1"
          contentContainerStyle={{
            flexGrow: 1,
            paddingBottom: isKeyboardVisible
              ? Platform.OS === "ios" ? 300 : 350
              : 20,
          }}
          showsVerticalScrollIndicator={false}
          style={{ backgroundColor: "transparent" }}
          keyboardShouldPersistTaps="handled"
          bounces={false}
        >
          {/* Header */}
          <View className="px-6 pt-6 pb-8 bg-transparent">
            <TouchableOpacity
              onPress={() => router.back()}
              className="mb-6 flex-row items-center"
            >
              <ChevronLeft size={24} color="#FFFFFF" />
            </TouchableOpacity>
            <Text className="text-white text-3xl font-bold mb-2">
              Criar Conta
            </Text>
            <Text className="text-blue-100 text-base">
              Preencha seus dados para começar
            </Text>
          </View>

          {/* Form */}
          <View className="px-6 pt-8 pb-6 bg-[#F8FAFC]">

            {/* Role selector */}
            <Text className="text-[#64748B] mb-3 text-sm font-medium">
              Tipo de conta
            </Text>
            <View className="flex-row gap-3 mb-6">
              <TouchableOpacity
                onPress={() => { setRole("CLIENT"); clearFieldError("role"); }}
                className={`flex-1 flex-row items-center gap-2 p-4 rounded-2xl border-2 ${
                  role === "CLIENT" ? "border-[#3B82F6] bg-blue-50" : "border-gray-200 bg-white"
                }`}
                activeOpacity={0.7}
              >
                <LinearGradient
                  colors={["#3B82F6", "#2563EB"]}
                  className="w-9 h-9 rounded-full items-center justify-center"
                >
                  <User size={18} color="#FFFFFF" />
                </LinearGradient>
                <Text className={`font-semibold ${role === "CLIENT" ? "text-[#2563EB]" : "text-gray-700"}`}>
                  Cliente
                </Text>
              </TouchableOpacity>

              <TouchableOpacity
                onPress={() => { setRole("PROVIDER"); clearFieldError("role"); }}
                className={`flex-1 flex-row items-center gap-2 p-4 rounded-2xl border-2 ${
                  role === "PROVIDER" ? "border-[#FB923C] bg-orange-50" : "border-gray-200 bg-white"
                }`}
                activeOpacity={0.7}
              >
                <LinearGradient
                  colors={["#FB923C", "#F97316"]}
                  className="w-9 h-9 rounded-full items-center justify-center"
                >
                  <Briefcase size={18} color="#FFFFFF" />
                </LinearGradient>
                <Text className={`font-semibold ${role === "PROVIDER" ? "text-[#F97316]" : "text-gray-700"}`}>
                  Prestador
                </Text>
              </TouchableOpacity>
            </View>
            {errors.role && (
              <Text className="text-red-500 text-sm -mt-4 mb-4">{errors.role}</Text>
            )}

            <View className="relative mb-1">
              <View className="absolute left-4 z-10" style={{ top: 38 }}>
                <User size={18} color="#94A3B8" />
              </View>
              <Input
                label="Nome completo"
                placeholder="Digite seu nome completo"
                autoCapitalize="words"
                autoComplete="name"
                value={name}
                onChangeText={(v) => { setName(v); clearFieldError("name"); }}
                error={errors.name}
                inputClassName="pl-11"
              />
            </View>

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
                onChangeText={(v) => { setEmail(v); clearFieldError("email"); }}
                error={errors.email}
                inputClassName="pl-11"
              />
            </View>

            <View className="relative mb-1">
              <View className="absolute left-4 z-10" style={{ top: 38 }}>
                <Phone size={18} color="#94A3B8" />
              </View>
              <Input
                label="Telefone"
                placeholder="(00) 00000-0000"
                keyboardType="phone-pad"
                autoComplete="tel"
                value={phone}
                onChangeText={(v) => {
                  setPhone(formatPhoneMask(v));
                  clearFieldError("phone");
                }}
                error={errors.phone}
                inputClassName="pl-11"
                maxLength={15}
              />
            </View>

            <View className="relative mb-1">
              <View className="absolute left-4 z-10" style={{ top: 38 }}>
                <Lock size={18} color="#94A3B8" />
              </View>
              <Input
                label="Senha"
                placeholder="Mínimo 8 caracteres"
                secureTextEntry
                showPasswordToggle
                value={password}
                onChangeText={(v) => { setPassword(v); clearFieldError("password"); }}
                error={errors.password}
                inputClassName="pl-11"
                onFocus={() => {
                  setTimeout(() => scrollViewRef.current?.scrollToEnd({ animated: true }), 200);
                }}
              />
            </View>

            <View className="relative mb-1">
              <View className="absolute left-4 z-10" style={{ top: 38 }}>
                <Lock size={18} color="#94A3B8" />
              </View>
              <Input
                label="Confirmar senha"
                placeholder="Digite a senha novamente"
                secureTextEntry
                showPasswordToggle
                value={confirmPassword}
                onChangeText={(v) => { setConfirmPassword(v); clearFieldError("confirmPassword"); }}
                error={errors.confirmPassword}
                inputClassName="pl-11"
                onFocus={() => {
                  setTimeout(() => scrollViewRef.current?.scrollToEnd({ animated: true }), 200);
                }}
              />
            </View>

            {/* Terms */}
            <TouchableOpacity
              onPress={() => { setAcceptedTerms((v) => !v); clearFieldError("terms"); }}
              className="flex-row items-start gap-3 pt-2 mb-2"
              activeOpacity={0.7}
            >
              <View
                className={`w-5 h-5 rounded border-2 mt-0.5 items-center justify-center ${
                  acceptedTerms ? "bg-[#3B82F6] border-[#3B82F6]" : "border-[#3B82F6]"
                }`}
              >
                {acceptedTerms && (
                  <Text className="text-white text-xs font-bold">✓</Text>
                )}
              </View>
              <Text className="text-[#64748B] text-sm flex-1">
                Aceito os{" "}
                <Text className="text-[#3B82F6]">termos de uso</Text> e{" "}
                <Text className="text-[#3B82F6]">política de privacidade</Text>
              </Text>
            </TouchableOpacity>
            {errors.terms && (
              <Text className="text-red-500 text-sm mb-4">{errors.terms}</Text>
            )}

            {/* General API error */}
            {errors.general && (
              <View className="mb-4 px-4 py-3 rounded-xl bg-red-50 border border-red-200">
                <Text className="text-red-700 text-sm text-center">
                  {errors.general}
                </Text>
              </View>
            )}

            <Button
              onPress={handleSignup}
              variant="primary"
              size="lg"
              fullWidth
              className="mb-4 mt-4"
              loading={isSubmitting}
              disabled={isSubmitting}
            >
              Criar Conta
            </Button>

            <Button onPress={() => router.back()} variant="ghost" fullWidth>
              <Text className="text-[#3B82F6] text-center">
                Já tem conta? <Text className="font-semibold">Entrar</Text>
              </Text>
            </Button>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
