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

// Category Types (legacy UI mock — kept for client-side display mapping)
export interface Category {
  id: string;
  name: string;
  icon: string;
  color: string;
  count: number;
  description: string;
}

// ── Backend-aligned types ────────────────────────────────────────────────────

/** Mirrors GET /api/v1/services/categories response item */
export interface ServiceCategory {
  id: string;
  name: string;
  description: string;
}

/** Mirrors POST /api/v1/services/categories request body */
export interface CreateServiceCategoryRequest {
  name: string;
  description: string;
}

/** Mirrors POST /api/v1/providers/services request body */
export interface CreateProviderServiceRequest {
  categoryId: string;
  name: string;
  description: string;
  /** Price in cents (e.g. 15000 = R$ 150,00) */
  priceCents: number;
}

/** Mirrors PUT /api/v1/providers/services/{id} request body */
export interface UpdateProviderServiceRequest {
  categoryId: string;
  name: string;
  description: string;
  priceCents: number;
}

/** Mirrors the provider service item returned by the backend */
export interface ProviderService {
  id: string;
  name: string;
  description: string;
  priceCents: number;
  /** Backend returns categoryId as a flat field — no nested object */
  categoryId: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  /** Resolved client-side by cross-referencing the categories list */
  category?: ServiceCategory | null;
}

/** Provider summary returned by GET /api/v1/providers */
export interface ProviderSummary {
  id: string;
  name: string;
  /** Category names the provider offers services in */
  categoryNames: string[];
  averageRating: number | null;
  totalReviews: number;
  active: boolean;
  serviceCount: number;
}

/** Service item embedded inside ProviderProfile */
export interface ProviderProfileService {
  id: string;
  name: string;
  description: string;
  priceCents: number;
}

/** Full provider profile returned by GET /api/v1/providers/{id} */
export interface ProviderProfile {
  id: string;
  name: string;
  categoryNames: string[];
  bio: string | null;
  averageRating: number | null;
  totalReviews: number;
  totalServicesCompleted: number | null;
  approvalRate: number | null;
  active: boolean;
  services: ProviderProfileService[];
}

/** Review item returned by GET /api/v1/providers/{id}/reviews */
export interface ProviderReview {
  id: string;
  clientName: string;
  rating: number;
  comment: string;
  createdAt: string;
}

/** Paginated response wrapper */
export interface Page<T> {
  content: T[];
  totalElements: number;
  page: number;
  size: number;
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
