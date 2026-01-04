import { ChevronLeft, Star, Award, Shield, Clock, Edit, MapPin, Phone, Mail } from 'lucide-react';

interface ProviderProfileScreenProps {
  onNavigate: (screen: string) => void;
}

const services = [
  { name: 'Instalação de tomadas', price: 'R$ 50', active: true },
  { name: 'Troca de disjuntores', price: 'R$ 80', active: true },
  { name: 'Instalação de chuveiro', price: 'R$ 120', active: true },
  { name: 'Reparo de fiação', price: 'R$ 150', active: true },
  { name: 'Instalação de ventilador', price: 'R$ 70', active: false },
];

export function ProviderProfileScreen({ onNavigate }: ProviderProfileScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-6">
      {/* Header Image */}
      <div className="relative">
        <div className="bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] h-48"></div>
        <button
          onClick={() => onNavigate('providerHome')}
          className="absolute top-12 left-6 w-10 h-10 rounded-full bg-white/90 backdrop-blur-sm flex items-center justify-center shadow-lg"
        >
          <ChevronLeft className="w-6 h-6 text-gray-800" />
        </button>
        <button className="absolute top-12 right-6 w-10 h-10 rounded-full bg-white/90 backdrop-blur-sm flex items-center justify-center shadow-lg">
          <Edit className="w-5 h-5 text-gray-800" />
        </button>

        {/* Profile Image */}
        <div className="absolute -bottom-16 left-6">
          <img
            src="https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBlbGVjdHJpY2lhbiUyMHBvcnRyYWl0fGVufDF8fHx8MTc2NzQ4ODA4OXww&ixlib=rb-4.1.0&q=80&w=1080"
            alt="Profile"
            className="w-32 h-32 rounded-2xl object-cover border-4 border-white shadow-lg"
          />
        </div>
      </div>

      {/* Profile Info */}
      <div className="px-6 mt-20">
        <div className="mb-6">
          <h1 className="mb-1">João Santos</h1>
          <p className="text-[#64748B] mb-3">Eletricista Profissional</p>
          <div className="flex items-center gap-4 flex-wrap">
            <div className="flex items-center gap-1">
              <Star className="w-5 h-5 fill-[#FACC15] text-[#FACC15]" />
              <span className="font-medium">4.8</span>
              <span className="text-[#64748B]">(127 avaliações)</span>
            </div>
            <div className="flex items-center gap-1 text-[#22C55E]">
              <div className="w-3 h-3 rounded-full bg-[#22C55E]"></div>
              <span>Disponível</span>
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-4 mb-6">
          <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 text-center">
            <div className="flex items-center justify-center w-10 h-10 rounded-full bg-[#3B82F6]/10 mx-auto mb-2">
              <Award className="w-5 h-5 text-[#3B82F6]" />
            </div>
            <p className="mb-1">312</p>
            <p className="text-[#64748B]">Serviços</p>
          </div>
          <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 text-center">
            <div className="flex items-center justify-center w-10 h-10 rounded-full bg-[#22C55E]/10 mx-auto mb-2">
              <Shield className="w-5 h-5 text-[#22C55E]" />
            </div>
            <p className="mb-1">98%</p>
            <p className="text-[#64748B]">Aprovação</p>
          </div>
          <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 text-center">
            <div className="flex items-center justify-center w-10 h-10 rounded-full bg-[#FB923C]/10 mx-auto mb-2">
              <Clock className="w-5 h-5 text-[#FB923C]" />
            </div>
            <p className="mb-1">5 anos</p>
            <p className="text-[#64748B]">Experiência</p>
          </div>
        </div>

        {/* Contact Info */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 mb-6">
          <h3 className="mb-4">Informações de contato</h3>
          <div className="space-y-3">
            <div className="flex items-center gap-3">
              <Phone className="w-5 h-5 text-[#64748B]" />
              <span className="text-[#64748B]">(11) 99999-9999</span>
            </div>
            <div className="flex items-center gap-3">
              <Mail className="w-5 h-5 text-[#64748B]" />
              <span className="text-[#64748B]">joao.santos@email.com</span>
            </div>
            <div className="flex items-center gap-3">
              <MapPin className="w-5 h-5 text-[#64748B]" />
              <span className="text-[#64748B]">Centro, São Paulo - SP</span>
            </div>
          </div>
        </div>

        {/* About */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 mb-6">
          <h3 className="mb-3">Sobre</h3>
          <p className="text-[#64748B] leading-relaxed">
            Eletricista profissional com 5 anos de experiência. Especializado em instalações residenciais e comerciais. Atendimento rápido e garantia de qualidade em todos os serviços.
          </p>
        </div>

        {/* Services */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h3>Serviços oferecidos</h3>
            <button className="text-[#3B82F6]">Editar</button>
          </div>
          <div className="space-y-3">
            {services.map((service, index) => (
              <div
                key={index}
                className="flex items-center justify-between py-3 border-b border-gray-100 last:border-0"
              >
                <div className="flex items-center gap-3 flex-1">
                  <input
                    type="checkbox"
                    checked={service.active}
                    className="w-5 h-5 accent-[#3B82F6]"
                    readOnly
                  />
                  <span className={service.active ? 'text-gray-900' : 'text-[#64748B]'}>
                    {service.name}
                  </span>
                </div>
                <span className="text-[#3B82F6]">{service.price}</span>
              </div>
            ))}
          </div>
          <button className="w-full mt-4 py-3 rounded-xl border-2 border-[#3B82F6] text-[#3B82F6] hover:bg-[#3B82F6]/5 transition-colors">
            Adicionar Novo Serviço
          </button>
        </div>

        {/* Performance */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <h3 className="mb-4">Desempenho este mês</h3>
          <div className="space-y-4">
            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-[#64748B]">Ganhos</span>
                <span className="text-[#22C55E]">R$ 2.450,00</span>
              </div>
              <div className="w-full bg-gray-100 rounded-full h-2">
                <div className="bg-[#22C55E] h-2 rounded-full" style={{ width: '65%' }}></div>
              </div>
            </div>
            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-[#64748B]">Serviços concluídos</span>
                <span>32 de 40</span>
              </div>
              <div className="w-full bg-gray-100 rounded-full h-2">
                <div className="bg-[#3B82F6] h-2 rounded-full" style={{ width: '80%' }}></div>
              </div>
            </div>
            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-[#64748B]">Taxa de aceitação</span>
                <span>90%</span>
              </div>
              <div className="w-full bg-gray-100 rounded-full h-2">
                <div className="bg-[#FB923C] h-2 rounded-full" style={{ width: '90%' }}></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
