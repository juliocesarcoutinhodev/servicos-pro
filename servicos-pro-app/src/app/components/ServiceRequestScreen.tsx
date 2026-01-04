import { ChevronLeft, Calendar, Clock, MapPin, FileText } from 'lucide-react';

interface ServiceRequestScreenProps {
  onNavigate: (screen: string) => void;
}

export function ServiceRequestScreen({ onNavigate }: ServiceRequestScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-24">
      {/* Header */}
      <div className="bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] px-6 pt-12 pb-8">
        <button
          onClick={() => onNavigate('professionalProfile')}
          className="text-white mb-6 flex items-center"
        >
          <ChevronLeft className="w-6 h-6" />
          <span className="ml-2">Voltar</span>
        </button>
        <h1 className="text-white mb-2">Solicitar Serviço</h1>
        <p className="text-blue-100">Preencha os detalhes da sua solicitação</p>
      </div>

      {/* Professional Card */}
      <div className="px-6 -mt-4">
        <div className="bg-white rounded-2xl p-4 shadow-lg border border-gray-100">
          <div className="flex items-center gap-4">
            <img
              src="https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBlbGVjdHJpY2lhbiUyMHBvcnRyYWl0fGVufDF8fHx8MTc2NzQ4ODA4OXww&ixlib=rb-4.1.0&q=80&w=1080"
              alt="Professional"
              className="w-16 h-16 rounded-xl object-cover"
            />
            <div className="flex-1">
              <h3 className="mb-1">João Santos</h3>
              <p className="text-[#64748B]">Eletricista</p>
            </div>
          </div>
        </div>
      </div>

      {/* Form */}
      <div className="px-6 mt-6 space-y-4">
        {/* Service Selection */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <label className="flex items-center gap-3 mb-3">
            <FileText className="w-5 h-5 text-[#3B82F6]" />
            <span>Serviço</span>
          </label>
          <select className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none bg-white">
            <option>Selecione o serviço</option>
            <option>Instalação de tomadas - R$ 50</option>
            <option>Troca de disjuntores - R$ 80</option>
            <option>Instalação de chuveiro - R$ 120</option>
            <option>Reparo de fiação - R$ 150</option>
          </select>
        </div>

        {/* Date */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <label className="flex items-center gap-3 mb-3">
            <Calendar className="w-5 h-5 text-[#3B82F6]" />
            <span>Data</span>
          </label>
          <input
            type="date"
            className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none"
          />
        </div>

        {/* Time */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <label className="flex items-center gap-3 mb-3">
            <Clock className="w-5 h-5 text-[#3B82F6]" />
            <span>Horário</span>
          </label>
          <div className="grid grid-cols-3 gap-3">
            {['08:00', '10:00', '14:00', '16:00', '18:00', '20:00'].map((time) => (
              <button
                key={time}
                className="px-4 py-3 rounded-xl border border-gray-200 hover:border-[#3B82F6] hover:bg-[#3B82F6] hover:text-white transition-colors"
              >
                {time}
              </button>
            ))}
          </div>
        </div>

        {/* Address */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <label className="flex items-center gap-3 mb-3">
            <MapPin className="w-5 h-5 text-[#3B82F6]" />
            <span>Endereço</span>
          </label>
          <input
            type="text"
            placeholder="Rua, número e complemento"
            className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none mb-3"
          />
          <div className="grid grid-cols-2 gap-3">
            <input
              type="text"
              placeholder="Bairro"
              className="px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none"
            />
            <input
              type="text"
              placeholder="CEP"
              className="px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none"
            />
          </div>
        </div>

        {/* Description */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <label className="block mb-3">Descrição do problema</label>
          <textarea
            placeholder="Descreva em detalhes o serviço que você precisa..."
            rows={4}
            className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none resize-none"
          ></textarea>
        </div>

        {/* Price Summary */}
        <div className="bg-gradient-to-br from-[#3B82F6]/10 to-[#2563EB]/10 rounded-2xl p-5 border border-[#3B82F6]/20">
          <div className="flex items-center justify-between mb-2">
            <span className="text-[#64748B]">Valor do serviço</span>
            <span>R$ 80,00</span>
          </div>
          <div className="flex items-center justify-between mb-2">
            <span className="text-[#64748B]">Taxa de serviço</span>
            <span>R$ 5,00</span>
          </div>
          <div className="border-t border-[#3B82F6]/20 pt-2 mt-2">
            <div className="flex items-center justify-between">
              <span>Total</span>
              <span className="text-[#3B82F6]">R$ 85,00</span>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-6 py-4">
        <button
          onClick={() => onNavigate('payment')}
          className="w-full bg-[#3B82F6] text-white py-4 rounded-xl hover:bg-[#2563EB] transition-colors max-w-md mx-auto block"
        >
          Continuar para Pagamento
        </button>
      </div>
    </div>
  );
}
