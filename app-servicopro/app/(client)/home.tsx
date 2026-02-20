import { useAuth } from "@/context/AuthContext";
import { getCategoryMeta } from "@/constants/categoryMeta";
import { listProviders, listServiceCategories } from "@/services/apiClient";
import { ProviderSummary, ServiceCategory } from "@/types";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  Home as HomeIcon,
  MapPin,
  Search,
  User,
  Wrench,
} from "lucide-react-native";
import React, { useCallback, useEffect, useState } from "react";
import {
  RefreshControl,
  ScrollView,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

// ── Helpers ──────────────────────────────────────────────────────────────────

function getInitials(name: string): string {
  const parts = name.trim().split(" ");
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

// ── Sub-components ────────────────────────────────────────────────────────────

function CategorySkeleton() {
  return (
    <View className="flex-row flex-wrap gap-4 mb-6">
      {[1, 2, 3, 4].map((i) => (
        <View
          key={i}
          className="bg-white rounded-2xl p-5 border border-gray-100 w-[48%] h-28 opacity-40"
        />
      ))}
    </View>
  );
}

function ProviderSkeleton() {
  return (
    <View>
      {[1, 2, 3].map((i) => (
        <View
          key={i}
          className="bg-white rounded-2xl p-4 border border-gray-100 mb-3 h-24 opacity-40"
        />
      ))}
    </View>
  );
}

// ── Main Screen ───────────────────────────────────────────────────────────────

export default function ClientHomeScreen() {
  const router = useRouter();
  const { user } = useAuth();
  const firstName = user?.name?.split(" ")[0] ?? "Olá";

  const [categories, setCategories] = useState<ServiceCategory[]>([]);
  const [providers, setProviders] = useState<ProviderSummary[]>([]);
  const [loadingCats, setLoadingCats] = useState(true);
  const [loadingProviders, setLoadingProviders] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const loadData = useCallback(async (silent = false) => {
    if (!silent) {
      setLoadingCats(true);
      setLoadingProviders(true);
    }
    try {
      const cats = await listServiceCategories();
      setCategories(cats);
    } catch {
      // silently fail — categories fallback to empty
    } finally {
      setLoadingCats(false);
    }

    try {
      const page = await listProviders({ size: 10 });
      setProviders(page.content);
    } catch {
      // endpoint may not exist yet — silently fail
      setProviders([]);
    } finally {
      setLoadingProviders(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const onRefresh = useCallback(() => {
    setRefreshing(true);
    loadData(true);
  }, [loadData]);

  return (
    <View className="flex-1 bg-[#F8FAFC]">
      <StatusBar style="light" />

      <ScrollView
        className="flex-1"
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            tintColor="#3B82F6"
          />
        }
      >
        {/* ── Header ────────────────────────────────────────────────────────── */}
        <LinearGradient
          colors={["#1E40AF", "#3B82F6"]}
          className="px-6 pt-16 pb-8 rounded-b-[32px]"
        >
          <View className="flex-row items-center justify-between mb-6">
            <View>
              <Text className="text-blue-100 mb-1">Olá,</Text>
              <Text className="text-white text-2xl font-bold">{firstName}</Text>
            </View>
            <TouchableOpacity
              onPress={() => router.push("/(client)/profile")}
              className="w-12 h-12 rounded-full bg-white/20 items-center justify-center"
            >
              <User size={24} color="#FFFFFF" />
            </TouchableOpacity>
          </View>

          {/* Location */}
          <View className="flex-row items-center gap-2 mb-6">
            <MapPin size={20} color="#FFFFFF" />
            <Text className="text-white">São Paulo, SP - Centro</Text>
          </View>

          {/* Search Bar */}
          <TouchableOpacity
            onPress={() => router.push("/(client)/categories")}
            activeOpacity={0.85}
            className="pl-12 pr-4 py-4 rounded-2xl bg-white shadow-lg flex-row items-center"
          >
            <View className="absolute left-4">
              <Search size={20} color="#64748B" />
            </View>
            <Text className="text-[#64748B]">
              Buscar serviços ou profissionais...
            </Text>
          </TouchableOpacity>
        </LinearGradient>

        {/* ── Categories ───────────────────────────────────────────────────── */}
        <View className="px-6 pt-6">
          <Text className="text-xl font-bold mb-4">Categorias</Text>

          {loadingCats ? (
            <CategorySkeleton />
          ) : categories.length === 0 ? (
            <Text className="text-[#64748B] mb-6">
              Nenhuma categoria disponível.
            </Text>
          ) : (
            <View className="flex-row flex-wrap gap-4 mb-6">
              {categories.map((cat) => {
                const { icon: Icon, colors } = getCategoryMeta(cat.name);
                return (
                  <TouchableOpacity
                    key={cat.id}
                    onPress={() =>
                      router.push({
                        pathname: "/(client)/professionals",
                        params: { categoryId: cat.id, categoryName: cat.name },
                      })
                    }
                    className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 w-[48%]"
                  >
                    <LinearGradient
                      colors={colors}
                      className="w-12 h-12 rounded-xl items-center justify-center mb-3"
                    >
                      <Icon size={24} color="#FFFFFF" />
                    </LinearGradient>
                    <Text className="font-semibold mb-1" numberOfLines={1}>
                      {cat.name}
                    </Text>
                    <Text className="text-[#64748B] text-sm" numberOfLines={1}>
                      {cat.description || "Ver profissionais"}
                    </Text>
                  </TouchableOpacity>
                );
              })}
            </View>
          )}

          {/* ── Nearby Providers ─────────────────────────────────────────── */}
          <View className="mb-6">
            <Text className="text-xl font-bold mb-4">
              Profissionais disponíveis
            </Text>

            {loadingProviders ? (
              <ProviderSkeleton />
            ) : providers.length === 0 ? (
              <View className="bg-white rounded-2xl p-6 border border-gray-100 items-center">
                <Wrench size={32} color="#CBD5E1" />
                <Text className="text-[#64748B] mt-3 text-center">
                  Nenhum profissional disponível no momento.
                </Text>
              </View>
            ) : (
              <View>
                {providers.map((p) => {
                  const initials = getInitials(p.name);
                  const { colors } = getCategoryMeta(
                    p.categoryNames?.[0] ?? ""
                  );
                  const specialty = p.categoryNames?.join(", ") || "Profissional";
                  return (
                    <TouchableOpacity
                      key={p.id}
                      onPress={() =>
                        router.push({
                          pathname: "/(client)/professional-profile",
                          params: { professionalId: p.id },
                        })
                      }
                      className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 mb-3"
                    >
                      <View className="flex-row items-center gap-4">
                        {/* Avatar with initials */}
                        <LinearGradient
                          colors={colors}
                          className="w-16 h-16 rounded-xl items-center justify-center"
                        >
                          <Text className="text-white text-xl font-bold">
                            {initials}
                          </Text>
                        </LinearGradient>

                        <View className="flex-1">
                          <Text className="font-semibold mb-0.5">{p.name}</Text>
                          <Text className="text-[#64748B] mb-1" numberOfLines={1}>
                            {specialty}
                          </Text>
                          <View className="flex-row items-center gap-1">
                            <Text className="text-[#FACC15]">★</Text>
                            {p.averageRating !== null ? (
                              <Text className="text-[#64748B]">
                                {p.averageRating.toFixed(1)}{" "}
                                ({p.totalReviews} avaliações)
                              </Text>
                            ) : (
                              <Text className="text-[#64748B]">
                                Sem avaliações
                              </Text>
                            )}
                          </View>
                        </View>

                        <View className="items-end gap-1">
                          <View
                            className={`w-3 h-3 rounded-full ${
                              p.active ? "bg-[#22C55E]" : "bg-[#CBD5E1]"
                            }`}
                          />
                          <Text className="text-[#64748B] text-xs">
                            {p.serviceCount}{" "}
                            {p.serviceCount === 1 ? "serviço" : "serviços"}
                          </Text>
                        </View>
                      </View>
                    </TouchableOpacity>
                  );
                })}
              </View>
            )}
          </View>
        </View>
      </ScrollView>

      {/* ── Tab Bar ──────────────────────────────────────────────────────────── */}
      <SafeAreaView
        edges={["bottom"]}
        className="bg-white border-t border-gray-200"
      >
        <View className="px-6 py-3">
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
            <TouchableOpacity
              onPress={() => router.push("/(client)/profile")}
              className="items-center gap-1"
            >
              <User size={24} color="#64748B" />
              <Text className="text-xs text-[#64748B]">Perfil</Text>
            </TouchableOpacity>
          </View>
        </View>
      </SafeAreaView>
    </View>
  );
}
