import { cn } from "@/utils/cn";
import { Eye, EyeOff } from "lucide-react-native";
import React, { useState } from "react";
import {
  StyleSheet,
  Text,
  TextInput,
  TextInputProps,
  TouchableOpacity,
  View,
} from "react-native";

interface InputProps extends TextInputProps {
  label?: string;
  error?: string;
  containerClassName?: string;
  inputClassName?: string;
  showPasswordToggle?: boolean;
}

export function Input({
  label,
  error,
  containerClassName,
  inputClassName,
  className,
  style,
  showPasswordToggle = false,
  secureTextEntry,
  ...props
}: InputProps) {
  const [isPasswordVisible, setIsPasswordVisible] = useState(false);
  const isPassword = secureTextEntry && showPasswordToggle;

  return (
    <View className={cn("mb-4", containerClassName)}>
      {label && (
        <Text className="text-[#64748B] mb-2 text-sm font-medium">{label}</Text>
      )}
      <View className="relative">
        <TextInput
          className={cn(
            "w-full px-4 py-3 rounded-xl border border-gray-200 bg-white text-gray-900",
            error && "border-red-500",
            isPassword && "pr-12",
            inputClassName,
            className
          )}
          placeholderTextColor="#94A3B8"
          style={[styles.input, style]}
          secureTextEntry={isPassword ? !isPasswordVisible : secureTextEntry}
          {...props}
        />
        {isPassword && (
          <TouchableOpacity
            onPress={() => setIsPasswordVisible(!isPasswordVisible)}
            className="absolute right-4"
            style={{
              top: "50%",
              transform: [{ translateY: -10 }],
            }}
            activeOpacity={0.7}
          >
            {isPasswordVisible ? (
              <EyeOff size={20} color="#64748B" />
            ) : (
              <Eye size={20} color="#64748B" />
            )}
          </TouchableOpacity>
        )}
      </View>
      {error && <Text className="text-red-500 text-sm mt-1">{error}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  input: {
    fontSize: 16,
  },
});
