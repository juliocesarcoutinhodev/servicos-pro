import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  CheckCircle2,
  ChevronLeft,
  CreditCard,
  Shield,
} from "lucide-react-native";
import React, { useState } from "react";
import { ScrollView, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

const paymentMethods = [
  {
    id: "credit",
    name: "Cartão de Crédito",
    desc: "Parcelamento em até 12x",
    icon: CreditCard,
    color: ["#3B82F6", "#2563EB"],
  },
  {
    id: "debit",
    name: "Cartão de Débito",
    desc: "Pagamento à vista",
    icon: CreditCard,
    color: ["#FB923C", "#F97316"],
  },
  {
    id: "pix",
    name: "PIX",
    desc: "Aprovação imediata",
    icon: null,
    color: ["#22C55E", "#16A34A"],
  },
  {
    id: "cash",
    name: "Dinheiro",
    desc: "Pagar na hora do serviço",
    icon: null,
    color: ["#64748B", "#475569"],
  },
];

export default function PaymentScreen() {
  const router = useRouter();
  const [selectedPayment, setSelectedPayment] = useState("credit");

  return (
    <View className="flex-1 bg-[#F8FAFC]">
      <StatusBar style="light" />
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <LinearGradient
          colors={["#1E40AF", "#3B82F6"]}
          className="px-6 pt-16 pb-8"
        >
          <TouchableOpacity
            onPress={() => router.back()}
            className="mb-6 flex-row items-center"
          >
            <ChevronLeft size={24} color="#FFFFFF" />
            <Text className="text-white ml-2">Voltar</Text>
          </TouchableOpacity>
          <Text className="text-white text-3xl font-bold mb-2">Pagamento</Text>
          <Text className="text-blue-100 text-base">
            Escolha a forma de pagamento
          </Text>
        </LinearGradient>

        {/* Security Badge */}
        <View className="px-6 -mt-4">
          <Card variant="elevated">
            <View className="flex-row items-center gap-3">
              <View className="w-12 h-12 rounded-xl bg-[#22C55E]/10 items-center justify-center">
                <Shield size={24} color="#22C55E" />
              </View>
              <View className="flex-1">
                <Text className="font-semibold mb-1">Pagamento Seguro</Text>
                <Text className="text-[#64748B]">
                  Seus dados estão protegidos
                </Text>
              </View>
            </View>
          </Card>
        </View>

        {/* Payment Methods */}
        <View className="px-6 mt-6">
          <Text className="text-lg font-semibold mb-4">Forma de pagamento</Text>
          <View>
            {paymentMethods.map((method) => {
              const Icon = method.icon;
              return (
                <TouchableOpacity
                  key={method.id}
                  onPress={() => setSelectedPayment(method.id)}
                  className={`bg-white rounded-2xl p-5 shadow-sm border mb-3 ${
                    selectedPayment === method.id
                      ? "border-2 border-[#3B82F6]"
                      : "border border-gray-100"
                  }`}
                >
                  <View className="flex-row items-center gap-4">
                    <LinearGradient
                      colors={method.color as [string, string]}
                      className="w-12 h-12 rounded-xl items-center justify-center"
                    >
                      {Icon ? (
                        <Icon size={24} color="#FFFFFF" />
                      ) : (
                        <Text className="text-white font-bold">
                          {method.id === "pix" ? "PIX" : "💰"}
                        </Text>
                      )}
                    </LinearGradient>
                    <View className="flex-1">
                      <Text className="font-semibold mb-1">{method.name}</Text>
                      <Text className="text-[#64748B]">{method.desc}</Text>
                    </View>
                    <View
                      className={`w-6 h-6 rounded-full border-2 ${
                        selectedPayment === method.id
                          ? "border-[#3B82F6]"
                          : "border-gray-300"
                      } items-center justify-center`}
                    >
                      {selectedPayment === method.id && (
                        <View className="w-3 h-3 rounded-full bg-[#3B82F6]" />
                      )}
                    </View>
                  </View>
                </TouchableOpacity>
              );
            })}
          </View>
        </View>

        {/* Card Form */}
        {selectedPayment === "credit" || selectedPayment === "debit" ? (
          <View className="px-6 mt-6">
            <Text className="text-lg font-semibold mb-4">Dados do cartão</Text>
            <Card>
              <Input
                label="Número do cartão"
                placeholder="0000 0000 0000 0000"
                keyboardType="numeric"
                containerClassName="mb-4"
              />
              <Input
                label="Nome no cartão"
                placeholder="Nome como está no cartão"
                autoCapitalize="words"
                containerClassName="mb-4"
              />
              <View className="flex-row gap-4">
                <View className="flex-1">
                  <Input
                    label="Validade"
                    placeholder="MM/AA"
                    keyboardType="numeric"
                    containerClassName="mb-0"
                  />
                </View>
                <View className="flex-1">
                  <Input
                    label="CVV"
                    placeholder="000"
                    keyboardType="numeric"
                    secureTextEntry
                    containerClassName="mb-0"
                  />
                </View>
              </View>
            </Card>
          </View>
        ) : null}

        {/* Order Summary */}
        <View className="px-6 mt-6 mb-6">
          <Text className="text-lg font-semibold mb-4">Resumo do pedido</Text>
          <Card>
            <View className="mb-3">
              <View className="flex-row justify-between mb-2">
                <Text className="text-[#64748B]">Profissional</Text>
                <Text>João Santos</Text>
              </View>
              <View className="flex-row justify-between mb-2">
                <Text className="text-[#64748B]">Serviço</Text>
                <Text>Troca de disjuntores</Text>
              </View>
              <View className="flex-row justify-between mb-2">
                <Text className="text-[#64748B]">Data e hora</Text>
                <Text>05/01/2026 - 14:00</Text>
              </View>
            </View>
            <View className="border-t border-gray-100 pt-3">
              <View className="flex-row justify-between mb-2">
                <Text className="text-[#64748B]">Valor do serviço</Text>
                <Text>R$ 80,00</Text>
              </View>
              <View className="flex-row justify-between mb-3">
                <Text className="text-[#64748B]">Taxa de serviço</Text>
                <Text>R$ 5,00</Text>
              </View>
              <View className="flex-row justify-between">
                <Text className="font-semibold">Total</Text>
                <Text className="text-[#3B82F6] font-bold text-lg">
                  R$ 85,00
                </Text>
              </View>
            </View>
          </Card>
        </View>
      </ScrollView>

      {/* Button */}
      <View className="px-6 pb-6">
        <Button
          onPress={() => router.push("/(client)/home")}
          variant="primary"
          size="lg"
          fullWidth
        >
          <View className="flex-row items-center justify-center gap-2">
            <CheckCircle2 size={20} color="#FFFFFF" />
            <Text className="text-white font-semibold">
              Confirmar Pagamento
            </Text>
          </View>
        </Button>
      </View>
    </View>
  );
}
