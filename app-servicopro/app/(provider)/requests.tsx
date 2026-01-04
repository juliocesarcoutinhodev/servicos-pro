import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { Calendar, ChevronLeft, MapPin } from "lucide-react-native";
import React, { useState } from "react";
import { ScrollView, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

const allRequests = [
  {
    id: "1",
    client: "Maria Silva",
    service: "Troca de disjuntores",
    date: "Hoje, 14:00",
    address: "Centro, São Paulo - SP",
    distance: "0.8 km",
    price: "R$ 80",
    status: "pending",
    description:
      "Preciso trocar o disjuntor da cozinha que está desarmando constantemente.",
  },
  {
    id: "2",
    client: "Carlos Mendes",
    service: "Instalação de tomadas",
    date: "Amanhã, 10:00",
    address: "Jardins, São Paulo - SP",
    distance: "2.3 km",
    price: "R$ 50",
    status: "pending",
    description: "Instalar 3 tomadas na sala de estar.",
  },
  {
    id: "3",
    client: "Ana Paula",
    service: "Instalação de chuveiro",
    date: "Amanhã, 16:00",
    address: "Vila Mariana, São Paulo - SP",
    distance: "1.5 km",
    price: "R$ 120",
    status: "pending",
    description: "Instalação de chuveiro elétrico de 220V.",
  },
  {
    id: "4",
    client: "Roberto Costa",
    service: "Reparo de fiação",
    date: "07/01, 09:00",
    address: "Pinheiros, São Paulo - SP",
    distance: "3.2 km",
    price: "R$ 150",
    status: "pending",
    description: "Reparo de fiação aparente na garagem.",
  },
];

export default function ProviderRequestsScreen() {
  const router = useRouter();
  const [selectedTab, setSelectedTab] = useState("pending");

  const filteredRequests = allRequests.filter(
    (req) => req.status === selectedTab
  );

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
          <Text className="text-white text-3xl font-bold mb-2">
            Solicitações
          </Text>
          <Text className="text-blue-100 text-base">
            Gerencie suas solicitações de serviços
          </Text>
        </LinearGradient>

        {/* Filter Tabs */}
        <View className="px-6 pt-4 pb-2">
          <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            <View className="flex-row gap-3">
              <TouchableOpacity
                onPress={() => setSelectedTab("pending")}
                className={`px-4 py-2 rounded-full ${
                  selectedTab === "pending"
                    ? "bg-[#3B82F6]"
                    : "bg-white border border-gray-200"
                }`}
              >
                <Text
                  className={
                    selectedTab === "pending" ? "text-white" : "text-[#64748B]"
                  }
                >
                  Pendentes (4)
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => setSelectedTab("accepted")}
                className={`px-4 py-2 rounded-full ${
                  selectedTab === "accepted"
                    ? "bg-[#3B82F6]"
                    : "bg-white border border-gray-200"
                }`}
              >
                <Text
                  className={
                    selectedTab === "accepted" ? "text-white" : "text-[#64748B]"
                  }
                >
                  Aceitas (8)
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => setSelectedTab("completed")}
                className={`px-4 py-2 rounded-full ${
                  selectedTab === "completed"
                    ? "bg-[#3B82F6]"
                    : "bg-white border border-gray-200"
                }`}
              >
                <Text
                  className={
                    selectedTab === "completed"
                      ? "text-white"
                      : "text-[#64748B]"
                  }
                >
                  Concluídas (32)
                </Text>
              </TouchableOpacity>
            </View>
          </ScrollView>
        </View>

        {/* Requests List */}
        <View className="px-6 mt-4 pb-6">
          {filteredRequests.map((request) => (
            <Card key={request.id} className="mb-4">
              <View className="flex-row items-start justify-between mb-3">
                <View className="flex-1">
                  <Text className="font-semibold mb-1">{request.client}</Text>
                  <Text className="text-[#64748B]">{request.service}</Text>
                </View>
                <View className="items-end ml-4">
                  <Text className="text-[#3B82F6] font-semibold">
                    {request.price}
                  </Text>
                </View>
              </View>

              <View className="bg-[#F8FAFC] rounded-xl p-3 mb-3">
                <Text className="text-[#64748B]">{request.description}</Text>
              </View>

              <View className="mb-4">
                <View className="flex-row items-center gap-2 mb-2">
                  <Calendar size={16} color="#64748B" />
                  <Text className="text-[#64748B]">{request.date}</Text>
                </View>
                <View className="flex-row items-center gap-2">
                  <MapPin size={16} color="#64748B" />
                  <Text className="text-[#64748B]">
                    {request.address} • {request.distance}
                  </Text>
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
      </ScrollView>
    </View>
  );
}
