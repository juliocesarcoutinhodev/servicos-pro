import { getCategoryMeta } from "@/constants/categoryMeta";
import { listProviders } from "@/services/apiClient";
import { ProviderSummary } from "@/types";
import { LinearGradient } from "expo-linear-gradient";
import { useLocalSearchParams, useRouter } from "expo-router";
import {
  ChevronLeft,
  MapPin,
  Search,
  Star,
  Users,
  Wrench,
} from "lucide-react-native";
import React, { useCallback, useEffect, useState } from "react";
import {
  ActivityIndicator,
  RefreshControl,
  ScrollView,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

// ── Helpers ───────────────────────────────────────────────────────────────────

function getInitials(name: string): string {
  const parts = name.trim().split(" ");
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

type SortKey = "name" | "rating" | "services";

function sortProviders(list: ProviderSummary[], key: SortKey): ProviderSummary[] {
  return [...list].sort((a, b) => {
    if (key === "rating") {
      return (b.averageRating ?? -1) - (a.averageRating ?? -1);
    }
    if (key === "services") {
      return b.serviceCount - a.serviceCount;
    }
    return a.name.localeCompare(b.name, "pt-BR");
  });
}

// ── Main Screen ───────────────────────────────────────────────────────────────

export default function ProfessionalsListScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { categoryId, categoryName } = useLocalSearchParams<{
    categoryId?: string;
    categoryName?: string;
  }>();

  const title = categoryName ?? "Profissionais";

  const [allProviders, setAllProviders] = useState<ProviderSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [sortKey, setSortKey] = useState<SortKey>("name");

  const loadData = useCallback(
    async (silent = false) => {
      if (!silent) setLoading(true);
      setError(null);
      try {
        const page = await listProviders({
          categoryId: categoryId ?? undefined,
          size: 50,
        });
        setAllProviders(page.content);
      } catch {
        setError("Não foi possível carregar os profissionais.");
      } finally {
        setLoading(false);
        setRefreshing(false);
      }
    },
    [categoryId]
  );

  useEffect(() => {
    loadData();
  }, [loadData]);

  const onRefresh = useCallback(() => {
    setRefreshing(true);
    loadData(true);
  }, [loadData]);

  // Filter + sort client-side
  const displayed = sortProviders(
    allProviders.filter((p) =>
      p.name.toLowerCase().includes(searchQuery.toLowerCase())
    ),
    sortKey
  );

  // ── Loading ───────────────────────────────────────────────────────────────

  if (loading) {
    return (
      <View className="flex-1 bg-[#F8FAFC]">
        <StatusBar style="light" />
        <LinearGradient
          colors={["#1E40AF", "#3B82F6"]}
          style={{ paddingTop: insets.top }}
          className="px-6 pb-6"
        >
          <TouchableOpacity
            onPress={() => router.back()}
            className="mt-4 mb-6 flex-row items-center"
          >
            <ChevronLeft size={24} color="#FFFFFF" />
            <Text className="text-white ml-2">Voltar</Text>
          </TouchableOpacity>
          <Text className="text-white text-3xl font-bold mb-1">{title}</Text>
        </LinearGradient>
        <View className="flex-1 items-center justify-center">
          <ActivityIndicator size="large" color="#3B82F6" />
          <Text className="mt-4 text-[#64748B]">Buscando profissionais...</Text>
        </View>
      </View>
    );
  }

  return (
    <View className="flex-1 bg-[#F8FAFC]">
      <StatusBar style="light" />
      <ScrollView
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            tintColor="#3B82F6"
          />
        }
      >
        {/* ── Header ──────────────────────────────────────────────────────── */}
        <LinearGradient
          colors={["#1E40AF", "#3B82F6"]}
          style={{ paddingTop: insets.top }}
          className="px-6 pb-6"
        >
          <TouchableOpacity
            onPress={() => router.back()}
            className="mt-4 mb-6 flex-row items-center"
          >
            <ChevronLeft size={24} color="#FFFFFF" />
            <Text className="text-white ml-2">Voltar</Text>
          </TouchableOpacity>

          <Text className="text-white text-3xl font-bold mb-1">{title}</Text>
          <Text className="text-blue-100 text-base mb-6">
            {allProviders.length}{" "}
            {allProviders.length === 1
              ? "profissional disponível"
              : "profissionais disponíveis"}
          </Text>

          {/* Search */}
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

        {/* ── Sort Filters ─────────────────────────────────────────────────── */}
        <View className="px-6 pt-4 pb-2">
          <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            <View className="flex-row gap-3">
              {(
                [
                  { key: "name", label: "A–Z" },
                  { key: "rating", label: "Melhor avaliados" },
                  { key: "services", label: "Mais serviços" },
                ] as { key: SortKey; label: string }[]
              ).map(({ key, label }) => (
                <TouchableOpacity
                  key={key}
                  onPress={() => setSortKey(key)}
                  className={`px-4 py-2 rounded-full ${
                    sortKey === key
                      ? "bg-[#3B82F6]"
                      : "bg-white border border-gray-200"
                  }`}
                >
                  <Text
                    className={
                      sortKey === key ? "text-white" : "text-[#64748B]"
                    }
                  >
                    {label}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </ScrollView>
        </View>

        {/* Location tag */}
        <View className="px-6 py-3 flex-row items-center gap-2">
          <MapPin size={16} color="#64748B" />
          <Text className="text-[#64748B]">São Paulo, SP - Centro</Text>
        </View>

        {/* ── Error ────────────────────────────────────────────────────────── */}
        {error && (
          <View className="mx-6 mb-4 bg-red-50 border border-red-200 rounded-2xl p-4">
            <Text className="text-red-600 text-center">{error}</Text>
            <TouchableOpacity onPress={() => loadData()} className="mt-3">
              <Text className="text-[#3B82F6] text-center font-semibold">
                Tentar novamente
              </Text>
            </TouchableOpacity>
          </View>
        )}

        {/* ── Empty State ───────────────────────────────────────────────────── */}
        {!error && displayed.length === 0 && (
          <View className="mx-6 mb-6 bg-white rounded-2xl p-8 border border-gray-100 items-center">
            <Users size={40} color="#CBD5E1" />
            <Text className="text-[#1E293B] font-semibold text-lg mt-4 mb-2">
              Nenhum profissional encontrado
            </Text>
            <Text className="text-[#64748B] text-center">
              {searchQuery
                ? `Nenhum resultado para "${searchQuery}".`
                : "Ainda não há profissionais cadastrados nesta categoria."}
            </Text>
          </View>
        )}

        {/* ── Professionals List ───────────────────────────────────────────── */}
        <View className="px-6 pb-6">
          {displayed.map((p) => {
            const initials = getInitials(p.name);
            const specialty = p.categoryNames?.join(", ") || "Profissional";
            const { colors } = getCategoryMeta(p.categoryNames?.[0] ?? "");
            return (
              <TouchableOpacity
                key={p.id}
                onPress={() =>
                  router.push({
                    pathname: "/(client)/professional-profile",
                    params: { professionalId: p.id },
                  })
                }
                className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 mb-4"
              >
                <View className="flex-row gap-4">
                  {/* Avatar */}
                  <LinearGradient
                    colors={colors}
                    className="w-20 h-20 rounded-xl items-center justify-center flex-shrink-0"
                  >
                    <Text className="text-white text-2xl font-bold">
                      {initials}
                    </Text>
                  </LinearGradient>

                  <View className="flex-1">
                    <View className="flex-row items-start justify-between mb-1">
                      <Text
                        className="font-semibold flex-1 text-[#1E293B]"
                        numberOfLines={1}
                      >
                        {p.name}
                      </Text>
                      <View
                        className={`w-2.5 h-2.5 rounded-full ml-2 mt-1 ${
                          p.active ? "bg-[#22C55E]" : "bg-[#CBD5E1]"
                        }`}
                      />
                    </View>

                    <Text className="text-[#64748B] mb-2" numberOfLines={1}>
                      {specialty}
                    </Text>

                    <View className="flex-row items-center gap-4 flex-wrap">
                      <View className="flex-row items-center gap-1">
                        <Star size={14} color="#FACC15" fill="#FACC15" />
                        {p.averageRating !== null ? (
                          <Text className="text-[#64748B] text-sm">
                            {p.averageRating.toFixed(1)} ({p.totalReviews})
                          </Text>
                        ) : (
                          <Text className="text-[#64748B] text-sm">
                            Sem avaliações
                          </Text>
                        )}
                      </View>
                      <View className="flex-row items-center gap-1">
                        <Wrench size={14} color="#64748B" />
                        <Text className="text-[#64748B] text-sm">
                          {p.serviceCount}{" "}
                          {p.serviceCount === 1 ? "serviço" : "serviços"}
                        </Text>
                      </View>
                    </View>
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
