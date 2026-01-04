import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { Briefcase, User } from "lucide-react-native";
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

type UserType = "client" | "provider" | null;

export default function LoginScreen() {
  const router = useRouter();
  const scrollViewRef = useRef<ScrollView>(null);
  const [selectedUserType, setSelectedUserType] = useState<UserType>(null);
  const [email, setEmail] = useState("");
  const [isKeyboardVisible, setIsKeyboardVisible] = useState(false);

  const handleUserTypeSelect = (type: "client" | "provider") => {
    setSelectedUserType(type);
    // Preenche email de exemplo baseado no tipo
    if (type === "client") {
      setEmail("cliente@servicopro.com");
    } else {
      setEmail("prestador@servicopro.com");
    }
  };

  const handleLogin = () => {
    if (selectedUserType === "client") {
      router.push("/(client)/home");
    } else if (selectedUserType === "provider") {
      router.push("/(provider)/home");
    }
  };

  useEffect(() => {
    const keyboardDidShowListener = Keyboard.addListener(
      "keyboardDidShow",
      () => {
        setIsKeyboardVisible(true);
        // Scroll para o final quando o teclado aparecer
        setTimeout(() => {
          scrollViewRef.current?.scrollToEnd({ animated: true });
        }, 200);
      }
    );

    const keyboardDidHideListener = Keyboard.addListener(
      "keyboardDidHide",
      () => {
        setIsKeyboardVisible(false);
        // Scroll de volta para o topo quando o teclado desaparecer
        setTimeout(() => {
          scrollViewRef.current?.scrollTo({ y: 0, animated: true });
        }, 100);
      }
    );

    return () => {
      keyboardDidShowListener.remove();
      keyboardDidHideListener.remove();
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
                label="Email ou telefone"
                placeholder="Digite seu email ou telefone"
                keyboardType="email-address"
                autoCapitalize="none"
                value={email}
                onChangeText={setEmail}
              />
              <Input
                label="Senha"
                placeholder="Digite sua senha"
                secureTextEntry
                showPasswordToggle
                onFocus={() => {
                  // Scroll para o campo de senha quando receber foco
                  setTimeout(() => {
                    scrollViewRef.current?.scrollToEnd({ animated: true });
                  }, 200);
                }}
              />
            </View>

            {/* User Type Indicator */}
            {selectedUserType && (
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
              disabled={!selectedUserType}
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
