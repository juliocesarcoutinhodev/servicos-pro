import { cn } from "@/utils/cn";
import React from "react";
import { View, ViewProps } from "react-native";

interface CardProps extends ViewProps {
  children: React.ReactNode;
  variant?: "default" | "elevated" | "outlined";
  className?: string;
}

export function Card({
  children,
  variant = "default",
  className,
  ...props
}: CardProps) {
  const variantStyles = {
    default: "bg-white shadow-sm border border-gray-100",
    elevated: "bg-white shadow-lg border border-gray-100",
    outlined: "bg-white border-2 border-gray-200",
  };

  return (
    <View
      className={cn("rounded-2xl p-5", variantStyles[variant], className)}
      {...props}
    >
      {children}
    </View>
  );
}
