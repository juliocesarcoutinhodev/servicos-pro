import { User, DollarSign, Star, TrendingUp, Calendar, MapPin, Clock, CheckCircle2 } from 'lucide-react';

interface ProviderHomeScreenProps {
  onNavigate: (screen: string) => void;
}

const recentRequests = [
  {
    client: 'Maria Silva',
    service: 'Troca de disjuntores',
    date: 'Hoje, 14:00',
    address: 'Centro, São Paulo',
    price: 'R$ 80',
    status: 'pending',
  },
  {
    client: 'Carlos Mendes',
    service: 'Instalação de tomadas',
    date: 'Amanhã, 10:00',
    address: 'Jardins, São Paulo',
    price: 'R$ 50',
    status: 'pending',
  },
  {
    client: 'Ana Paula',
    service: 'Instalação de chuveiro',
    date: 'Amanhã, 16:00',
    address: 'Vila Mariana, São Paulo',
    price: 'R$ 120',
    status: 'pending',
  },
];

export function ProviderHomeScreen({ onNavigate }: ProviderHomeScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-20">
      {/* Header with gradient */}
      <div className="bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] px-6 pt-12 pb-8 rounded-b-[32px]">
        <div className="flex items-center justify-between mb-6">
          <div>
            <p className="text-blue-100 mb-1">Bem-vindo,</p>
            <h2 className="text-white">João Santos</h2>
          </div>
          <button
            onClick={() => onNavigate('providerProfile')}
            className="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center"
          >
            <User className="w-6 h-6 text-white" />
          </button>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-3 gap-3">
          <div className="bg-white/10 backdrop-blur-sm rounded-xl p-4">
            <DollarSign className="w-6 h-6 text-white mb-2" />
            <p className="text-white mb-1">R$ 2.450</p>
            <p className="text-blue-100 text-xs">Este mês</p>
          </div>
          <div className="bg-white/10 backdrop-blur-sm rounded-xl p-4">
            <Star className="w-6 h-6 text-white mb-2" />
            <p className="text-white mb-1">4.8</p>
            <p className="text-blue-100 text-xs">Avaliação</p>
          </div>
          <div className="bg-white/10 backdrop-blur-sm rounded-xl p-4">
            <TrendingUp className="w-6 h-6 text-white mb-2" />
            <p className="text-white mb-1">32</p>
            <p className="text-blue-100 text-xs">Serviços</p>
          </div>
        </div>
      </div>

      {/* Toggle Available */}
      <div className="px-6 mt-6">
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="mb-1">Status de disponibilidade</h3>
              <p className="text-[#64748B]">Receba solicitações de serviços</p>
            </div>
            <button className="w-14 h-8 rounded-full bg-[#22C55E] relative flex items-center">
              <div className="w-6 h-6 rounded-full bg-white absolute right-1 shadow-sm"></div>
            </button>
          </div>
        </div>
      </div>

      {/* New Requests */}
      <div className="px-6 mt-6">
        <div className="flex items-center justify-between mb-4">
          <h3>Novas Solicitações</h3>
          <button
            onClick={() => onNavigate('providerRequests')}
            className="text-[#3B82F6]"
          >
            Ver todas
          </button>
        </div>

        <div className="space-y-3">
          {recentRequests.map((request, index) => (
            <div key={index} className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
              <div className="flex items-start justify-between mb-3">
                <div>
                  <h4 className="mb-1">{request.client}</h4>
                  <p className="text-[#64748B]">{request.service}</p>
                </div>
                <div className="text-right">
                  <p className="text-[#3B82F6] mb-1">{request.price}</p>
                </div>
              </div>

              <div className="space-y-2 mb-4">
                <div className="flex items-center gap-2 text-[#64748B]">
                  <Calendar className="w-4 h-4" />
                  <span>{request.date}</span>
                </div>
                <div className="flex items-center gap-2 text-[#64748B]">
                  <MapPin className="w-4 h-4" />
                  <span>{request.address}</span>
                </div>
              </div>

              <div className="flex gap-3">
                <button className="flex-1 bg-[#3B82F6] text-white py-3 rounded-xl hover:bg-[#2563EB] transition-colors">
                  Aceitar
                </button>
                <button className="flex-1 border-2 border-gray-200 text-[#64748B] py-3 rounded-xl hover:border-gray-300 transition-colors">
                  Recusar
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Today's Schedule */}
      <div className="px-6 mt-6">
        <h3 className="mb-4">Agenda de Hoje</h3>
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <div className="flex items-center gap-4 mb-4">
            <div className="w-12 h-12 rounded-xl bg-[#22C55E]/10 flex items-center justify-center">
              <CheckCircle2 className="w-6 h-6 text-[#22C55E]" />
            </div>
            <div className="flex-1">
              <h4 className="mb-1">Ana Paula</h4>
              <p className="text-[#64748B]">Instalação de chuveiro</p>
            </div>
          </div>
          <div className="flex items-center gap-4 text-[#64748B]">
            <div className="flex items-center gap-2">
              <Clock className="w-4 h-4" />
              <span>14:00</span>
            </div>
            <div className="flex items-center gap-2">
              <MapPin className="w-4 h-4" />
              <span>Centro</span>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Navigation */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-6 py-4">
        <div className="flex items-center justify-around max-w-md mx-auto">
          <button className="flex flex-col items-center gap-1">
            <Calendar className="w-6 h-6 text-[#3B82F6]" />
            <span className="text-xs text-[#3B82F6]">Início</span>
          </button>
          <button className="flex flex-col items-center gap-1" onClick={() => onNavigate('providerRequests')}>
            <Clock className="w-6 h-6 text-[#64748B]" />
            <span className="text-xs text-[#64748B]">Solicitações</span>
          </button>
          <button className="flex flex-col items-center gap-1">
            <DollarSign className="w-6 h-6 text-[#64748B]" />
            <span className="text-xs text-[#64748B]">Ganhos</span>
          </button>
          <button className="flex flex-col items-center gap-1" onClick={() => onNavigate('providerProfile')}>
            <User className="w-6 h-6 text-[#64748B]" />
            <span className="text-xs text-[#64748B]">Perfil</span>
          </button>
        </div>
      </div>
    </div>
  );
}
