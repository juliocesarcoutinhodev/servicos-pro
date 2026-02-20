import {
  Armchair,
  Car,
  Droplet,
  Hammer,
  Home as HomeIcon,
  Leaf,
  PaintBucket,
  Scissors,
  Snowflake,
  Wrench,
  Zap,
} from "lucide-react-native";
import type { LucideIcon } from "lucide-react-native";

/**
 * Maps backend category names (case-insensitive) to display metadata.
 * Add entries here whenever a new category is seeded in the backend.
 */
export interface CategoryMeta {
  icon: LucideIcon;
  colors: [string, string];
}

const META_MAP: Record<string, CategoryMeta> = {
  eletricista: { icon: Zap, colors: ["#FACC15", "#FB923C"] },
  encanador: { icon: Wrench, colors: ["#60A5FA", "#06B6D4"] },
  diarista: { icon: HomeIcon, colors: ["#6B7280", "#4B5563"] },
  pintor: { icon: PaintBucket, colors: ["#3B82F6", "#6366F1"] },
  "montador de moveis": { icon: Armchair, colors: ["#D97706", "#F97316"] },
  jardineiro: { icon: Leaf, colors: ["#22C55E", "#10B981"] },
  "tecnico de ar-condicionado": { icon: Snowflake, colors: ["#0EA5E9", "#06B6D4"] },
  "marido de aluguel": { icon: Hammer, colors: ["#F97316", "#EF4444"] },
  mecanica: { icon: Car, colors: ["#EF4444", "#F97316"] },
  "cabeleireiro": { icon: Scissors, colors: ["#F472B6", "#FB7185"] },
  default: { icon: Wrench, colors: ["#64748B", "#94A3B8"] },
};

/**
 * Returns display metadata (icon + gradient colors) for a category name.
 * Falls back to a generic icon if the name is not mapped.
 */
export function getCategoryMeta(name: string): CategoryMeta {
  const key = name.trim().toLowerCase();
  return META_MAP[key] ?? META_MAP["default"];
}

