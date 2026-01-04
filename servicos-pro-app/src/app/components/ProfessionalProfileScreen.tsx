import { ChevronLeft, Star, MapPin, Phone, MessageCircle, Shield, Award, Clock } from 'lucide-react';

interface ProfessionalProfileScreenProps {
  onNavigate: (screen: string) => void;
}

const services = [
  { name: 'Instalação de tomadas', price: 'R$ 50' },
  { name: 'Troca de disjuntores', price: 'R$ 80' },
  { name: 'Instalação de chuveiro', price: 'R$ 120' },
  { name: 'Reparo de fiação', price: 'R$ 150' },
];

const reviews = [
  { name: 'Ana Paula', rating: 5, comment: 'Excelente profissional! Muito pontual e cuidadoso.', date: '2 dias atrás' },
  { name: 'Roberto Costa', rating: 5, comment: 'Resolveu o problema rapidamente. Recomendo!', date: '1 semana atrás' },
  { name: 'Mariana Silva', rating: 4, comment: 'Bom trabalho, preço justo.', date: '2 semanas atrás' },
];

export function ProfessionalProfileScreen({ onNavigate }: ProfessionalProfileScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-24">
      {/* Header Image */}
      <div className="relative">
        <img
          src="https://images.unsplash.com/photo-1601462904263-f2fa0c851cb9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBlbGVjdHJpY2lhbiUyMHBvcnRyYWl0fGVufDF8fHx8MTc2NzQ4ODA4OXww&ixlib=rb-4.1.0&q=80&w=1080"
          alt="Professional"
          className="w-full h-64 object-cover"
        />
        <button
          onClick={() => onNavigate('professionalsList')}
          className="absolute top-12 left-6 w-10 h-10 rounded-full bg-white/90 backdrop-blur-sm flex items-center justify-center shadow-lg"
        >
          <ChevronLeft className="w-6 h-6 text-gray-800" />
        </button>
      </div>

      {/* Profile Info */}
      <div className="px-6 -mt-8">
        <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
          <div className="flex items-start justify-between mb-4">
            <div className="flex-1">
              <h1 className="mb-1">João Santos</h1>
              <p className="text-[#64748B] mb-2">Eletricista</p>
              <div className="flex items-center gap-4 flex-wrap">
                <div className="flex items-center gap-1">
                  <Star className="w-5 h-5 fill-[#FACC15] text-[#FACC15]" />
                  <span className="font-medium">4.8</span>
                  <span className="text-[#64748B]">(127 avaliações)</span>
                </div>
              </div>
            </div>
            <div className="flex items-center gap-1 text-[#22C55E]">
              <div className="w-3 h-3 rounded-full bg-[#22C55E]"></div>
              <span>Online</span>
            </div>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-3 gap-4 pt-4 border-t border-gray-100">
            <div className="text-center">
              <div className="flex items-center justify-center w-10 h-10 rounded-full bg-[#3B82F6]/10 mx-auto mb-2">
                <Award className="w-5 h-5 text-[#3B82F6]" />
              </div>
              <p className="text-[#64748B]">312</p>
              <p className="text-[#64748B]">Serviços</p>
            </div>
            <div className="text-center">
              <div className="flex items-center justify-center w-10 h-10 rounded-full bg-[#22C55E]/10 mx-auto mb-2">
                <Shield className="w-5 h-5 text-[#22C55E]" />
              </div>
              <p className="text-[#64748B]">98%</p>
              <p className="text-[#64748B]">Aprovação</p>
            </div>
            <div className="text-center">
              <div className="flex items-center justify-center w-10 h-10 rounded-full bg-[#FB923C]/10 mx-auto mb-2">
                <Clock className="w-5 h-5 text-[#FB923C]" />
              </div>
              <p className="text-[#64748B]">5 anos</p>
              <p className="text-[#64748B]">Experiência</p>
            </div>
          </div>
        </div>
      </div>

      {/* About */}
      <div className="px-6 mt-4">
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="mb-3">Sobre</h3>
          <p className="text-[#64748B] leading-relaxed">
            Eletricista profissional com 5 anos de experiência. Especializado em instalações residenciais e comerciais. Atendimento rápido e garantia de qualidade.
          </p>
        </div>
      </div>

      {/* Location */}
      <div className="px-6 mt-4">
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <div className="flex items-center gap-3 mb-2">
            <MapPin className="w-5 h-5 text-[#3B82F6]" />
            <h3>Localização</h3>
          </div>
          <p className="text-[#64748B]">Centro, São Paulo - SP</p>
          <p className="text-[#64748B]">0.8 km de distância</p>
        </div>
      </div>

      {/* Services */}
      <div className="px-6 mt-4">
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="mb-4">Serviços oferecidos</h3>
          <div className="space-y-3">
            {services.map((service, index) => (
              <div key={index} className="flex items-center justify-between py-3 border-b border-gray-100 last:border-0">
                <span className="text-[#64748B]">{service.name}</span>
                <span className="text-[#3B82F6]">{service.price}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Reviews */}
      <div className="px-6 mt-4">
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="mb-4">Avaliações (127)</h3>
          <div className="space-y-4">
            {reviews.map((review, index) => (
              <div key={index} className="pb-4 border-b border-gray-100 last:border-0">
                <div className="flex items-center justify-between mb-2">
                  <span>{review.name}</span>
                  <div className="flex items-center gap-1">
                    {[...Array(review.rating)].map((_, i) => (
                      <Star key={i} className="w-4 h-4 fill-[#FACC15] text-[#FACC15]" />
                    ))}
                  </div>
                </div>
                <p className="text-[#64748B] mb-1">{review.comment}</p>
                <p className="text-[#64748B]">{review.date}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-6 py-4">
        <div className="flex gap-3 max-w-md mx-auto">
          <button className="w-12 h-12 rounded-xl border-2 border-[#3B82F6] flex items-center justify-center flex-shrink-0">
            <Phone className="w-5 h-5 text-[#3B82F6]" />
          </button>
          <button className="w-12 h-12 rounded-xl border-2 border-[#3B82F6] flex items-center justify-center flex-shrink-0">
            <MessageCircle className="w-5 h-5 text-[#3B82F6]" />
          </button>
          <button
            onClick={() => onNavigate('serviceRequest')}
            className="flex-1 bg-[#3B82F6] text-white py-3 rounded-xl hover:bg-[#2563EB] transition-colors"
          >
            Solicitar Serviço
          </button>
        </div>
      </div>
    </div>
  );
}
