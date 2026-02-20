import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import {
  createProviderService,
  deleteProviderService,
  listMyProviderServices,
  listServiceCategories,
  updateProviderService,
} from "@/services/apiClient";
import {
  CreateProviderServiceRequest,
  ProviderService,
  ServiceCategory,
} from "@/types";
import { extractApiError } from "@/utils/apiError";
import { LinearGradient } from "expo-linear-gradient";
import { useRouter } from "expo-router";
import {
  AlertCircle,
  Briefcase,
  ChevronDown,
  ChevronLeft,
  Edit2,
  Plus,
  Tag,
  Trash2,
  X,
} from "lucide-react-native";
import React, { useCallback, useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  FlatList,
  KeyboardAvoidingView,
  Modal,
  Platform,
  RefreshControl,
  ScrollView,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

// ── Helpers ──────────────────────────────────────────────────────────────────

function formatPrice(cents: number): string {
  return (cents / 100).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

function parsePriceToCents(raw: string): number {
  // Accept "150", "150,00", "R$ 150,00"
  const cleaned = raw.replace(/[^\d,]/g, "").replace(",", ".");
  const value = parseFloat(cleaned);
  if (isNaN(value)) return 0;
  // If user typed like "150.00" treat as reais, if "15000" treat as reais too
  return Math.round(value * 100);
}

// ── Form Modal ────────────────────────────────────────────────────────────────

interface ServiceFormModalProps {
  visible: boolean;
  onClose: () => void;
  onSaved: () => void;
  categories: ServiceCategory[];
  editTarget: ProviderService | null;
}

function ServiceFormModal({
  visible,
  onClose,
  onSaved,
  categories,
  editTarget,
}: ServiceFormModalProps) {
  const isEditing = editTarget !== null;

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [priceInput, setPriceInput] = useState("");
  const [selectedCategory, setSelectedCategory] =
    useState<ServiceCategory | null>(null);
  const [categoryPickerVisible, setCategoryPickerVisible] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Populate form when editing
  useEffect(() => {
    if (editTarget) {
      setName(editTarget.name);
      setDescription(editTarget.description);
      setPriceInput((editTarget.priceCents / 100).toFixed(2).replace(".", ","));

      // Resolve category: prefer enriched nested object, fallback to categoryId lookup
      const resolved =
        editTarget.category ??
        categories.find((c) => c.id === editTarget.categoryId) ??
        null;
      setSelectedCategory(resolved);
    } else {
      setName("");
      setDescription("");
      setPriceInput("");
      setSelectedCategory(null);
    }
    setError(null);
  }, [editTarget, visible, categories]);

  const handleSubmit = async () => {
    if (!name.trim()) {
      setError("Informe o nome do serviço.");
      return;
    }
    if (!selectedCategory) {
      setError("Selecione uma categoria.");
      return;
    }
    const priceCents = parsePriceToCents(priceInput);
    if (priceCents <= 0) {
      setError("Informe um preço válido (ex: 150,00).");
      return;
    }

    setError(null);
    setSubmitting(true);
    try {
      const payload: CreateProviderServiceRequest = {
        categoryId: selectedCategory.id,
        name: name.trim(),
        description: description.trim(),
        priceCents,
      };

      if (isEditing) {
        await updateProviderService(editTarget!.id, payload);
      } else {
        await createProviderService(payload);
      }

      onSaved();
      onClose();
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      visible={visible}
      animationType="slide"
      presentationStyle="pageSheet"
      onRequestClose={onClose}
    >
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        className="flex-1 bg-[#F8FAFC]"
      >
        <ScrollView
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}
        >
          {/* Modal header */}
          <View className="flex-row items-center justify-between px-6 pt-6 pb-4 bg-white border-b border-gray-100">
            <Text className="text-xl font-bold text-[#1E293B]">
              {isEditing ? "Editar serviço" : "Novo serviço"}
            </Text>
            <TouchableOpacity
              onPress={onClose}
              className="w-9 h-9 rounded-full bg-gray-100 items-center justify-center"
            >
              <X size={18} color="#64748B" />
            </TouchableOpacity>
          </View>

          <View className="px-6 pt-6">
            {error && (
              <View className="flex-row items-center gap-2 bg-red-50 border border-red-200 rounded-xl p-4 mb-4">
                <AlertCircle size={16} color="#EF4444" />
                <Text className="text-red-600 text-sm flex-1">{error}</Text>
              </View>
            )}

            {/* Category picker */}
            <View className="mb-4">
              <Text className="text-[#64748B] mb-2 text-sm font-medium">
                Categoria *
              </Text>
              <TouchableOpacity
                onPress={() => setCategoryPickerVisible(true)}
                className="flex-row items-center justify-between w-full px-4 py-3 rounded-xl border border-gray-200 bg-white"
              >
                <Text
                  className={
                    selectedCategory ? "text-gray-900" : "text-gray-400"
                  }
                >
                  {selectedCategory
                    ? selectedCategory.name
                    : "Selecione uma categoria"}
                </Text>
                <ChevronDown size={18} color="#64748B" />
              </TouchableOpacity>
            </View>

            <Input
              label="Nome do serviço *"
              placeholder="Ex: Instalação de tomada"
              value={name}
              onChangeText={setName}
              autoCapitalize="sentences"
            />

            <Input
              label="Descrição"
              placeholder="Ex: Serviço residencial com garantia de 90 dias"
              value={description}
              onChangeText={setDescription}
              autoCapitalize="sentences"
              multiline
              numberOfLines={3}
              inputClassName="h-20"
              textAlignVertical="top"
            />

            <Input
              label="Preço (R$) *"
              placeholder="Ex: 150,00"
              value={priceInput}
              onChangeText={setPriceInput}
              keyboardType="decimal-pad"
            />

            <Button
              onPress={handleSubmit}
              loading={submitting}
              disabled={submitting}
              fullWidth
              size="lg"
            >
              {isEditing ? "Salvar alterações" : "Criar serviço"}
            </Button>

            <View className="h-8" />
          </View>
        </ScrollView>
      </KeyboardAvoidingView>

      {/* Category picker modal */}
      <Modal
        visible={categoryPickerVisible}
        animationType="fade"
        transparent
        onRequestClose={() => setCategoryPickerVisible(false)}
      >
        <TouchableOpacity
          activeOpacity={1}
          onPress={() => setCategoryPickerVisible(false)}
          className="flex-1 bg-black/50 justify-end"
        >
          <TouchableOpacity
            activeOpacity={1}
            className="bg-white rounded-t-3xl px-6 pt-6 pb-10 max-h-[60%]"
          >
            <Text className="text-lg font-bold text-[#1E293B] mb-4">
              Categoria
            </Text>
            <FlatList
              data={categories}
              keyExtractor={(item) => item.id}
              showsVerticalScrollIndicator={false}
              renderItem={({ item }) => (
                <TouchableOpacity
                  onPress={() => {
                    setSelectedCategory(item);
                    setCategoryPickerVisible(false);
                  }}
                  className={`flex-row items-center gap-3 py-4 border-b border-gray-100 ${
                    selectedCategory?.id === item.id ? "opacity-100" : ""
                  }`}
                >
                  <View
                    className={`w-3 h-3 rounded-full ${
                      selectedCategory?.id === item.id
                        ? "bg-[#3B82F6]"
                        : "bg-gray-200"
                    }`}
                  />
                  <View className="flex-1">
                    <Text className="font-semibold text-[#1E293B]">
                      {item.name}
                    </Text>
                    {item.description ? (
                      <Text className="text-[#64748B] text-xs mt-0.5">
                        {item.description}
                      </Text>
                    ) : null}
                  </View>
                </TouchableOpacity>
              )}
            />
          </TouchableOpacity>
        </TouchableOpacity>
      </Modal>
    </Modal>
  );
}

// ── Service Card ──────────────────────────────────────────────────────────────

interface ServiceCardProps {
  service: ProviderService;
  onEdit: (service: ProviderService) => void;
  onDelete: (service: ProviderService) => void;
}

function ServiceCard({ service, onEdit, onDelete }: ServiceCardProps) {
  // category is resolved client-side via the categories map in loadData
  const categoryName = service.category?.name ?? "Sem categoria";

  return (
    <Card className="mb-4">
      <View className="flex-row items-start justify-between">
        <View className="flex-1 mr-3">
          {/* Category badge */}
          <View className="flex-row items-center gap-1.5 mb-2">
            <Tag size={12} color="#3B82F6" />
            <Text className="text-[#3B82F6] text-xs font-medium">
              {categoryName}
            </Text>
          </View>
          <Text className="text-[#1E293B] font-bold text-base mb-1">
            {service.name}
          </Text>
          {service.description ? (
            <Text className="text-[#64748B] text-sm mb-3" numberOfLines={2}>
              {service.description}
            </Text>
          ) : null}
          <Text className="text-[#22C55E] font-bold text-lg">
            {formatPrice(service.priceCents)}
          </Text>
        </View>

        {/* Actions */}
        <View className="flex-row gap-2">
          <TouchableOpacity
            onPress={() => onEdit(service)}
            className="w-9 h-9 rounded-xl bg-[#3B82F6]/10 items-center justify-center"
          >
            <Edit2 size={16} color="#3B82F6" />
          </TouchableOpacity>
          <TouchableOpacity
            onPress={() => onDelete(service)}
            className="w-9 h-9 rounded-xl bg-red-50 items-center justify-center"
          >
            <Trash2 size={16} color="#EF4444" />
          </TouchableOpacity>
        </View>
      </View>
    </Card>
  );
}

// ── Main Screen ───────────────────────────────────────────────────────────────

export default function ProviderMyServicesScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();

  const [services, setServices] = useState<ProviderService[]>([]);
  const [categories, setCategories] = useState<ServiceCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formVisible, setFormVisible] = useState(false);
  const [editTarget, setEditTarget] = useState<ProviderService | null>(null);

  const loadData = useCallback(async (silent = false) => {
    if (!silent) setLoading(true);
    setError(null);
    try {
      const [cats, svcs] = await Promise.all([
        listServiceCategories(),
        listMyProviderServices(),
      ]);
      setCategories(cats);

      // Enrich each service with its resolved category object
      const catMap = new Map(cats.map((c) => [c.id, c]));
      const enriched = svcs.map((s) => ({
        ...s,
        category: catMap.get(s.categoryId) ?? null,
      }));
      setServices(enriched);
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setLoading(false);
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

  const openCreate = () => {
    setEditTarget(null);
    setFormVisible(true);
  };

  const openEdit = (service: ProviderService) => {
    setEditTarget(service);
    setFormVisible(true);
  };

  const handleDelete = (service: ProviderService) => {
    Alert.alert(
      "Remover serviço",
      `Deseja remover "${service.name}"? Esta ação não pode ser desfeita.`,
      [
        { text: "Cancelar", style: "cancel" },
        {
          text: "Remover",
          style: "destructive",
          onPress: async () => {
            try {
              await deleteProviderService(service.id);
              setServices((prev) => prev.filter((s) => s.id !== service.id));
            } catch (err) {
              Alert.alert("Erro", extractApiError(err));
            }
          },
        },
      ]
    );
  };

  // ── Loading ─────────────────────────────────────────────────────────────────

  if (loading) {
    return (
      <View className="flex-1 bg-[#F8FAFC] items-center justify-center">
        <ActivityIndicator size="large" color="#3B82F6" />
        <Text className="mt-4 text-[#64748B]">Carregando serviços...</Text>
      </View>
    );
  }

  // ── Error ───────────────────────────────────────────────────────────────────

  if (error) {
    return (
      <View className="flex-1 bg-[#F8FAFC] items-center justify-center px-8">
        <AlertCircle size={64} color="#CBD5E1" />
        <Text className="text-lg font-semibold text-[#1E293B] mt-4 mb-2">
          Ops! Algo deu errado
        </Text>
        <Text className="text-[#64748B] text-center mb-6">{error}</Text>
        <TouchableOpacity
          onPress={() => loadData()}
          className="bg-[#3B82F6] px-8 py-3 rounded-2xl"
        >
          <Text className="text-white font-semibold">Tentar novamente</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View className="flex-1 bg-[#F8FAFC]">
      <StatusBar style="light" />

      {/* Header */}
      <LinearGradient
        colors={["#1E40AF", "#3B82F6"]}
        style={{ paddingTop: insets.top }}
        className="px-6 pb-8 rounded-b-[32px]"
      >
        <View className="flex-row items-center justify-between mt-4 mb-4">
          <TouchableOpacity
            onPress={() => router.back()}
            className="w-10 h-10 rounded-full bg-white/20 items-center justify-center"
          >
            <ChevronLeft size={24} color="#FFFFFF" />
          </TouchableOpacity>
          <Text className="text-white text-lg font-bold">Meus Serviços</Text>
          <TouchableOpacity
            onPress={openCreate}
            className="w-10 h-10 rounded-full bg-white/20 items-center justify-center"
          >
            <Plus size={22} color="#FFFFFF" />
          </TouchableOpacity>
        </View>

        {/* Summary */}
        <View className="bg-white/10 rounded-2xl px-5 py-4 flex-row items-center gap-4">
          <View className="w-12 h-12 rounded-xl bg-white/20 items-center justify-center">
            <Briefcase size={24} color="#FFFFFF" />
          </View>
          <View>
            <Text className="text-white text-2xl font-bold">
              {services.length}
            </Text>
            <Text className="text-blue-100 text-sm">
              {services.length === 1
                ? "serviço cadastrado"
                : "serviços cadastrados"}
            </Text>
          </View>
        </View>
      </LinearGradient>

      {/* Content */}
      <ScrollView
        className="flex-1 px-6 pt-6"
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            tintColor="#3B82F6"
          />
        }
      >
        {services.length === 0 ? (
          /* Empty state */
          <View className="items-center justify-center py-20">
            <View className="w-20 h-20 rounded-full bg-[#3B82F6]/10 items-center justify-center mb-4">
              <Briefcase size={36} color="#3B82F6" />
            </View>
            <Text className="text-xl font-bold text-[#1E293B] mb-2">
              Nenhum serviço ainda
            </Text>
            <Text className="text-[#64748B] text-center mb-8 px-4">
              Cadastre seus serviços para que os clientes possam te encontrar.
            </Text>
            <Button onPress={openCreate} size="lg">
              Cadastrar primeiro serviço
            </Button>
          </View>
        ) : (
          <>
            {services.map((service) => (
              <ServiceCard
                key={service.id}
                service={service}
                onEdit={openEdit}
                onDelete={handleDelete}
              />
            ))}
            {/* FAB area spacer */}
            <View className="h-24" />
          </>
        )}
      </ScrollView>

      {/* FAB — only when has services */}
      {services.length > 0 && (
        <View
          className="absolute right-6"
          style={{ bottom: insets.bottom + 24 }}
        >
          <TouchableOpacity
            onPress={openCreate}
            className="w-14 h-14 rounded-full bg-[#3B82F6] items-center justify-center shadow-lg"
            style={{
              shadowColor: "#3B82F6",
              shadowOffset: { width: 0, height: 4 },
              shadowOpacity: 0.4,
              shadowRadius: 8,
              elevation: 8,
            }}
          >
            <Plus size={26} color="#FFFFFF" />
          </TouchableOpacity>
        </View>
      )}

      {/* Form modal */}
      <ServiceFormModal
        visible={formVisible}
        onClose={() => setFormVisible(false)}
        onSaved={() => loadData(true)}
        categories={categories}
        editTarget={editTarget}
      />
    </View>
  );
}

