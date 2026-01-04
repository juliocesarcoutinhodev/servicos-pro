import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  Calendar,
  CheckCircle2,
  Clock,
  DollarSign,
  MapPin,
  Star,
  TrendingUp,
  User,
} from "lucide-react-native";
import React, { useState } from "react";
import { ScrollView, Switch, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

const recentRequests = [
  {
    id: "1",
    client: "Maria Silva",
    service: "Troca de disjuntores",
    date: "Hoje, 14:00",
    address: "Centro, São Paulo",
    price: "R$ 80",
    status: "pending",
  },
  {
    id: "2",
    client: "Carlos Mendes",
    service: "Instalação de tomadas",
    date: "Amanhã, 10:00",
    address: "Jardins, São Paulo",
    price: "R$ 50",
    status: "pending",
  },
  {
    id: "3",
    client: "Ana Paula",
    service: "Instalação de chuveiro",
    date: "Amanhã, 16:00",
    address: "Vila Mariana, São Paulo",
    price: "R$ 120",
    status: "pending",
  },
];

export default function ProviderHomeScreen() {
  const router = useRouter();
  const [isAvailable, setIsAvailable] = useState(true);

  return (
    <SafeAreaView className="flex-1 bg-[#F8FAFC]" edges={["top"]}>
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header with gradient */}
        <LinearGradient
          colors={["#1E40AF", "#3B82F6"]}
          className="px-6 pt-12 pb-8 rounded-b-[32px]"
        >
          <View className="flex-row items-center justify-between mb-6">
            <View>
              <Text className="text-blue-100 mb-1">Bem-vindo,</Text>
              <Text className="text-white text-2xl font-bold">João Santos</Text>
            </View>
            <TouchableOpacity
              onPress={() => router.push("/(provider)/profile")}
              className="w-12 h-12 rounded-full bg-white/20 items-center justify-center"
            >
              <User size={24} color="#FFFFFF" />
            </TouchableOpacity>
          </View>

          {/* Stats Cards */}
          <View className="flex-row gap-3">
            <View className="flex-1 bg-white/10 backdrop-blur-sm rounded-xl p-4">
              <DollarSign size={24} color="#FFFFFF" />
              <Text className="text-white text-xl font-bold mb-1 mt-2">
                R$ 2.450
              </Text>
              <Text className="text-blue-100 text-xs">Este mês</Text>
            </View>
            <View className="flex-1 bg-white/10 backdrop-blur-sm rounded-xl p-4">
              <Star size={24} color="#FFFFFF" />
              <Text className="text-white text-xl font-bold mb-1 mt-2">
                4.8
              </Text>
              <Text className="text-blue-100 text-xs">Avaliação</Text>
            </View>
            <View className="flex-1 bg-white/10 backdrop-blur-sm rounded-xl p-4">
              <TrendingUp size={24} color="#FFFFFF" />
              <Text className="text-white text-xl font-bold mb-1 mt-2">32</Text>
              <Text className="text-blue-100 text-xs">Serviços</Text>
            </View>
          </View>
        </LinearGradient>

        {/* Toggle Available */}
        <View className="px-6 mt-6">
          <Card>
            <View className="flex-row items-center justify-between">
              <View className="flex-1">
                <Text className="font-semibold mb-1">
                  Status de disponibilidade
                </Text>
                <Text className="text-[#64748B]">
                  Receba solicitações de serviços
                </Text>
              </View>
              <Switch
                value={isAvailable}
                onValueChange={setIsAvailable}
                trackColor={{ false: "#D1D5DB", true: "#22C55E" }}
                thumbColor="#FFFFFF"
              />
            </View>
          </Card>
        </View>

        {/* New Requests */}
        <View className="px-6 mt-6">
          <View className="flex-row items-center justify-between mb-4">
            <Text className="text-xl font-bold">Novas Solicitações</Text>
            <TouchableOpacity
              onPress={() => router.push("/(provider)/requests")}
            >
              <Text className="text-[#3B82F6]">Ver todas</Text>
            </TouchableOpacity>
          </View>

          <View>
            {recentRequests.map((request) => (
              <Card key={request.id} className="mb-3">
                <View className="flex-row items-start justify-between mb-3">
                  <View className="flex-1">
                    <Text className="font-semibold mb-1">{request.client}</Text>
                    <Text className="text-[#64748B]">{request.service}</Text>
                  </View>
                  <View className="items-end">
                    <Text className="text-[#3B82F6] font-semibold">
                      {request.price}
                    </Text>
                  </View>
                </View>

                <View className="mb-4">
                  <View className="flex-row items-center gap-2 mb-2">
                    <Calendar size={16} color="#64748B" />
                    <Text className="text-[#64748B]">{request.date}</Text>
                  </View>
                  <View className="flex-row items-center gap-2">
                    <MapPin size={16} color="#64748B" />
                    <Text className="text-[#64748B]">{request.address}</Text>
                  </View>
                </View>

                {request.status === "pending" && (
                  <View className="flex-row gap-3">
                    <Button
                      onPress={() => {}}
                      variant="primary"
                      className="flex-1"
                    >
                      Aceitar
                    </Button>
                    <Button
                      onPress={() => {}}
                      variant="outline"
                      className="flex-1"
                    >
                      Recusar
                    </Button>
                  </View>
                )}
              </Card>
            ))}
          </View>
        </View>

        {/* Today's Schedule */}
        <View className="px-6 mt-6 mb-6">
          <Text className="text-xl font-bold mb-4">Agenda de Hoje</Text>
          <Card>
            <View className="flex-row items-center gap-4 mb-4">
              <View className="w-12 h-12 rounded-xl bg-[#22C55E]/10 items-center justify-center">
                <CheckCircle2 size={24} color="#22C55E" />
              </View>
              <View className="flex-1">
                <Text className="font-semibold mb-1">Ana Paula</Text>
                <Text className="text-[#64748B]">Instalação de chuveiro</Text>
              </View>
            </View>
            <View className="flex-row items-center gap-4">
              <View className="flex-row items-center gap-2">
                <Clock size={16} color="#64748B" />
                <Text className="text-[#64748B]">14:00</Text>
              </View>
              <View className="flex-row items-center gap-2">
                <MapPin size={16} color="#64748B" />
                <Text className="text-[#64748B]">Centro</Text>
              </View>
            </View>
          </Card>
        </View>
      </ScrollView>

      {/* Bottom Navigation */}
      <View className="bg-white border-t border-gray-200 px-6 py-4">
        <View className="flex-row items-center justify-around">
          <TouchableOpacity className="items-center gap-1">
            <Calendar size={24} color="#3B82F6" />
            <Text className="text-xs text-[#3B82F6]">Início</Text>
          </TouchableOpacity>
          <TouchableOpacity
            onPress={() => router.push("/(provider)/requests")}
            className="items-center gap-1"
          >
            <Clock size={24} color="#64748B" />
            <Text className="text-xs text-[#64748B]">Solicitações</Text>
          </TouchableOpacity>
          <TouchableOpacity className="items-center gap-1">
            <DollarSign size={24} color="#64748B" />
            <Text className="text-xs text-[#64748B]">Ganhos</Text>
          </TouchableOpacity>
          <TouchableOpacity
            onPress={() => router.push("/(provider)/profile")}
            className="items-center gap-1"
          >
            <User size={24} color="#64748B" />
            <Text className="text-xs text-[#64748B]">Perfil</Text>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}
