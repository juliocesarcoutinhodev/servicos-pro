import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  Calendar,
  ChevronLeft,
  Clock,
  FileText,
  MapPin,
} from "lucide-react-native";
import React, { useState } from "react";
import { ScrollView, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

const timeSlots = ["08:00", "10:00", "14:00", "16:00", "18:00", "20:00"];
const services = [
  "Instalação de tomadas - R$ 50",
  "Troca de disjuntores - R$ 80",
  "Instalação de chuveiro - R$ 120",
  "Reparo de fiação - R$ 150",
];

export default function ServiceRequestScreen() {
  const router = useRouter();
  const [selectedTime, setSelectedTime] = useState<string | null>(null);
  const [selectedService, setSelectedService] = useState("");

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
            Solicitar Serviço
          </Text>
          <Text className="text-blue-100 text-base">
            Preencha os detalhes da sua solicitação
          </Text>
        </LinearGradient>

        {/* Professional Card */}
        <View className="px-6 -mt-4">
          <Card variant="elevated">
            <View className="flex-row items-center gap-4">
              <View className="w-16 h-16 rounded-xl bg-gray-200" />
              <View className="flex-1">
                <Text className="text-lg font-semibold mb-1">João Santos</Text>
                <Text className="text-[#64748B]">Eletricista</Text>
              </View>
            </View>
          </Card>
        </View>

        {/* Form */}
        <View className="px-6 mt-6 pb-6">
          {/* Service Selection */}
          <Card className="mb-4">
            <View className="flex-row items-center gap-3 mb-3">
              <FileText size={20} color="#3B82F6" />
              <Text className="font-semibold">Serviço</Text>
            </View>
            <View className="border border-gray-200 rounded-xl bg-white">
              <ScrollView>
                {services.map((service, index) => (
                  <TouchableOpacity
                    key={index}
                    onPress={() => setSelectedService(service)}
                    className={`px-4 py-3 border-b border-gray-100 ${
                      selectedService === service ? "bg-[#3B82F6]/10" : ""
                    }`}
                    style={{
                      borderBottomWidth: index === services.length - 1 ? 0 : 1,
                    }}
                  >
                    <Text
                      className={
                        selectedService === service
                          ? "text-[#3B82F6] font-semibold"
                          : "text-gray-900"
                      }
                    >
                      {service}
                    </Text>
                  </TouchableOpacity>
                ))}
              </ScrollView>
            </View>
          </Card>

          {/* Date */}
          <Card className="mb-4">
            <View className="flex-row items-center gap-3 mb-3">
              <Calendar size={20} color="#3B82F6" />
              <Text className="font-semibold">Data</Text>
            </View>
            <Input
              placeholder="Selecione a data"
              editable={false}
              containerClassName="mb-0"
            />
          </Card>

          {/* Time */}
          <Card className="mb-4">
            <View className="flex-row items-center gap-3 mb-3">
              <Clock size={20} color="#3B82F6" />
              <Text className="font-semibold">Horário</Text>
            </View>
            <View className="flex-row flex-wrap gap-3">
              {timeSlots.map((time) => (
                <TouchableOpacity
                  key={time}
                  onPress={() => setSelectedTime(time)}
                  className={`px-4 py-3 rounded-xl border ${
                    selectedTime === time
                      ? "border-[#3B82F6] bg-[#3B82F6]"
                      : "border-gray-200"
                  }`}
                >
                  <Text
                    className={
                      selectedTime === time
                        ? "text-white font-semibold"
                        : "text-gray-900"
                    }
                  >
                    {time}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </Card>

          {/* Address */}
          <Card className="mb-4">
            <View className="flex-row items-center gap-3 mb-3">
              <MapPin size={20} color="#3B82F6" />
              <Text className="font-semibold">Endereço</Text>
            </View>
            <Input
              placeholder="Rua, número e complemento"
              containerClassName="mb-4"
            />
            <View className="flex-row gap-3">
              <View className="flex-1">
                <Input placeholder="Bairro" containerClassName="mb-0" />
              </View>
              <View className="flex-1">
                <Input
                  placeholder="CEP"
                  keyboardType="numeric"
                  containerClassName="mb-0"
                />
              </View>
            </View>
          </Card>

          {/* Description */}
          <Card className="mb-4">
            <Text className="font-semibold mb-3">Descrição do problema</Text>
            <Input
              placeholder="Descreva em detalhes o serviço que você precisa..."
              multiline
              numberOfLines={4}
              textAlignVertical="top"
              containerClassName="mb-0"
            />
          </Card>

          {/* Price Summary */}
          <View className="bg-gradient-to-br from-[#3B82F6]/10 to-[#2563EB]/10 rounded-2xl p-5 border border-[#3B82F6]/20">
            <View className="flex-row items-center justify-between mb-2">
              <Text className="text-[#64748B]">Valor do serviço</Text>
              <Text>R$ 80,00</Text>
            </View>
            <View className="flex-row items-center justify-between mb-2">
              <Text className="text-[#64748B]">Taxa de serviço</Text>
              <Text>R$ 5,00</Text>
            </View>
            <View className="border-t border-[#3B82F6]/20 pt-2 mt-2">
              <View className="flex-row items-center justify-between">
                <Text className="font-semibold">Total</Text>
                <Text className="text-[#3B82F6] font-bold text-lg">
                  R$ 85,00
                </Text>
              </View>
            </View>
          </View>
        </View>
      </ScrollView>

      {/* Bottom Actions */}
      <SafeAreaView edges={["bottom"]} className="bg-white border-t border-gray-200">
        <View className="px-6 py-4">
          <Button
            onPress={() => router.push("/(client)/payment")}
            variant="primary"
            size="lg"
            fullWidth
          >
            Continuar para Pagamento
          </Button>
        </View>
      </SafeAreaView>
    </View>
  );
}
