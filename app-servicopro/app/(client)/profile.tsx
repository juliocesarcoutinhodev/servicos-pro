import { useAuth } from "@/context/AuthContext";
import { fetchMe } from "@/services/apiClient";
import { AuthUser } from "@/types/auth";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  CalendarDays,
  CheckCircle2,
  ChevronLeft,
  LogOut,
  Mail,
  Phone,
  ShieldCheck,
  User,
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

/**
 * Formats a phone string from E.164 (+5511...) to a readable Brazilian mask.
 * e.g. "+551194704876" → "(11) 94704-876"
 */
function formatPhone(raw: string): string {
  const digits = raw.replace(/\D/g, "");
  // Remove country code 55
  const local = digits.startsWith("55") ? digits.slice(2) : digits;
  if (local.length === 11) {
    return `(${local.slice(0, 2)}) ${local.slice(2, 7)}-${local.slice(7)}`;
  }
  if (local.length === 10) {
    return `(${local.slice(0, 2)}) ${local.slice(2, 6)}-${local.slice(6)}`;
  }
  return raw;
}

/**
 * Formats an ISO date string to a localised Brazilian date.
 * e.g. "2026-02-19T20:36:28.378815Z" → "19/02/2026"
 */
function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

/** Returns the user initials for the avatar placeholder. */
function getInitials(name: string): string {
  const parts = name.trim().split(" ");
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

// ── Info Row Component ───────────────────────────────────────────────────────

interface InfoRowProps {
  icon: React.ReactNode;
  label: string;
  value: string;
  isLast?: boolean;
}

function InfoRow({ icon, label, value, isLast = false }: InfoRowProps) {
  return (
    <View
      className={`flex-row items-center gap-4 py-4 ${
        !isLast ? "border-b border-gray-100" : ""
      }`}
    >
      <View className="w-10 h-10 rounded-full bg-[#EFF6FF] items-center justify-center">
        {icon}
      </View>
      <View className="flex-1">
        <Text className="text-xs text-[#94A3B8] mb-0.5">{label}</Text>
        <Text className="text-[#1E293B] font-medium">{value}</Text>
      </View>
    </View>
  );
}

// ── Main Screen ──────────────────────────────────────────────────────────────

export default function ClientProfileScreen() {
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
      setError("Não foi possível carregar o perfil. Tente novamente.");
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
      {
        text: "Sair",
        style: "destructive",
        onPress: () => signOut(),
      },
    ]);
  };

  // ── Loading state ─────────────────────────────────────────────────────────

  if (loading) {
    return (
      <SafeAreaView className="flex-1 bg-[#F8FAFC] items-center justify-center">
        <ActivityIndicator size="large" color="#3B82F6" />
        <Text className="mt-4 text-[#64748B]">Carregando perfil...</Text>
      </SafeAreaView>
    );
  }

  // ── Error state ───────────────────────────────────────────────────────────

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

  // ── Render ────────────────────────────────────────────────────────────────

  const initials = getInitials(user.name);
  const firstName = user.name.split(" ")[0];

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
        {/* ── Hero Header ─────────────────────────────────────────────────── */}
        <LinearGradient
          colors={["#1E40AF", "#3B82F6"]}
          className="pt-16 pb-10 px-6"
        >
          {/* Navigation */}
          <View className="flex-row items-center justify-between mb-8">
            <TouchableOpacity
              onPress={() => router.back()}
              className="w-10 h-10 rounded-full bg-white/20 items-center justify-center"
            >
              <ChevronLeft size={22} color="#FFFFFF" />
            </TouchableOpacity>
            <Text className="text-white text-lg font-bold">Meu Perfil</Text>
            <TouchableOpacity
              onPress={handleLogout}
              className="w-10 h-10 rounded-full bg-white/20 items-center justify-center"
            >
              <LogOut size={20} color="#FFFFFF" />
            </TouchableOpacity>
          </View>

          {/* Avatar + name */}
          <View className="items-center">
            <View className="w-24 h-24 rounded-full bg-white/25 border-4 border-white/50 items-center justify-center mb-4 shadow-lg">
              <Text className="text-white text-3xl font-bold">{initials}</Text>
            </View>
            <Text className="text-white text-2xl font-bold mb-1">
              {user.name}
            </Text>
            <View className="flex-row items-center gap-2">
              <View
                className={`px-3 py-1 rounded-full ${
                  user.role === "CLIENT"
                    ? "bg-[#60A5FA]/30"
                    : "bg-[#34D399]/30"
                }`}
              >
                <Text className="text-white text-xs font-medium">
                  {user.role === "CLIENT" ? "Cliente" : "Prestador"}
                </Text>
              </View>
              {user.active && (
                <View className="flex-row items-center gap-1 bg-[#22C55E]/20 px-3 py-1 rounded-full">
                  <View className="w-2 h-2 rounded-full bg-[#22C55E]" />
                  <Text className="text-[#DCFCE7] text-xs font-medium">
                    Ativo
                  </Text>
                </View>
              )}
            </View>
          </View>
        </LinearGradient>

        {/* ── Content ─────────────────────────────────────────────────────── */}
        <View className="px-6 -mt-4">

          {/* Welcome card */}
          <View className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 mb-4 flex-row items-center gap-4">
            <View className="w-12 h-12 rounded-xl bg-[#EFF6FF] items-center justify-center">
              <ShieldCheck size={24} color="#3B82F6" />
            </View>
            <View className="flex-1">
              <Text className="font-semibold text-[#1E293B] mb-0.5">
                Olá, {firstName}! 👋
              </Text>
              <Text className="text-[#64748B] text-sm">
                Sua conta está verificada e segura.
              </Text>
            </View>
          </View>

          {/* Personal info */}
          <View className="bg-white rounded-2xl px-5 shadow-sm border border-gray-100 mb-4">
            <Text className="text-base font-bold text-[#1E293B] pt-5 pb-1">
              Informações pessoais
            </Text>
            <InfoRow
              icon={<User size={18} color="#3B82F6" />}
              label="Nome completo"
              value={user.name}
            />
            <InfoRow
              icon={<Mail size={18} color="#3B82F6" />}
              label="E-mail"
              value={user.email}
            />
            <InfoRow
              icon={<Phone size={18} color="#3B82F6" />}
              label="Telefone"
              value={formatPhone(user.phone)}
            />
            <InfoRow
              icon={<CalendarDays size={18} color="#3B82F6" />}
              label="Membro desde"
              value={formatDate(user.createdAt)}
              isLast
            />
          </View>

          {/* Account info */}
          <View className="bg-white rounded-2xl px-5 shadow-sm border border-gray-100 mb-4">
            <Text className="text-base font-bold text-[#1E293B] pt-5 pb-1">
              Dados da conta
            </Text>
            <InfoRow
              icon={<UserCircle2 size={18} color="#3B82F6" />}
              label="ID da conta"
              value={user.id}
            />
            <InfoRow
              icon={<CheckCircle2 size={18} color={user.active ? "#22C55E" : "#EF4444"} />}
              label="Status"
              value={user.active ? "Conta ativa" : "Conta inativa"}
              isLast
            />
          </View>

          {/* Logout button */}
          <TouchableOpacity
            onPress={handleLogout}
            className="bg-white rounded-2xl p-5 shadow-sm border border-red-100 mb-8 flex-row items-center justify-center gap-3"
          >
            <LogOut size={20} color="#EF4444" />
            <Text className="text-[#EF4444] font-semibold text-base">
              Sair da conta
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </View>
  );
}

