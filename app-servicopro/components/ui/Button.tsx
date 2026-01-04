import { cn } from "@/utils/cn";
import React from "react";
import { ActivityIndicator, Text, TouchableOpacity } from "react-native";

interface ButtonProps {
  children: React.ReactNode;
  onPress: () => void;
  variant?: "primary" | "secondary" | "outline" | "ghost";
  size?: "sm" | "md" | "lg";
  disabled?: boolean;
  loading?: boolean;
  className?: string;
  fullWidth?: boolean;
}

export function Button({
  children,
  onPress,
  variant = "primary",
  size = "md",
  disabled = false,
  loading = false,
  className,
  fullWidth = false,
}: ButtonProps) {
  const getVariantStyle = () => {
    switch (variant) {
      case "primary":
        return "bg-[#3B82F6]";
      case "secondary":
        return "bg-[#64748B]";
      case "outline":
        return "border-2 border-[#3B82F6] bg-transparent";
      case "ghost":
        return "bg-transparent";
      default:
        return "bg-[#3B82F6]";
    }
  };

  const getTextColor = () => {
    switch (variant) {
      case "primary":
      case "secondary":
        return "text-white";
      case "outline":
      case "ghost":
        return "text-[#3B82F6]";
      default:
        return "text-white";
    }
  };

  const getSizeStyle = () => {
    switch (size) {
      case "sm":
        return "px-4 py-2";
      case "md":
        return "px-6 py-3";
      case "lg":
        return "px-8 py-4";
      default:
        return "px-6 py-3";
    }
  };

  const getTextSize = () => {
    switch (size) {
      case "sm":
        return "text-sm";
      case "md":
        return "text-base";
      case "lg":
        return "text-lg";
      default:
        return "text-base";
    }
  };

  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={disabled || loading}
      className={cn(
        "rounded-xl items-center justify-center",
        getVariantStyle(),
        getSizeStyle(),
        fullWidth && "w-full",
        disabled && "opacity-50",
        className
      )}
      activeOpacity={0.7}
    >
      {loading ? (
        <ActivityIndicator
          color={
            variant === "primary" || variant === "secondary"
              ? "#FFFFFF"
              : "#3B82F6"
          }
        />
      ) : (
        <Text className={cn("font-semibold", getTextColor(), getTextSize())}>
          {children}
        </Text>
      )}
    </TouchableOpacity>
  );
}
