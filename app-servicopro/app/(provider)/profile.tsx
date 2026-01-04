import { Card } from "@/components/ui/Card";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  Award,
  ChevronLeft,
  Clock,
  Edit,
  Mail,
  MapPin,
  Phone,
  Shield,
  Star,
} from "lucide-react-native";
import React from "react";
import { Image, ScrollView, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

const services = [
  { id: "1", name: "Instalação de tomadas", price: "R$ 50", active: true },
  { id: "2", name: "Troca de disjuntores", price: "R$ 80", active: true },
  { id: "3", name: "Instalação de chuveiro", price: "R$ 120", active: true },
  { id: "4", name: "Reparo de fiação", price: "R$ 150", active: true },
  { id: "5", name: "Instalação de ventilador", price: "R$ 70", active: false },
];

export default function ProviderProfileScreen() {
  const router = useRouter();

  return (
    <SafeAreaView className="flex-1 bg-[#F8FAFC]" edges={["top"]}>
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header Image */}
        <View className="relative">
          <LinearGradient colors={["#1E40AF", "#3B82F6"]} className="h-48" />
          <TouchableOpacity
            onPress={() => router.back()}
            className="absolute top-12 left-6 w-10 h-10 rounded-full bg-white/90 items-center justify-center shadow-lg"
          >
            <ChevronLeft size={24} color="#1F2937" />
          </TouchableOpacity>
          <TouchableOpacity className="absolute top-12 right-6 w-10 h-10 rounded-full bg-white/90 items-center justify-center shadow-lg">
            <Edit size={20} color="#1F2937" />
          </TouchableOpacity>

          {/* Profile Image */}
          <View className="absolute -bottom-16 left-6">
            <Image
              source={{
                uri: "https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBlbGVjdHJpY2lhbiUyMHBvcnRyYWl0fGVufDF8fHx8MTc2NzQ4ODA4OXww&ixlib=rb-4.1.0&q=80&w=1080",
              }}
              className="w-32 h-32 rounded-2xl border-4 border-white shadow-lg"
              resizeMode="cover"
            />
          </View>
        </View>

        {/* Profile Info */}
        <View className="px-6 mt-20 pb-6">
          <View className="mb-6">
            <Text className="text-2xl font-bold mb-1">João Santos</Text>
            <Text className="text-[#64748B] mb-3">
              Eletricista Profissional
            </Text>
            <View className="flex-row items-center gap-4 flex-wrap">
              <View className="flex-row items-center gap-1">
                <Star size={20} color="#FACC15" fill="#FACC15" />
                <Text className="font-semibold">4.8</Text>
                <Text className="text-[#64748B]">(127 avaliações)</Text>
              </View>
              <View className="flex-row items-center gap-1">
                <View className="w-3 h-3 rounded-full bg-[#22C55E]" />
                <Text className="text-[#22C55E]">Disponível</Text>
              </View>
            </View>
          </View>

          {/* Stats */}
          <View className="flex-row gap-4 mb-6">
            <Card className="flex-1 items-center">
              <View className="w-10 h-10 rounded-full bg-[#3B82F6]/10 items-center justify-center mb-2">
                <Award size={20} color="#3B82F6" />
              </View>
              <Text className="font-semibold mb-1">312</Text>
              <Text className="text-[#64748B] text-sm">Serviços</Text>
            </Card>
            <Card className="flex-1 items-center">
              <View className="w-10 h-10 rounded-full bg-[#22C55E]/10 items-center justify-center mb-2">
                <Shield size={20} color="#22C55E" />
              </View>
              <Text className="font-semibold mb-1">98%</Text>
              <Text className="text-[#64748B] text-sm">Aprovação</Text>
            </Card>
            <Card className="flex-1 items-center">
              <View className="w-10 h-10 rounded-full bg-[#FB923C]/10 items-center justify-center mb-2">
                <Clock size={20} color="#FB923C" />
              </View>
              <Text className="font-semibold mb-1">5 anos</Text>
              <Text className="text-[#64748B] text-sm">Experiência</Text>
            </Card>
          </View>

          {/* Contact Info */}
          <Card className="mb-6">
            <Text className="text-lg font-semibold mb-4">
              Informações de contato
            </Text>
            <View>
              <View className="flex-row items-center gap-3 mb-3">
                <Phone size={20} color="#64748B" />
                <Text className="text-[#64748B]">(11) 99999-9999</Text>
              </View>
              <View className="flex-row items-center gap-3 mb-3">
                <Mail size={20} color="#64748B" />
                <Text className="text-[#64748B]">joao.santos@email.com</Text>
              </View>
              <View className="flex-row items-center gap-3">
                <MapPin size={20} color="#64748B" />
                <Text className="text-[#64748B]">Centro, São Paulo - SP</Text>
              </View>
            </View>
          </Card>

          {/* About */}
          <Card className="mb-6">
            <Text className="text-lg font-semibold mb-3">Sobre</Text>
            <Text className="text-[#64748B] leading-6">
              Eletricista profissional com 5 anos de experiência. Especializado
              em instalações residenciais e comerciais. Atendimento rápido e
              garantia de qualidade em todos os serviços.
            </Text>
          </Card>

          {/* Services */}
          <Card className="mb-6">
            <View className="flex-row items-center justify-between mb-4">
              <Text className="text-lg font-semibold">Serviços oferecidos</Text>
              <TouchableOpacity>
                <Text className="text-[#3B82F6]">Editar</Text>
              </TouchableOpacity>
            </View>
            <View>
              {services.map((service) => (
                <View
                  key={service.id}
                  className="flex-row items-center justify-between py-3 border-b border-gray-100"
                  style={{
                    borderBottomWidth:
                      service.id === services[services.length - 1].id ? 0 : 1,
                  }}
                >
                  <View className="flex-row items-center gap-3 flex-1">
                    <View
                      className={`w-5 h-5 rounded border-2 ${
                        service.active
                          ? "border-[#3B82F6] bg-[#3B82F6]"
                          : "border-gray-300"
                      }`}
                    />
                    <Text
                      className={
                        service.active ? "text-gray-900" : "text-[#64748B]"
                      }
                    >
                      {service.name}
                    </Text>
                  </View>
                  <Text className="text-[#3B82F6] font-semibold">
                    {service.price}
                  </Text>
                </View>
              ))}
            </View>
            <TouchableOpacity className="w-full mt-4 py-3 rounded-xl border-2 border-[#3B82F6] items-center">
              <Text className="text-[#3B82F6] font-semibold">
                Adicionar Novo Serviço
              </Text>
            </TouchableOpacity>
          </Card>

          {/* Performance */}
          <Card>
            <Text className="text-lg font-semibold mb-4">
              Desempenho este mês
            </Text>
            <View>
              <View className="mb-4">
                <View className="flex-row items-center justify-between mb-2">
                  <Text className="text-[#64748B]">Ganhos</Text>
                  <Text className="text-[#22C55E] font-semibold">
                    R$ 2.450,00
                  </Text>
                </View>
                <View className="w-full bg-gray-100 rounded-full h-2">
                  <View
                    className="bg-[#22C55E] h-2 rounded-full"
                    style={{ width: "65%" }}
                  />
                </View>
              </View>
              <View className="mb-4">
                <View className="flex-row items-center justify-between mb-2">
                  <Text className="text-[#64748B]">Serviços concluídos</Text>
                  <Text>32 de 40</Text>
                </View>
                <View className="w-full bg-gray-100 rounded-full h-2">
                  <View
                    className="bg-[#3B82F6] h-2 rounded-full"
                    style={{ width: "80%" }}
                  />
                </View>
              </View>
              <View>
                <View className="flex-row items-center justify-between mb-2">
                  <Text className="text-[#64748B]">Taxa de aceitação</Text>
                  <Text>90%</Text>
                </View>
                <View className="w-full bg-gray-100 rounded-full h-2">
                  <View
                    className="bg-[#FB923C] h-2 rounded-full"
                    style={{ width: "90%" }}
                  />
                </View>
              </View>
            </View>
          </Card>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
