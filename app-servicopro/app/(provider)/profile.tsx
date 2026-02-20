import { Card } from "@/components/ui/Card";
import { useAuth } from "@/context/AuthContext";
import { fetchMe } from "@/services/apiClient";
import { AuthUser } from "@/types/auth";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  Award,
  CalendarDays,
  CheckCircle2,
  ChevronLeft,
  Clock,
  LogOut,
  Mail,
  Phone,
  Shield,
  Star,
  UserCircle2,
} from "lucide-react-native";
import React, { useCallback, useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  RefreshControl,
  ScrollView,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

// ── Helpers ──────────────────────────────────────────────────────────────────

function formatPhone(raw: string): string {
  const digits = raw.replace(/\D/g, "");
  const local = digits.startsWith("55") ? digits.slice(2) : digits;
  if (local.length === 11) {
    return `(${local.slice(0, 2)}) ${local.slice(2, 7)}-${local.slice(7)}`;
  }
  if (local.length === 10) {
    return `(${local.slice(0, 2)}) ${local.slice(2, 6)}-${local.slice(6)}`;
  }
  return raw;
}

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString("pt-BR", {
    day: "2-digit",
    month: "long",
    year: "numeric",
  });
}

function getInitials(name: string): string {
  const parts = name.trim().split(" ");
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

// ── Main Screen ──────────────────────────────────────────────────────────────

export default function ProviderProfileScreen() {
  const router = useRouter();
  const { signOut } = useAuth();

  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadProfile = useCallback(async (silent = false) => {
    if (!silent) setLoading(true);
    setError(null);
    try {
      const data = await fetchMe();
      setUser(data);
    } catch {
      setError("Não foi possível carregar o perfil.");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  const onRefresh = useCallback(() => {
    setRefreshing(true);
    loadProfile(true);
  }, [loadProfile]);

  const handleLogout = () => {
    Alert.alert("Sair da conta", "Tem certeza que deseja sair?", [
      { text: "Cancelar", style: "cancel" },
      { text: "Sair", style: "destructive", onPress: () => signOut() },
    ]);
  };

  // ── Loading ───────────────────────────────────────────────────────────────

  if (loading) {
    return (
      <SafeAreaView className="flex-1 bg-[#F8FAFC] items-center justify-center">
        <ActivityIndicator size="large" color="#3B82F6" />
        <Text className="mt-4 text-[#64748B]">Carregando perfil...</Text>
      </SafeAreaView>
    );
  }

  // ── Error ─────────────────────────────────────────────────────────────────

  if (error || !user) {
    return (
      <SafeAreaView className="flex-1 bg-[#F8FAFC] items-center justify-center px-8">
        <UserCircle2 size={64} color="#CBD5E1" />
        <Text className="text-lg font-semibold text-[#1E293B] mt-4 mb-2">
          Ops! Algo deu errado
        </Text>
        <Text className="text-[#64748B] text-center mb-6">{error}</Text>
        <TouchableOpacity
          onPress={() => loadProfile()}
          className="bg-[#3B82F6] px-8 py-3 rounded-2xl"
        >
          <Text className="text-white font-semibold">Tentar novamente</Text>
        </TouchableOpacity>
      </SafeAreaView>
    );
  }

  const initials = getInitials(user.name);

  return (
    <SafeAreaView className="flex-1 bg-[#F8FAFC]" edges={["top"]}>
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
        <View className="relative">
          <LinearGradient colors={["#1E40AF", "#3B82F6"]} className="h-48" />

          <TouchableOpacity
            onPress={() => router.back()}
            className="absolute top-6 left-6 w-10 h-10 rounded-full bg-white/90 items-center justify-center shadow-lg"
          >
            <ChevronLeft size={24} color="#1F2937" />
          </TouchableOpacity>

          <TouchableOpacity
            onPress={handleLogout}
            className="absolute top-6 right-6 w-10 h-10 rounded-full bg-white/90 items-center justify-center shadow-lg"
          >
            <LogOut size={20} color="#EF4444" />
          </TouchableOpacity>

          {/* Avatar placeholder */}
          <View className="absolute -bottom-16 left-6">
            <View className="w-32 h-32 rounded-2xl border-4 border-white shadow-lg bg-[#1E40AF] items-center justify-center">
              <Text className="text-white text-4xl font-bold">{initials}</Text>
            </View>
          </View>
        </View>

        {/* ── Profile Info ─────────────────────────────────────────────────── */}
        <View className="px-6 mt-20 pb-6">
          {/* Name & role */}
          <View className="mb-6">
            <Text className="text-2xl font-bold mb-1">{user.name}</Text>
            <Text className="text-[#64748B] mb-3">
              {user.role === "PROVIDER" ? "Prestador de Serviços" : "Cliente"}
            </Text>
            <View className="flex-row items-center gap-4 flex-wrap">
              <View className="flex-row items-center gap-1">
                <Star size={20} color="#FACC15" fill="#FACC15" />
                <Text className="font-semibold">—</Text>
                <Text className="text-[#64748B]">(sem avaliações ainda)</Text>
              </View>
              {user.active && (
                <View className="flex-row items-center gap-1">
                  <View className="w-3 h-3 rounded-full bg-[#22C55E]" />
                  <Text className="text-[#22C55E]">Ativo</Text>
                </View>
              )}
            </View>
          </View>

          {/* Stats */}
          <View className="flex-row gap-4 mb-6">
            <Card className="flex-1 items-center">
              <View className="w-10 h-10 rounded-full bg-[#3B82F6]/10 items-center justify-center mb-2">
                <Award size={20} color="#3B82F6" />
              </View>
              <Text className="font-semibold mb-1">—</Text>
              <Text className="text-[#64748B] text-sm">Serviços</Text>
            </Card>
            <Card className="flex-1 items-center">
              <View className="w-10 h-10 rounded-full bg-[#22C55E]/10 items-center justify-center mb-2">
                <Shield size={20} color="#22C55E" />
              </View>
              <Text className="font-semibold mb-1">—</Text>
              <Text className="text-[#64748B] text-sm">Aprovação</Text>
            </Card>
            <Card className="flex-1 items-center">
              <View className="w-10 h-10 rounded-full bg-[#FB923C]/10 items-center justify-center mb-2">
                <Clock size={20} color="#FB923C" />
              </View>
              <Text className="font-semibold mb-1">—</Text>
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
                <Text className="text-[#64748B]">
                  {formatPhone(user.phone)}
                </Text>
              </View>
              <View className="flex-row items-center gap-3 mb-3">
                <Mail size={20} color="#64748B" />
                <Text className="text-[#64748B]">{user.email}</Text>
              </View>
              <View className="flex-row items-center gap-3">
                <CalendarDays size={20} color="#64748B" />
                <Text className="text-[#64748B]">
                  Membro desde {formatDate(user.createdAt)}
                </Text>
              </View>
            </View>
          </Card>

          {/* Account status */}
          <Card className="mb-6">
            <Text className="text-lg font-semibold mb-4">Status da conta</Text>
            <View className="flex-row items-center gap-3">
              <CheckCircle2
                size={20}
                color={user.active ? "#22C55E" : "#EF4444"}
              />
              <Text
                className={user.active ? "text-[#22C55E]" : "text-[#EF4444]"}
              >
                {user.active ? "Conta ativa e verificada" : "Conta inativa"}
              </Text>
            </View>
          </Card>

          {/* Logout */}
          <TouchableOpacity
            onPress={handleLogout}
            className="bg-white rounded-2xl p-5 shadow-sm border border-red-100 flex-row items-center justify-center gap-3 mb-4"
          >
            <LogOut size={20} color="#EF4444" />
            <Text className="text-[#EF4444] font-semibold text-base">
              Sair da conta
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
