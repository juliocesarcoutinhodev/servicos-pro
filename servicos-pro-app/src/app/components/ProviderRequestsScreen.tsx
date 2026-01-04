import { ChevronLeft, Calendar, MapPin, Clock } from 'lucide-react';

interface ProviderRequestsScreenProps {
  onNavigate: (screen: string) => void;
}

const allRequests = [
  {
    client: 'Maria Silva',
    service: 'Troca de disjuntores',
    date: 'Hoje, 14:00',
    address: 'Centro, São Paulo - SP',
    distance: '0.8 km',
    price: 'R$ 80',
    status: 'pending',
    description: 'Preciso trocar o disjuntor da cozinha que está desarmando constantemente.',
  },
  {
    client: 'Carlos Mendes',
    service: 'Instalação de tomadas',
    date: 'Amanhã, 10:00',
    address: 'Jardins, São Paulo - SP',
    distance: '2.3 km',
    price: 'R$ 50',
    status: 'pending',
    description: 'Instalar 3 tomadas na sala de estar.',
  },
  {
    client: 'Ana Paula',
    service: 'Instalação de chuveiro',
    date: 'Amanhã, 16:00',
    address: 'Vila Mariana, São Paulo - SP',
    distance: '1.5 km',
    price: 'R$ 120',
    status: 'pending',
    description: 'Instalação de chuveiro elétrico de 220V.',
  },
  {
    client: 'Roberto Costa',
    service: 'Reparo de fiação',
    date: '07/01, 09:00',
    address: 'Pinheiros, São Paulo - SP',
    distance: '3.2 km',
    price: 'R$ 150',
    status: 'pending',
    description: 'Reparo de fiação aparente na garagem.',
  },
];

export function ProviderRequestsScreen({ onNavigate }: ProviderRequestsScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-6">
      {/* Header */}
      <div className="bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] px-6 pt-12 pb-8">
        <button
          onClick={() => onNavigate('providerHome')}
          className="text-white mb-6 flex items-center"
        >
          <ChevronLeft className="w-6 h-6" />
          <span className="ml-2">Voltar</span>
        </button>
        <h1 className="text-white mb-2">Solicitações</h1>
        <p className="text-blue-100">Gerencie suas solicitações de serviços</p>
      </div>

      {/* Filter Tabs */}
      <div className="px-6 pt-4 pb-2">
        <div className="flex items-center gap-3 overflow-x-auto pb-2">
          <button className="px-4 py-2 rounded-full bg-[#3B82F6] text-white whitespace-nowrap shadow-sm">
            Pendentes (4)
          </button>
          <button className="px-4 py-2 rounded-full bg-white text-[#64748B] border border-gray-200 whitespace-nowrap">
            Aceitas (8)
          </button>
          <button className="px-4 py-2 rounded-full bg-white text-[#64748B] border border-gray-200 whitespace-nowrap">
            Concluídas (32)
          </button>
        </div>
      </div>

      {/* Requests List */}
      <div className="px-6 mt-4 space-y-4">
        {allRequests.map((request, index) => (
          <div key={index} className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
            <div className="flex items-start justify-between mb-3">
              <div className="flex-1">
                <h3 className="mb-1">{request.client}</h3>
                <p className="text-[#64748B]">{request.service}</p>
              </div>
              <div className="text-right ml-4">
                <p className="text-[#3B82F6]">{request.price}</p>
              </div>
            </div>

            <div className="bg-[#F8FAFC] rounded-xl p-3 mb-3">
              <p className="text-[#64748B]">{request.description}</p>
            </div>

            <div className="space-y-2 mb-4">
              <div className="flex items-center gap-2 text-[#64748B]">
                <Calendar className="w-4 h-4" />
                <span>{request.date}</span>
              </div>
              <div className="flex items-center gap-2 text-[#64748B]">
                <MapPin className="w-4 h-4" />
                <span>{request.address} • {request.distance}</span>
              </div>
            </div>

            {request.status === 'pending' && (
              <div className="flex gap-3">
                <button className="flex-1 bg-[#3B82F6] text-white py-3 rounded-xl hover:bg-[#2563EB] transition-colors">
                  Aceitar
                </button>
                <button className="flex-1 border-2 border-gray-200 text-[#64748B] py-3 rounded-xl hover:border-gray-300 transition-colors">
                  Recusar
                </button>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
