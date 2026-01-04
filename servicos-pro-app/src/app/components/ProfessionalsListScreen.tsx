import { ChevronLeft, Search, MapPin, Star, Filter } from 'lucide-react';

interface ProfessionalsListScreenProps {
  onNavigate: (screen: string) => void;
}

const professionals = [
  {
    name: 'João Santos',
    specialty: 'Eletricista',
    rating: 4.8,
    reviews: 127,
    distance: '0.8 km',
    price: 'R$ 80',
    available: true,
    image: 'https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBlbGVjdHJpY2lhbiUyMHBvcnRyYWl0fGVufDF8fHx8MTc2NzQ4ODA4OXww&ixlib=rb-4.1.0&q=80&w=1080',
  },
  {
    name: 'Carlos Oliveira',
    specialty: 'Eletricista',
    rating: 4.9,
    reviews: 98,
    distance: '1.2 km',
    price: 'R$ 85',
    available: true,
    image: 'https://images.unsplash.com/photo-1758519290233-a03c1d17ecc9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzZXJ2aWNlJTIwcHJvZmVzc2lvbmFsJTIwc21pbGluZ3xlbnwxfHx8fDE3Njc0ODk2NjV8MA&ixlib=rb-4.1.0&q=80&w=1080',
  },
  {
    name: 'Pedro Almeida',
    specialty: 'Eletricista',
    rating: 4.7,
    reviews: 156,
    distance: '1.8 km',
    price: 'R$ 75',
    available: false,
    image: 'https://images.unsplash.com/photo-1646227655718-dd721b681d91?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjb25zdHJ1Y3Rpb24lMjB3b3JrZXIlMjBwb3J0cmFpdHxlbnwxfHx8fDE3Njc0ODk2NjR8MA&ixlib=rb-4.1.0&q=80&w=1080',
  },
  {
    name: 'Ricardo Lima',
    specialty: 'Eletricista',
    rating: 4.6,
    reviews: 89,
    distance: '2.3 km',
    price: 'R$ 70',
    available: true,
    image: 'https://images.unsplash.com/photo-1635221798248-8a3452ad07cd?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwbHVtYmVyJTIwcHJvZmVzc2lvbmFsJTIwd29ya3xlbnwxfHx8fDE3Njc0NDE1MDl8MA&ixlib=rb-4.1.0&q=80&w=1080',
  },
];

export function ProfessionalsListScreen({ onNavigate }: ProfessionalsListScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-6">
      {/* Header */}
      <div className="bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] px-6 pt-12 pb-6">
        <button
          onClick={() => onNavigate('categories')}
          className="text-white mb-6 flex items-center"
        >
          <ChevronLeft className="w-6 h-6" />
          <span className="ml-2">Voltar</span>
        </button>
        <h1 className="text-white mb-2">Eletricistas</h1>
        <p className="text-blue-100">45 profissionais disponíveis</p>

        {/* Search Bar */}
        <div className="relative mt-6">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-[#64748B]" />
          <input
            type="text"
            placeholder="Buscar por nome..."
            className="w-full pl-12 pr-4 py-3 rounded-xl bg-white shadow-lg focus:outline-none"
          />
        </div>
      </div>

      {/* Filters */}
      <div className="px-6 pt-4 pb-2">
        <div className="flex items-center gap-3 overflow-x-auto pb-2">
          <button className="px-4 py-2 rounded-full bg-[#3B82F6] text-white whitespace-nowrap shadow-sm">
            Mais próximos
          </button>
          <button className="px-4 py-2 rounded-full bg-white text-[#64748B] border border-gray-200 whitespace-nowrap">
            Melhor avaliados
          </button>
          <button className="px-4 py-2 rounded-full bg-white text-[#64748B] border border-gray-200 whitespace-nowrap">
            Menor preço
          </button>
          <button className="p-2 rounded-full bg-white text-[#64748B] border border-gray-200">
            <Filter className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Location */}
      <div className="px-6 py-3 flex items-center gap-2 text-[#64748B]">
        <MapPin className="w-4 h-4" />
        <span>São Paulo, SP - Centro</span>
      </div>

      {/* Professionals List */}
      <div className="px-6 space-y-4">
        {professionals.map((professional, index) => (
          <button
            key={index}
            onClick={() => onNavigate('professionalProfile')}
            className="w-full bg-white rounded-2xl p-4 shadow-sm hover:shadow-md transition-shadow border border-gray-100"
          >
            <div className="flex gap-4">
              <img
                src={professional.image}
                alt={professional.name}
                className="w-20 h-20 rounded-xl object-cover flex-shrink-0"
              />
              <div className="flex-1 text-left min-w-0">
                <div className="flex items-start justify-between mb-1">
                  <h3 className="truncate">{professional.name}</h3>
                  {professional.available && (
                    <div className="flex items-center gap-1 text-[#22C55E] ml-2">
                      <div className="w-2 h-2 rounded-full bg-[#22C55E]"></div>
                    </div>
                  )}
                </div>
                <p className="text-[#64748B] mb-2">{professional.specialty}</p>
                <div className="flex items-center gap-4 flex-wrap">
                  <div className="flex items-center gap-1">
                    <Star className="w-4 h-4 fill-[#FACC15] text-[#FACC15]" />
                    <span className="text-[#64748B]">{professional.rating}</span>
                    <span className="text-[#64748B]">({professional.reviews})</span>
                  </div>
                  <div className="flex items-center gap-1 text-[#64748B]">
                    <MapPin className="w-4 h-4" />
                    <span>{professional.distance}</span>
                  </div>
                </div>
              </div>
              <div className="text-right flex-shrink-0">
                <p className="text-[#64748B] mb-1">A partir de</p>
                <p className="text-[#3B82F6]">{professional.price}</p>
              </div>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
