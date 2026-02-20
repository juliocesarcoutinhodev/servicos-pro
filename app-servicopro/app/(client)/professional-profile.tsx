import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { getCategoryMeta } from "@/constants/categoryMeta";
import { getProviderProfile, listProviderReviews } from "@/services/apiClient";
import { ProviderProfile, ProviderReview } from "@/types";
import { extractApiError } from "@/utils/apiError";
import { LinearGradient } from "expo-linear-gradient";
import { useLocalSearchParams, useRouter } from "expo-router";
import {
  Award,
  ChevronLeft,
  Clock,
  MessageCircle,
  Phone,
  Shield,
  Star,
  UserCircle2,
  Wrench,
} from "lucide-react-native";
import React, { useCallback, useEffect, useState } from "react";
import {
  ActivityIndicator,
  RefreshControl,
  ScrollView,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView, useSafeAreaInsets } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

// ── Helpers ───────────────────────────────────────────────────────────────────

function getInitials(name: string): string {
  const parts = name.trim().split(" ");
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

function formatPrice(cents: number): string {
  return (cents / 100).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

function formatReviewDate(iso: string): string {
  return new Date(iso).toLocaleDateString("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

function Stars({ rating, size = 16 }: { rating: number; size?: number }) {
  return (
    <View className="flex-row gap-0.5">
      {[1, 2, 3, 4, 5].map((i) => (
        <Star
          key={i}
          size={size}
          color="#FACC15"
          fill={i <= Math.round(rating) ? "#FACC15" : "none"}
        />
      ))}
    </View>
  );
}

// ── Main Screen ───────────────────────────────────────────────────────────────

export default function ProfessionalProfileScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { professionalId } = useLocalSearchParams<{ professionalId: string }>();

  const [profile, setProfile] = useState<ProviderProfile | null>(null);
  const [reviews, setReviews] = useState<ProviderReview[]>([]);
  const [totalReviews, setTotalReviews] = useState(0);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadData = useCallback(
    async (silent = false) => {
      if (!professionalId) return;
      if (!silent) setLoading(true);
      setError(null);
      try {
        const [prof, reviewPage] = await Promise.all([
          getProviderProfile(professionalId),
          listProviderReviews(professionalId, { size: 5 }),
        ]);
        setProfile(prof);
        setReviews(reviewPage.content);
        setTotalReviews(reviewPage.totalElements);
      } catch (err) {
        setError(extractApiError(err));
      } finally {
        setLoading(false);
        setRefreshing(false);
      }
    },
    [professionalId]
  );

  useEffect(() => {
    loadData();
  }, [loadData]);

  const onRefresh = useCallback(() => {
    setRefreshing(true);
    loadData(true);
  }, [loadData]);

  // ── Loading ───────────────────────────────────────────────────────────────

  if (loading) {
    return (
      <View className="flex-1 bg-[#F8FAFC] items-center justify-center">
        <StatusBar style="light" />
        <ActivityIndicator size="large" color="#3B82F6" />
        <Text className="mt-4 text-[#64748B]">Carregando perfil...</Text>
      </View>
    );
  }

  // ── Error ─────────────────────────────────────────────────────────────────

  if (error || !profile) {
    return (
      <View className="flex-1 bg-[#F8FAFC] items-center justify-center px-8">
        <StatusBar style="dark" />
        <UserCircle2 size={64} color="#CBD5E1" />
        <Text className="text-lg font-semibold text-[#1E293B] mt-4 mb-2">
          Ops! Algo deu errado
        </Text>
        <Text className="text-[#64748B] text-center mb-6">{error}</Text>
        <TouchableOpacity
          onPress={() => loadData()}
          className="bg-[#3B82F6] px-8 py-3 rounded-2xl mb-4"
        >
          <Text className="text-white font-semibold">Tentar novamente</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => router.back()}>
          <Text className="text-[#64748B]">Voltar</Text>
        </TouchableOpacity>
      </View>
    );
  }

  const initials = getInitials(profile.name);
  const specialty = profile.categoryNames?.join(", ") || "Profissional";
  const { colors } = getCategoryMeta(profile.categoryNames?.[0] ?? "");

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
        {/* ── Hero Header ──────────────────────────────────────────────────── */}
        <View className="relative">
          <LinearGradient
            colors={["#1E40AF", "#3B82F6"]}
            style={{ height: 200 + insets.top }}
          />
          <TouchableOpacity
            onPress={() => router.back()}
            style={{ top: insets.top + 16 }}
            className="absolute left-6 w-10 h-10 rounded-full bg-white/90 items-center justify-center shadow-lg"
          >
            <ChevronLeft size={24} color="#1F2937" />
          </TouchableOpacity>

          {/* Avatar com iniciais */}
          <View className="absolute -bottom-16 self-center items-center">
            <LinearGradient
              colors={colors}
              className="w-32 h-32 rounded-2xl border-4 border-white shadow-lg items-center justify-center"
            >
              <Text className="text-white text-4xl font-bold">{initials}</Text>
            </LinearGradient>
          </View>
        </View>

        {/* ── Profile Card ─────────────────────────────────────────────────── */}
        <View className="px-6 mt-20 pb-2">
          <Card variant="elevated">
            <View className="items-center mb-4">
              <Text className="text-2xl font-bold text-[#1E293B] mb-1 text-center">
                {profile.name}
              </Text>
              <Text className="text-[#64748B] mb-3">{specialty}</Text>

              <View className="flex-row items-center gap-4 flex-wrap justify-center">
                {profile.averageRating !== null ? (
                  <View className="flex-row items-center gap-1.5">
                    <Stars rating={profile.averageRating} size={18} />
                    <Text className="font-semibold text-[#1E293B]">
                      {profile.averageRating.toFixed(1)}
                    </Text>
                    <Text className="text-[#64748B]">
                      ({profile.totalReviews} avaliações)
                    </Text>
                  </View>
                ) : (
                  <Text className="text-[#64748B]">Sem avaliações ainda</Text>
                )}
                <View className="flex-row items-center gap-1.5">
                  <View
                    className={`w-2.5 h-2.5 rounded-full ${
                      profile.active ? "bg-[#22C55E]" : "bg-[#CBD5E1]"
                    }`}
                  />
                  <Text
                    className={
                      profile.active ? "text-[#22C55E]" : "text-[#94A3B8]"
                    }
                  >
                    {profile.active ? "Disponível" : "Indisponível"}
                  </Text>
                </View>
              </View>
            </View>

            {/* Stats */}
            <View className="flex-row gap-4 pt-4 border-t border-gray-100">
              <View className="flex-1 items-center">
                <View className="w-10 h-10 rounded-full bg-[#3B82F6]/10 items-center justify-center mb-2">
                  <Award size={20} color="#3B82F6" />
                </View>
                <Text className="font-semibold text-[#1E293B]">
                  {profile.totalServicesCompleted ?? "—"}
                </Text>
                <Text className="text-[#64748B] text-sm">Serviços</Text>
              </View>
              <View className="flex-1 items-center">
                <View className="w-10 h-10 rounded-full bg-[#22C55E]/10 items-center justify-center mb-2">
                  <Shield size={20} color="#22C55E" />
                </View>
                <Text className="font-semibold text-[#1E293B]">
                  {profile.approvalRate !== null
                    ? `${profile.approvalRate}%`
                    : "—"}
                </Text>
                <Text className="text-[#64748B] text-sm">Aprovação</Text>
              </View>
              <View className="flex-1 items-center">
                <View className="w-10 h-10 rounded-full bg-[#FB923C]/10 items-center justify-center mb-2">
                  <Clock size={20} color="#FB923C" />
                </View>
                <Text className="font-semibold text-[#1E293B]">
                  {profile.services.length}
                </Text>
                <Text className="text-[#64748B] text-sm">Tipos de serviço</Text>
              </View>
            </View>
          </Card>
        </View>

        {/* ── Bio ──────────────────────────────────────────────────────────── */}
        {profile.bio ? (
          <View className="px-6 mt-4">
            <Card>
              <Text className="text-lg font-semibold mb-3 text-[#1E293B]">
                Sobre
              </Text>
              <Text className="text-[#64748B] leading-6">{profile.bio}</Text>
            </Card>
          </View>
        ) : null}

        {/* ── Serviços ─────────────────────────────────────────────────────── */}
        {profile.services.length > 0 && (
          <View className="px-6 mt-4">
            <Card>
              <View className="flex-row items-center gap-2 mb-4">
                <Wrench size={18} color="#3B82F6" />
                <Text className="text-lg font-semibold text-[#1E293B]">
                  Serviços oferecidos
                </Text>
              </View>
              {profile.services.map((svc, index) => (
                <View
                  key={svc.id}
                  className="flex-row items-center justify-between py-3"
                  style={{
                    borderBottomWidth:
                      index < profile.services.length - 1 ? 1 : 0,
                    borderBottomColor: "#F1F5F9",
                  }}
                >
                  <View className="flex-1 mr-4">
                    <Text className="text-[#1E293B] font-medium">
                      {svc.name}
                    </Text>
                    {svc.description ? (
                      <Text
                        className="text-[#64748B] text-sm mt-0.5"
                        numberOfLines={1}
                      >
                        {svc.description}
                      </Text>
                    ) : null}
                  </View>
                  <Text className="text-[#3B82F6] font-semibold">
                    {formatPrice(svc.priceCents)}
                  </Text>
                </View>
              ))}
            </Card>
          </View>
        )}

        {/* ── Avaliações ───────────────────────────────────────────────────── */}
        <View className="px-6 mt-4 mb-32">
          <Card>
            <View className="flex-row items-center gap-2 mb-4">
              <Star size={18} color="#FACC15" fill="#FACC15" />
              <Text className="text-lg font-semibold text-[#1E293B]">
                Avaliações{totalReviews > 0 ? ` (${totalReviews})` : ""}
              </Text>
            </View>

            {reviews.length === 0 ? (
              <Text className="text-[#64748B] text-center py-4">
                Nenhuma avaliação ainda.
              </Text>
            ) : (
              reviews.map((review, index) => (
                <View
                  key={review.id}
                  className="pb-4 mb-4"
                  style={{
                    borderBottomWidth: index < reviews.length - 1 ? 1 : 0,
                    borderBottomColor: "#F1F5F9",
                  }}
                >
                  <View className="flex-row items-center justify-between mb-2">
                    <Text className="font-semibold text-[#1E293B]">
                      {review.clientName}
                    </Text>
                    <Stars rating={review.rating} size={14} />
                  </View>
                  <Text className="text-[#64748B] mb-1">{review.comment}</Text>
                  <Text className="text-[#94A3B8] text-xs">
                    {formatReviewDate(review.createdAt)}
                  </Text>
                </View>
              ))
            )}
          </Card>
        </View>
      </ScrollView>

      {/* ── Bottom Actions ───────────────────────────────────────────────────── */}
      <SafeAreaView
        edges={["bottom"]}
        className="absolute bottom-0 left-0 right-0 bg-white border-t border-gray-200"
      >
        <View className="px-6 py-4">
          <View className="flex-row gap-3">
            <TouchableOpacity className="w-12 h-12 rounded-xl border-2 border-[#3B82F6] items-center justify-center">
              <Phone size={20} color="#3B82F6" />
            </TouchableOpacity>
            <TouchableOpacity className="w-12 h-12 rounded-xl border-2 border-[#3B82F6] items-center justify-center">
              <MessageCircle size={20} color="#3B82F6" />
            </TouchableOpacity>
            <Button
              onPress={() =>
                router.push({
                  pathname: "/(client)/service-request",
                  params: { professionalId: profile.id },
                })
              }
              variant="primary"
              className="flex-1"
            >
              Solicitar Serviço
            </Button>
          </View>
        </View>
      </SafeAreaView>
    </View>
  );
}
