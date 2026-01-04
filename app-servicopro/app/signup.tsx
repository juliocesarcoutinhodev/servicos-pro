import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { ChevronLeft } from "lucide-react-native";
import React from "react";
import { ScrollView, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

export default function SignupScreen() {
  const router = useRouter();

  return (
    <SafeAreaView className="flex-1 bg-[#F8FAFC]">
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        {/* Header */}
        <LinearGradient
          colors={["#1E40AF", "#3B82F6"]}
          className="px-6 pt-12 pb-8"
        >
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
        </LinearGradient>

        {/* Form */}
        <View className="px-6 pt-8 pb-6">
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
          />

          <Input
            label="Confirmar senha"
            placeholder="Digite a senha novamente"
            secureTextEntry
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
    </SafeAreaView>
  );
}
