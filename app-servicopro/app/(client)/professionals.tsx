import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import { ChevronLeft, Filter, MapPin, Search, Star } from "lucide-react-native";
import React, { useState } from "react";
import {
  Image,
  ScrollView,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import { StatusBar } from "expo-status-bar";

const professionals = [
  {
    id: "1",
    name: "João Santos",
    specialty: "Eletricista",
    rating: 4.8,
    reviews: 127,
    distance: "0.8 km",
    price: "R$ 80",
    available: true,
    image:
      "https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBlbGVjdHJpY2lhbiUyMHBvcnRyYWl0fGVufDF8fHx8MTc2NzQ4ODA4OXww&ixlib=rb-4.1.0&q=80&w=1080",
  },
  {
    id: "2",
    name: "Carlos Oliveira",
    specialty: "Eletricista",
    rating: 4.9,
    reviews: 98,
    distance: "1.2 km",
    price: "R$ 85",
    available: true,
    image:
      "https://images.unsplash.com/photo-1758519290233-a03c1d17ecc9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzZXJ2aWNlJTIwcHJvZmVzc2lvbmFsJTIwc21pbGluZ3xlbnwxfHx8fDE3Njc0ODk2NjV8MA&ixlib=rb-4.1.0&q=80&w=1080",
  },
  {
    id: "3",
    name: "Pedro Almeida",
    specialty: "Eletricista",
    rating: 4.7,
    reviews: 156,
    distance: "1.8 km",
    price: "R$ 75",
    available: false,
    image:
      "https://images.unsplash.com/photo-1646227655718-dd721b681d91?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjb25zdHJ1Y3Rpb24lMjB3b3JrZXIlMjBwb3J0cmFpdHxlbnwxfHx8fDE3Njc0ODk2NjR8MA&ixlib=rb-4.1.0&q=80&w=1080",
  },
  {
    id: "4",
    name: "Ricardo Lima",
    specialty: "Eletricista",
    rating: 4.6,
    reviews: 89,
    distance: "2.3 km",
    price: "R$ 70",
    available: true,
    image:
      "https://images.unsplash.com/photo-1635221798248-8a3452ad07cd?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwbHVtYmVyJTIwcHJvZmVzc2lvbmFsJTIwd29ya3xlbnwxfHx8fDE3Njc0NDE1MDl8MA&ixlib=rb-4.1.0&q=80&w=1080",
  },
];

export default function ProfessionalsListScreen() {
  const router = useRouter();
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedFilter, setSelectedFilter] = useState("proximos");

  const filteredProfessionals = professionals.filter((prof) =>
    prof.name.toLowerCase().includes(searchQuery.toLowerCase())
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
          <Text className="text-white text-3xl font-bold mb-2">
            Eletricistas
          </Text>
          <Text className="text-blue-100 text-base mb-6">
            45 profissionais disponíveis
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
              placeholder="Buscar por nome..."
              placeholderTextColor="#94A3B8"
              className="pl-12 pr-4 py-3 rounded-xl bg-white shadow-lg text-gray-900"
            />
          </View>
        </LinearGradient>

        {/* Filters */}
        <View className="px-6 pt-4 pb-2">
          <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            <View className="flex-row gap-3">
              <TouchableOpacity
                onPress={() => setSelectedFilter("proximos")}
                className={`px-4 py-2 rounded-full ${
                  selectedFilter === "proximos"
                    ? "bg-[#3B82F6]"
                    : "bg-white border border-gray-200"
                }`}
              >
                <Text
                  className={
                    selectedFilter === "proximos"
                      ? "text-white"
                      : "text-[#64748B]"
                  }
                >
                  Mais próximos
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => setSelectedFilter("avaliados")}
                className={`px-4 py-2 rounded-full ${
                  selectedFilter === "avaliados"
                    ? "bg-[#3B82F6]"
                    : "bg-white border border-gray-200"
                }`}
              >
                <Text
                  className={
                    selectedFilter === "avaliados"
                      ? "text-white"
                      : "text-[#64748B]"
                  }
                >
                  Melhor avaliados
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => setSelectedFilter("preco")}
                className={`px-4 py-2 rounded-full ${
                  selectedFilter === "preco"
                    ? "bg-[#3B82F6]"
                    : "bg-white border border-gray-200"
                }`}
              >
                <Text
                  className={
                    selectedFilter === "preco" ? "text-white" : "text-[#64748B]"
                  }
                >
                  Menor preço
                </Text>
              </TouchableOpacity>
              <TouchableOpacity className="p-2 rounded-full bg-white border border-gray-200">
                <Filter size={20} color="#64748B" />
              </TouchableOpacity>
            </View>
          </ScrollView>
        </View>

        {/* Location */}
        <View className="px-6 py-3 flex-row items-center gap-2">
          <MapPin size={16} color="#64748B" />
          <Text className="text-[#64748B]">São Paulo, SP - Centro</Text>
        </View>

        {/* Professionals List */}
        <View className="px-6 pb-6">
          {filteredProfessionals.map((professional) => (
            <TouchableOpacity
              key={professional.id}
              onPress={() => router.push("/(client)/professional-profile")}
              className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 mb-4"
            >
              <View className="flex-row gap-4">
                <Image
                  source={{ uri: professional.image }}
                  className="w-20 h-20 rounded-xl"
                  resizeMode="cover"
                />
                <View className="flex-1">
                  <View className="flex-row items-start justify-between mb-1">
                    <Text className="font-semibold flex-1" numberOfLines={1}>
                      {professional.name}
                    </Text>
                    {professional.available && (
                      <View className="w-2 h-2 rounded-full bg-[#22C55E] ml-2" />
                    )}
                  </View>
                  <Text className="text-[#64748B] mb-2">
                    {professional.specialty}
                  </Text>
                  <View className="flex-row items-center gap-4 flex-wrap">
                    <View className="flex-row items-center gap-1">
                      <Star size={16} color="#FACC15" fill="#FACC15" />
                      <Text className="text-[#64748B]">
                        {professional.rating}
                      </Text>
                      <Text className="text-[#64748B]">
                        ({professional.reviews})
                      </Text>
                    </View>
                    <View className="flex-row items-center gap-1">
                      <MapPin size={16} color="#64748B" />
                      <Text className="text-[#64748B]">
                        {professional.distance}
                      </Text>
                    </View>
                  </View>
                </View>
                <View className="items-end">
                  <Text className="text-[#64748B] mb-1 text-sm">
                    A partir de
                  </Text>
                  <Text className="text-[#3B82F6] font-semibold">
                    {professional.price}
                  </Text>
                </View>
              </View>
            </TouchableOpacity>
          ))}
        </View>
      </ScrollView>
    </View>
  );
}
