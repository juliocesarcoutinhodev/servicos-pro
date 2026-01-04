import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useRouter } from "expo-router";
import {
  Award,
  ChevronLeft,
  Clock,
  MapPin,
  MessageCircle,
  Phone,
  Shield,
  Star,
} from "lucide-react-native";
import React from "react";
import { Image, ScrollView, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

const services = [
  { name: "Instalação de tomadas", price: "R$ 50" },
  { name: "Troca de disjuntores", price: "R$ 80" },
  { name: "Instalação de chuveiro", price: "R$ 120" },
  { name: "Reparo de fiação", price: "R$ 150" },
];

const reviews = [
  {
    id: "1",
    name: "Ana Paula",
    rating: 5,
    comment: "Excelente profissional! Muito pontual e cuidadoso.",
    date: "2 dias atrás",
  },
  {
    id: "2",
    name: "Roberto Costa",
    rating: 5,
    comment: "Resolveu o problema rapidamente. Recomendo!",
    date: "1 semana atrás",
  },
  {
    id: "3",
    name: "Mariana Silva",
    rating: 4,
    comment: "Bom trabalho, preço justo.",
    date: "2 semanas atrás",
  },
];

export default function ProfessionalProfileScreen() {
  const router = useRouter();

  return (
    <SafeAreaView className="flex-1 bg-[#F8FAFC]" edges={["top"]}>
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header Image */}
        <View className="relative">
          <Image
            source={{
              uri: "https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBlbGVjdHJpY2lhbiUyMHBvcnRyYWl0fGVufDF8fHx8MTc2NzQ4ODA4OXww&ixlib=rb-4.1.0&q=80&w=1080",
            }}
            className="w-full h-64"
            resizeMode="cover"
          />
          <TouchableOpacity
            onPress={() => router.back()}
            className="absolute top-12 left-6 w-10 h-10 rounded-full bg-white/90 items-center justify-center shadow-lg"
          >
            <ChevronLeft size={24} color="#1F2937" />
          </TouchableOpacity>
        </View>

        {/* Profile Info */}
        <View className="px-6 -mt-8">
          <Card variant="elevated">
            <View className="flex-row items-start justify-between mb-4">
              <View className="flex-1">
                <Text className="text-2xl font-bold mb-1">João Santos</Text>
                <Text className="text-[#64748B] mb-2">Eletricista</Text>
                <View className="flex-row items-center gap-4 flex-wrap">
                  <View className="flex-row items-center gap-1">
                    <Star size={20} color="#FACC15" fill="#FACC15" />
                    <Text className="font-semibold">4.8</Text>
                    <Text className="text-[#64748B]">(127 avaliações)</Text>
                  </View>
                </View>
              </View>
              <View className="flex-row items-center gap-1">
                <View className="w-3 h-3 rounded-full bg-[#22C55E]" />
                <Text className="text-[#22C55E]">Online</Text>
              </View>
            </View>

            {/* Stats */}
            <View className="flex-row gap-4 pt-4 border-t border-gray-100">
              <View className="flex-1 items-center">
                <View className="w-10 h-10 rounded-full bg-[#3B82F6]/10 items-center justify-center mb-2">
                  <Award size={20} color="#3B82F6" />
                </View>
                <Text className="text-[#64748B]">312</Text>
                <Text className="text-[#64748B] text-sm">Serviços</Text>
              </View>
              <View className="flex-1 items-center">
                <View className="w-10 h-10 rounded-full bg-[#22C55E]/10 items-center justify-center mb-2">
                  <Shield size={20} color="#22C55E" />
                </View>
                <Text className="text-[#64748B]">98%</Text>
                <Text className="text-[#64748B] text-sm">Aprovação</Text>
              </View>
              <View className="flex-1 items-center">
                <View className="w-10 h-10 rounded-full bg-[#FB923C]/10 items-center justify-center mb-2">
                  <Clock size={20} color="#FB923C" />
                </View>
                <Text className="text-[#64748B]">5 anos</Text>
                <Text className="text-[#64748B] text-sm">Experiência</Text>
              </View>
            </View>
          </Card>
        </View>

        {/* About */}
        <View className="px-6 mt-4">
          <Card>
            <Text className="text-lg font-semibold mb-3">Sobre</Text>
            <Text className="text-[#64748B] leading-6">
              Eletricista profissional com 5 anos de experiência. Especializado
              em instalações residenciais e comerciais. Atendimento rápido e
              garantia de qualidade.
            </Text>
          </Card>
        </View>

        {/* Location */}
        <View className="px-6 mt-4">
          <Card>
            <View className="flex-row items-center gap-3 mb-2">
              <MapPin size={20} color="#3B82F6" />
              <Text className="text-lg font-semibold">Localização</Text>
            </View>
            <Text className="text-[#64748B]">Centro, São Paulo - SP</Text>
            <Text className="text-[#64748B]">0.8 km de distância</Text>
          </Card>
        </View>

        {/* Services */}
        <View className="px-6 mt-4">
          <Card>
            <Text className="text-lg font-semibold mb-4">
              Serviços oferecidos
            </Text>
            {services.map((service, index) => (
              <View
                key={index}
                className="flex-row items-center justify-between py-3 border-b border-gray-100"
                style={{
                  borderBottomWidth: index === services.length - 1 ? 0 : 1,
                }}
              >
                <Text className="text-[#64748B]">{service.name}</Text>
                <Text className="text-[#3B82F6] font-semibold">
                  {service.price}
                </Text>
              </View>
            ))}
          </Card>
        </View>

        {/* Reviews */}
        <View className="px-6 mt-4 mb-24">
          <Card>
            <Text className="text-lg font-semibold mb-4">Avaliações (127)</Text>
            {reviews.map((review) => (
              <View
                key={review.id}
                className="pb-4 mb-4 border-b border-gray-100"
                style={{
                  borderBottomWidth:
                    review.id === reviews[reviews.length - 1].id ? 0 : 1,
                }}
              >
                <View className="flex-row items-center justify-between mb-2">
                  <Text className="font-semibold">{review.name}</Text>
                  <View className="flex-row items-center gap-1">
                    {[...Array(review.rating)].map((_, i) => (
                      <Star key={i} size={16} color="#FACC15" fill="#FACC15" />
                    ))}
                  </View>
                </View>
                <Text className="text-[#64748B] mb-1">{review.comment}</Text>
                <Text className="text-[#64748B] text-sm">{review.date}</Text>
              </View>
            ))}
          </Card>
        </View>
      </ScrollView>

      {/* Bottom Actions */}
      <View className="absolute bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-6 py-4">
        <View className="flex-row gap-3">
          <TouchableOpacity className="w-12 h-12 rounded-xl border-2 border-[#3B82F6] items-center justify-center">
            <Phone size={20} color="#3B82F6" />
          </TouchableOpacity>
          <TouchableOpacity className="w-12 h-12 rounded-xl border-2 border-[#3B82F6] items-center justify-center">
            <MessageCircle size={20} color="#3B82F6" />
          </TouchableOpacity>
          <Button
            onPress={() => router.push("/(client)/service-request")}
            variant="primary"
            className="flex-1"
          >
            Solicitar Serviço
          </Button>
        </View>
      </View>
    </SafeAreaView>
  );
}
