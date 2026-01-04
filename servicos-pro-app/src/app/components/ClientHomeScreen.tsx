import { Search, MapPin, Zap, Wrench, Home as HomeIcon, Scissors, Palette, User } from 'lucide-react';

interface ClientHomeScreenProps {
  onNavigate: (screen: string) => void;
}

const categories = [
  { icon: Zap, name: 'Eletricista', color: 'from-yellow-400 to-orange-400', count: 45 },
  { icon: Wrench, name: 'Encanador', color: 'from-blue-400 to-cyan-400', count: 38 },
  { icon: HomeIcon, name: 'Pedreiro', color: 'from-gray-500 to-gray-600', count: 52 },
  { icon: Scissors, name: 'Cabeleireiro', color: 'from-pink-400 to-rose-400', count: 67 },
  { icon: Palette, name: 'Manicure', color: 'from-purple-400 to-pink-400', count: 43 },
  { icon: Wrench, name: 'Mecânico', color: 'from-red-500 to-orange-500', count: 29 },
];

export function ClientHomeScreen({ onNavigate }: ClientHomeScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-20">
      {/* Header with gradient */}
      <div className="bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] px-6 pt-12 pb-8 rounded-b-[32px]">
        <div className="flex items-center justify-between mb-6">
          <div>
            <p className="text-blue-100 mb-1">Olá,</p>
            <h2 className="text-white">Maria Silva</h2>
          </div>
          <button className="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center">
            <User className="w-6 h-6 text-white" />
          </button>
        </div>

        {/* Location */}
        <div className="flex items-center gap-2 text-white mb-6">
          <MapPin className="w-5 h-5" />
          <span>São Paulo, SP - Centro</span>
        </div>

        {/* Search Bar */}
        <div className="relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-[#64748B]" />
          <input
            type="text"
            placeholder="Buscar serviços ou profissionais..."
            className="w-full pl-12 pr-4 py-4 rounded-2xl bg-white shadow-lg focus:outline-none"
          />
        </div>
      </div>

      {/* Categories */}
      <div className="px-6 pt-6">
        <h3 className="mb-4">Categorias</h3>
        <div className="grid grid-cols-2 gap-4 mb-6">
          {categories.map((category, index) => {
            const Icon = category.icon;
            return (
              <button
                key={index}
                onClick={() => onNavigate('professionalsList')}
                className="bg-white rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow border border-gray-100"
              >
                <div className={`w-12 h-12 rounded-xl bg-gradient-to-br ${category.color} flex items-center justify-center mb-3`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>
                <h4 className="mb-1">{category.name}</h4>
                <p className="text-[#64748B]">{category.count} profissionais</p>
              </button>
            );
          })}
        </div>

        {/* Recent Services */}
        <div className="mb-6">
          <h3 className="mb-4">Profissionais próximos</h3>
          <div className="space-y-3">
            {[1, 2, 3].map((item) => (
              <button
                key={item}
                onClick={() => onNavigate('professionalProfile')}
                className="w-full bg-white rounded-2xl p-4 shadow-sm hover:shadow-md transition-shadow border border-gray-100"
              >
                <div className="flex items-center gap-4">
                  <img
                    src="https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBlbGVjdHJpY2lhbiUyMHBvcnRyYWl0fGVufDF8fHx8MTc2NzQ4ODA4OXww&ixlib=rb-4.1.0&q=80&w=1080"
                    alt="Profissional"
                    className="w-16 h-16 rounded-xl object-cover"
                  />
                  <div className="flex-1 text-left">
                    <h4 className="mb-1">João Santos</h4>
                    <p className="text-[#64748B] mb-1">Eletricista</p>
                    <div className="flex items-center gap-1">
                      <span className="text-[#FACC15]">★</span>
                      <span className="text-[#64748B]">4.8 (127 avaliações)</span>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-[#22C55E] mb-1">●</div>
                    <p className="text-[#64748B]">0.8 km</p>
                  </div>
                </div>
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Bottom Navigation */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-6 py-4">
        <div className="flex items-center justify-around max-w-md mx-auto">
          <button className="flex flex-col items-center gap-1">
            <HomeIcon className="w-6 h-6 text-[#3B82F6]" />
            <span className="text-xs text-[#3B82F6]">Início</span>
          </button>
          <button className="flex flex-col items-center gap-1" onClick={() => onNavigate('categories')}>
            <Search className="w-6 h-6 text-[#64748B]" />
            <span className="text-xs text-[#64748B]">Buscar</span>
          </button>
          <button className="flex flex-col items-center gap-1">
            <Wrench className="w-6 h-6 text-[#64748B]" />
            <span className="text-xs text-[#64748B]">Serviços</span>
          </button>
          <button className="flex flex-col items-center gap-1">
            <User className="w-6 h-6 text-[#64748B]" />
            <span className="text-xs text-[#64748B]">Perfil</span>
          </button>
        </div>
      </div>
    </div>
  );
}
