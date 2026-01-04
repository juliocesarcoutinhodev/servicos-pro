import { ChevronLeft, Search, Zap, Wrench, Home as HomeIcon, Scissors, Palette, Car, Droplet, Hammer, PaintBucket, Laptop } from 'lucide-react';

interface CategoriesScreenProps {
  onNavigate: (screen: string) => void;
}

const allCategories = [
  { icon: Zap, name: 'Eletricista', color: 'from-yellow-400 to-orange-400', count: 45, desc: 'Instalações e reparos elétricos' },
  { icon: Wrench, name: 'Encanador', color: 'from-blue-400 to-cyan-400', count: 38, desc: 'Hidráulica e vazamentos' },
  { icon: HomeIcon, name: 'Pedreiro', color: 'from-gray-500 to-gray-600', count: 52, desc: 'Construção e reformas' },
  { icon: Scissors, name: 'Cabeleireiro', color: 'from-pink-400 to-rose-400', count: 67, desc: 'Cortes e tratamentos capilares' },
  { icon: Palette, name: 'Manicure', color: 'from-purple-400 to-pink-400', count: 43, desc: 'Unhas e cuidados' },
  { icon: Car, name: 'Mecânico', color: 'from-red-500 to-orange-500', count: 29, desc: 'Manutenção automotiva' },
  { icon: Droplet, name: 'Pintor', color: 'from-blue-500 to-indigo-500', count: 34, desc: 'Pintura residencial e comercial' },
  { icon: Hammer, name: 'Marceneiro', color: 'from-amber-600 to-orange-600', count: 21, desc: 'Móveis sob medida' },
  { icon: PaintBucket, name: 'Jardineiro', color: 'from-green-500 to-emerald-500', count: 18, desc: 'Paisagismo e manutenção' },
  { icon: Laptop, name: 'Técnico em TI', color: 'from-indigo-500 to-purple-500', count: 56, desc: 'Suporte e manutenção' },
];

export function CategoriesScreen({ onNavigate }: CategoriesScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-6">
      {/* Header */}
      <div className="bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] px-6 pt-12 pb-6">
        <button
          onClick={() => onNavigate('clientHome')}
          className="text-white mb-6 flex items-center"
        >
          <ChevronLeft className="w-6 h-6" />
          <span className="ml-2">Voltar</span>
        </button>
        <h1 className="text-white mb-2">Categorias</h1>
        <p className="text-blue-100">Escolha o serviço que você precisa</p>

        {/* Search Bar */}
        <div className="relative mt-6">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-[#64748B]" />
          <input
            type="text"
            placeholder="Buscar categoria..."
            className="w-full pl-12 pr-4 py-3 rounded-xl bg-white shadow-lg focus:outline-none"
          />
        </div>
      </div>

      {/* Categories Grid */}
      <div className="px-6 pt-6">
        <div className="grid grid-cols-1 gap-4">
          {allCategories.map((category, index) => {
            const Icon = category.icon;
            return (
              <button
                key={index}
                onClick={() => onNavigate('professionalsList')}
                className="bg-white rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow border border-gray-100"
              >
                <div className="flex items-center gap-4">
                  <div className={`w-14 h-14 rounded-xl bg-gradient-to-br ${category.color} flex items-center justify-center flex-shrink-0`}>
                    <Icon className="w-7 h-7 text-white" />
                  </div>
                  <div className="flex-1 text-left">
                    <h3 className="mb-1">{category.name}</h3>
                    <p className="text-[#64748B] mb-1">{category.desc}</p>
                    <p className="text-[#3B82F6]">{category.count} profissionais disponíveis</p>
                  </div>
                </div>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}
