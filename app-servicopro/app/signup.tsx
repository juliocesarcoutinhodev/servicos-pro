import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { ChevronLeft } from "lucide-react-native";
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

export default function SignupScreen() {
  const router = useRouter();
  const scrollViewRef = useRef<ScrollView>(null);
  const [isKeyboardVisible, setIsKeyboardVisible] = useState(false);

  useEffect(() => {
    const keyboardDidShowListener = Keyboard.addListener(
      "keyboardDidShow",
      () => {
        setIsKeyboardVisible(true);
        setTimeout(() => {
          scrollViewRef.current?.scrollToEnd({ animated: true });
        }, 200);
      }
    );

    const keyboardDidHideListener = Keyboard.addListener(
      "keyboardDidHide",
      () => {
        setIsKeyboardVisible(false);
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
        keyboardVerticalOffset={Platform.OS === "ios" ? 0 : 20}
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
          <View className="px-6 pt-6 pb-8 rounded-b-[32px] bg-transparent">
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
            <Input
              label="Nome completo"
              placeholder="Digite seu nome completo"
              autoCapitalize="words"
            />

            <Input
              label="Email"
              placeholder="seu@email.com"
              keyboardType="email-address"
              autoCapitalize="none"
            />

            <Input
              label="Telefone"
              placeholder="(00) 00000-0000"
              keyboardType="phone-pad"
            />

            <Input
              label="Senha"
              placeholder="Mínimo 6 caracteres"
              secureTextEntry
              showPasswordToggle
              onFocus={() => {
                setTimeout(() => {
                  scrollViewRef.current?.scrollToEnd({ animated: true });
                }, 200);
              }}
            />

            <Input
              label="Confirmar senha"
              placeholder="Digite a senha novamente"
              secureTextEntry
              showPasswordToggle
              onFocus={() => {
                setTimeout(() => {
                  scrollViewRef.current?.scrollToEnd({ animated: true });
                }, 200);
              }}
            />

          <View className="flex-row items-start gap-3 pt-2 mb-6">
            <View className="w-5 h-5 rounded border-2 border-[#3B82F6] mt-0.5" />
            <Text className="text-[#64748B] text-sm flex-1">
              Aceito os <Text className="text-[#3B82F6]">termos de uso</Text> e{" "}
              <Text className="text-[#3B82F6]">política de privacidade</Text>
            </Text>
          </View>

          <Button
            onPress={() => router.push("/(client)/home")}
            variant="primary"
            size="lg"
            fullWidth
            className="mb-4"
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
