// Navigation Types
export type RootStackParamList = {
  index: undefined;
  login: undefined;
  signup: undefined;
  "(client)": undefined;
  "(provider)": undefined;
  "client/home": undefined;
  "client/categories": undefined;
  "client/professionals": undefined;
  "client/professional-profile": { professionalId?: string };
  "client/service-request": { professionalId?: string };
  "client/payment": undefined;
  "provider/home": undefined;
  "provider/requests": undefined;
  "provider/profile": undefined;
};

// User Types
export type UserType = "client" | "provider";

export interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  type: UserType;
  avatar?: string;
}

// Professional Types
export interface Professional {
  id: string;
  name: string;
  specialty: string;
  rating: number;
  reviews: number;
  distance: string;
  price: string;
  available: boolean;
  image: string;
  location: string;
  services: Service[];
  about?: string;
  stats?: {
    services: number;
    approval: number;
    experience: string;
  };
}

export interface Service {
  name: string;
  price: string;
  active?: boolean;
}

// Category Types
export interface Category {
  id: string;
  name: string;
  icon: string;
  color: string;
  count: number;
  description: string;
}

// Request Types
export interface ServiceRequest {
  id: string;
  client: string;
  service: string;
  date: string;
  address: string;
  distance?: string;
  price: string;
  status: "pending" | "accepted" | "completed" | "rejected";
  description?: string;
}

// Review Types
export interface Review {
  id: string;
  name: string;
  rating: number;
  comment: string;
  date: string;
}
