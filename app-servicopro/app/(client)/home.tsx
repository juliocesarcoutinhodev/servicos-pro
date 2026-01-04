import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  Home as HomeIcon,
  MapPin,
  Palette,
  Scissors,
  Search,
  User,
  Wrench,
  Zap,
} from "lucide-react-native";
import React from "react";
import { Image, ScrollView, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

const categories = [
  { icon: Zap, name: "Eletricista", color: ["#FACC15", "#FB923C"], count: 45 },
  { icon: Wrench, name: "Encanador", color: ["#60A5FA", "#06B6D4"], count: 38 },
  {
    icon: HomeIcon,
    name: "Pedreiro",
    color: ["#6B7280", "#4B5563"],
    count: 52,
  },
  {
    icon: Scissors,
    name: "Cabeleireiro",
    color: ["#F472B6", "#FB7185"],
    count: 67,
  },
  { icon: Palette, name: "Manicure", color: ["#A78BFA", "#F472B6"], count: 43 },
  { icon: Wrench, name: "Mecânico", color: ["#EF4444", "#F97316"], count: 29 },
];

export default function ClientHomeScreen() {
  const router = useRouter();

  return (
    <SafeAreaView className="flex-1 bg-[#F8FAFC]" edges={["top"]}>
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        {/* Header with gradient */}
        <LinearGradient
          colors={["#1E40AF", "#3B82F6"]}
          className="px-6 pt-6 pb-8 rounded-b-[32px]"
        >
          <View className="flex-row items-center justify-between mb-6">
            <View>
              <Text className="text-blue-100 mb-1">Olá,</Text>
              <Text className="text-white text-2xl font-bold">Maria Silva</Text>
            </View>
            <TouchableOpacity className="w-12 h-12 rounded-full bg-white/20 items-center justify-center">
              <User size={24} color="#FFFFFF" />
            </TouchableOpacity>
          </View>

          {/* Location */}
          <View className="flex-row items-center gap-2 mb-6">
            <MapPin size={20} color="#FFFFFF" />
            <Text className="text-white">São Paulo, SP - Centro</Text>
          </View>

          {/* Search Bar */}
          <View className="relative">
            <View
              className="absolute left-4 top-1/2 z-10"
              style={{ transform: [{ translateY: -10 }] }}
            >
              <Search size={20} color="#64748B" />
            </View>
            <View className="pl-12 pr-4 py-4 rounded-2xl bg-white shadow-lg">
              <Text className="text-[#64748B]">
                Buscar serviços ou profissionais...
              </Text>
            </View>
          </View>
        </LinearGradient>

        {/* Categories */}
        <View className="px-6 pt-6">
          <Text className="text-xl font-bold mb-4">Categorias</Text>
          <View className="flex-row flex-wrap gap-4 mb-6">
            {categories.map((category, index) => {
              const Icon = category.icon;
              return (
                <TouchableOpacity
                  key={index}
                  onPress={() => router.push("/(client)/professionals")}
                  className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 w-[48%]"
                >
                  <LinearGradient
                    colors={category.color as [string, string]}
                    className="w-12 h-12 rounded-xl items-center justify-center mb-3"
                  >
                    <Icon size={24} color="#FFFFFF" />
                  </LinearGradient>
                  <Text className="font-semibold mb-1">{category.name}</Text>
                  <Text className="text-[#64748B] text-sm">
                    {category.count} profissionais
                  </Text>
                </TouchableOpacity>
              );
            })}
          </View>

          {/* Recent Services */}
          <View className="mb-6">
            <Text className="text-xl font-bold mb-4">
              Profissionais próximos
            </Text>
            <View>
              {[1, 2, 3].map((item) => (
                <TouchableOpacity
                  key={item}
                  onPress={() => router.push("/(client)/professional-profile")}
                  className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 mb-3"
                >
                  <View className="flex-row items-center gap-4">
                    <Image
                      source={{
                        uri: "https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBlbGVjdHJpY2lhbiUyMHBvcnRyYWl0fGVufDF8fHx8MTc2NzQ4ODA4OXww&ixlib=rb-4.1.0&q=80&w=1080",
                      }}
                      className="w-16 h-16 rounded-xl"
                      resizeMode="cover"
                    />
                    <View className="flex-1">
                      <Text className="font-semibold mb-1">João Santos</Text>
                      <Text className="text-[#64748B] mb-1">Eletricista</Text>
                      <View className="flex-row items-center gap-1">
                        <Text className="text-[#FACC15]">★</Text>
                        <Text className="text-[#64748B]">
                          4.8 (127 avaliações)
                        </Text>
                      </View>
                    </View>
                    <View className="items-end">
                      <View className="w-3 h-3 rounded-full bg-[#22C55E] mb-1" />
                      <Text className="text-[#64748B] text-sm">0.8 km</Text>
                    </View>
                  </View>
                </TouchableOpacity>
              ))}
            </View>
          </View>
        </View>

        {/* Bottom Navigation */}
        <SafeAreaView edges={["bottom"]} className="bg-white border-t border-gray-200">
          <View className="px-6 py-4">
            <View className="flex-row items-center justify-around">
              <TouchableOpacity className="items-center gap-1">
                <HomeIcon size={24} color="#3B82F6" />
                <Text className="text-xs text-[#3B82F6]">Início</Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => router.push("/(client)/categories")}
                className="items-center gap-1"
              >
                <Search size={24} color="#64748B" />
                <Text className="text-xs text-[#64748B]">Buscar</Text>
              </TouchableOpacity>
              <TouchableOpacity className="items-center gap-1">
                <Wrench size={24} color="#64748B" />
                <Text className="text-xs text-[#64748B]">Serviços</Text>
              </TouchableOpacity>
              <TouchableOpacity className="items-center gap-1">
                <User size={24} color="#64748B" />
                <Text className="text-xs text-[#64748B]">Perfil</Text>
              </TouchableOpacity>
            </View>
          </View>
        </SafeAreaView>
      </ScrollView>
    </SafeAreaView>
  );
}
