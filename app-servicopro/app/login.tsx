import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { useAuth } from "@/context/AuthContext";
import { extractApiError } from "@/utils/apiError";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { Briefcase, User } from "lucide-react-native";
import React, { useEffect, useRef, useState } from "react";
import {
  Alert,
  Keyboard,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

type UserType = "client" | "provider";

/**
 * Login screen — authenticates via POST /api/v1/auth/login.
 * On success, AuthContext persists the access token and the
 * route guard redirects the user to the appropriate home screen.
 */
export default function LoginScreen() {
  const router = useRouter();
  const { signIn, isLoading: authLoading } = useAuth();
  const scrollViewRef = useRef<ScrollView>(null);

  const [selectedUserType, setSelectedUserType] = useState<UserType | null>(null);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isKeyboardVisible, setIsKeyboardVisible] = useState(false);
  const [errors, setErrors] = useState<{
    email?: string;
    password?: string;
    general?: string;
  }>({});

  const handleUserTypeSelect = (type: UserType) => {
    setSelectedUserType(type);
    setErrors({});
  };

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
    if (!selectedUserType) {
      Alert.alert("Atenção", "Selecione o tipo de acesso antes de continuar.");
      return;
    }
    if (!validate()) return;

    setIsSubmitting(true);
    setErrors({});
    try {
      await signIn({ email: email.trim(), password });
      // Navigation handled automatically by the route guard in AuthContext
    } catch (err) {
      setErrors({ general: extractApiError(err) });
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
          {/* Header */}
          <View className="px-6 pt-8 pb-8">
            <Text className="text-white text-center text-3xl font-bold mb-2">
              Serviços Pro
            </Text>
            <Text className="text-blue-100 text-center text-base">
              Profissionais qualificados na sua região
            </Text>
          </View>

          {/* Content */}
          <View className="bg-[#F8FAFC] rounded-t-[32px] px-6 pt-10 pb-6">
            <Text className="text-center text-2xl font-bold mb-2">
              Bem-vindo!
            </Text>
            <Text className="text-[#64748B] text-center mb-8">
              Escolha como deseja continuar
            </Text>

            {/* User Type Cards */}
            <View className="mb-8">
              <TouchableOpacity
                onPress={() => handleUserTypeSelect("client")}
                className={`bg-white rounded-2xl p-6 shadow-sm border-2 mb-4 ${
                  selectedUserType === "client"
                    ? "border-[#3B82F6] bg-[#3B82F6]/5"
                    : "border-gray-100"
                }`}
                activeOpacity={0.7}
              >
                <View className="flex-row items-center gap-4 w-full">
                  <LinearGradient
                    colors={["#3B82F6", "#2563EB"]}
                    className="w-14 h-14 rounded-full items-center justify-center"
                  >
                    <User size={28} color="#FFFFFF" />
                  </LinearGradient>
                  <View className="flex-1">
                    <View className="flex-row items-center gap-2">
                      <Text className="text-lg font-semibold mb-1">
                        Sou Cliente
                      </Text>
                      {selectedUserType === "client" && (
                        <View className="w-2 h-2 rounded-full bg-[#3B82F6]" />
                      )}
                    </View>
                    <Text className="text-[#64748B]">
                      Preciso contratar um serviço
                    </Text>
                  </View>
                </View>
              </TouchableOpacity>

              <TouchableOpacity
                onPress={() => handleUserTypeSelect("provider")}
                className={`bg-white rounded-2xl p-6 shadow-sm border-2 ${
                  selectedUserType === "provider"
                    ? "border-[#FB923C] bg-[#FB923C]/5"
                    : "border-gray-100"
                }`}
                activeOpacity={0.7}
              >
                <View className="flex-row items-center gap-4 w-full">
                  <LinearGradient
                    colors={["#FB923C", "#F97316"]}
                    className="w-14 h-14 rounded-full items-center justify-center"
                  >
                    <Briefcase size={28} color="#FFFFFF" />
                  </LinearGradient>
                  <View className="flex-1">
                    <View className="flex-row items-center gap-2">
                      <Text className="text-lg font-semibold mb-1">
                        Sou Prestador
                      </Text>
                      {selectedUserType === "provider" && (
                        <View className="w-2 h-2 rounded-full bg-[#FB923C]" />
                      )}
                    </View>
                    <Text className="text-[#64748B]">
                      Quero oferecer meus serviços
                    </Text>
                  </View>
                </View>
              </TouchableOpacity>
            </View>

            {/* Input Fields */}
            <View className="mb-6">
              <Input
                label="Email"
                placeholder="Digite seu email"
                keyboardType="email-address"
                autoCapitalize="none"
                autoComplete="email"
                value={email}
                onChangeText={(v) => {
                  setEmail(v);
                  if (errors.email) setErrors((e) => ({ ...e, email: undefined }));
                }}
                error={errors.email}
              />
              <Input
                label="Senha"
                placeholder="Digite sua senha"
                secureTextEntry
                showPasswordToggle
                value={password}
                onChangeText={(v) => {
                  setPassword(v);
                  if (errors.password) setErrors((e) => ({ ...e, password: undefined }));
                }}
                error={errors.password}
                onFocus={() => {
                  setTimeout(() => scrollViewRef.current?.scrollToEnd({ animated: true }), 200);
                }}
              />
            </View>

            {/* General API error */}
            {errors.general && (
              <View className="mb-4 px-4 py-3 rounded-xl bg-red-50 border border-red-200">
                <Text className="text-red-700 text-sm text-center">
                  {errors.general}
                </Text>
              </View>
            )}

            {/* User Type Indicator */}
            {selectedUserType && !errors.general && (
              <View className="mb-4 px-4 py-3 rounded-xl bg-blue-50 border border-blue-200">
                <Text className="text-blue-800 text-sm text-center">
                  {selectedUserType === "client"
                    ? "🔵 Acessando como Cliente"
                    : "🟠 Acessando como Prestador"}
                </Text>
              </View>
            )}

            <Button
              onPress={handleLogin}
              variant="primary"
              size="lg"
              fullWidth
              className="mb-4"
              disabled={!selectedUserType || isSubmitting || authLoading}
              loading={isSubmitting}
            >
              Entrar
            </Button>

            <Button
              onPress={() => router.push("/signup")}
              variant="ghost"
              fullWidth
            >
              <Text className="text-[#3B82F6] text-center">
                Não tem conta?{" "}
                <Text className="font-semibold">Cadastre-se</Text>
              </Text>
            </Button>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

