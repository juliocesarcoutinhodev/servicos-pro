import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  Car,
  ChevronLeft,
  Droplet,
  Hammer,
  Home as HomeIcon,
  Laptop,
  PaintBucket,
  Palette,
  Scissors,
  Search,
  Wrench,
  Zap,
} from "lucide-react-native";
import React, { useState } from "react";
import {
  ScrollView,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import { StatusBar } from "expo-status-bar";

const allCategories = [
  {
    icon: Zap,
    name: "Eletricista",
    color: ["#FACC15", "#FB923C"],
    count: 45,
    desc: "Instalações e reparos elétricos",
  },
  {
    icon: Wrench,
    name: "Encanador",
    color: ["#60A5FA", "#06B6D4"],
    count: 38,
    desc: "Hidráulica e vazamentos",
  },
  {
    icon: HomeIcon,
    name: "Pedreiro",
    color: ["#6B7280", "#4B5563"],
    count: 52,
    desc: "Construção e reformas",
  },
  {
    icon: Scissors,
    name: "Cabeleireiro",
    color: ["#F472B6", "#FB7185"],
    count: 67,
    desc: "Cortes e tratamentos capilares",
  },
  {
    icon: Palette,
    name: "Manicure",
    color: ["#A78BFA", "#F472B6"],
    count: 43,
    desc: "Unhas e cuidados",
  },
  {
    icon: Car,
    name: "Mecânico",
    color: ["#EF4444", "#F97316"],
    count: 29,
    desc: "Manutenção automotiva",
  },
  {
    icon: Droplet,
    name: "Pintor",
    color: ["#3B82F6", "#6366F1"],
    count: 34,
    desc: "Pintura residencial e comercial",
  },
  {
    icon: Hammer,
    name: "Marceneiro",
    color: ["#D97706", "#F97316"],
    count: 21,
    desc: "Móveis sob medida",
  },
  {
    icon: PaintBucket,
    name: "Jardineiro",
    color: ["#22C55E", "#10B981"],
    count: 18,
    desc: "Paisagismo e manutenção",
  },
  {
    icon: Laptop,
    name: "Técnico em TI",
    color: ["#6366F1", "#A78BFA"],
    count: 56,
    desc: "Suporte e manutenção",
  },
];

export default function CategoriesScreen() {
  const router = useRouter();
  const [searchQuery, setSearchQuery] = useState("");

  const filteredCategories = allCategories.filter((category) =>
    category.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <View className="flex-1 bg-[#F8FAFC]">
      <StatusBar style="light" />
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <LinearGradient
          colors={["#1E40AF", "#3B82F6"]}
          className="px-6 pt-16 pb-6"
        >
          <TouchableOpacity
            onPress={() => router.back()}
            className="mb-6 flex-row items-center"
          >
            <ChevronLeft size={24} color="#FFFFFF" />
            <Text className="text-white ml-2">Voltar</Text>
          </TouchableOpacity>
          <Text className="text-white text-3xl font-bold mb-2">Categorias</Text>
          <Text className="text-blue-100 text-base mb-6">
            Escolha o serviço que você precisa
          </Text>

          {/* Search Bar */}
          <View className="relative">
            <View
              className="absolute left-4 top-1/2 z-10"
              style={{ transform: [{ translateY: -10 }] }}
            >
              <Search size={20} color="#64748B" />
            </View>
            <TextInput
              value={searchQuery}
              onChangeText={setSearchQuery}
              placeholder="Buscar categoria..."
              placeholderTextColor="#94A3B8"
              className="pl-12 pr-4 py-3 rounded-xl bg-white shadow-lg text-gray-900"
            />
          </View>
        </LinearGradient>

        {/* Categories Grid */}
        <View className="px-6 pt-6 pb-6">
          {filteredCategories.map((category, index) => {
            const Icon = category.icon;
            return (
              <TouchableOpacity
                key={index}
                onPress={() => router.push("/(client)/professionals")}
                className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 mb-4"
              >
                <View className="flex-row items-center gap-4">
                  <LinearGradient
                    colors={category.color as [string, string]}
                    className="w-14 h-14 rounded-xl items-center justify-center"
                  >
                    <Icon size={28} color="#FFFFFF" />
                  </LinearGradient>
                  <View className="flex-1">
                    <Text className="text-lg font-semibold mb-1">
                      {category.name}
                    </Text>
                    <Text className="text-[#64748B] mb-1">{category.desc}</Text>
                    <Text className="text-[#3B82F6]">
                      {category.count} profissionais disponíveis
                    </Text>
                  </View>
                </View>
              </TouchableOpacity>
            );
          })}
        </View>
      </ScrollView>
    </View>
  );
}
