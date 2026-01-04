import { ChevronLeft, CreditCard, Shield, CheckCircle2 } from 'lucide-react';

interface PaymentScreenProps {
  onNavigate: (screen: string) => void;
}

export function PaymentScreen({ onNavigate }: PaymentScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-6">
      {/* Header */}
      <div className="bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] px-6 pt-12 pb-8">
        <button
          onClick={() => onNavigate('serviceRequest')}
          className="text-white mb-6 flex items-center"
        >
          <ChevronLeft className="w-6 h-6" />
          <span className="ml-2">Voltar</span>
        </button>
        <h1 className="text-white mb-2">Pagamento</h1>
        <p className="text-blue-100">Escolha a forma de pagamento</p>
      </div>

      {/* Security Badge */}
      <div className="px-6 -mt-4">
        <div className="bg-white rounded-2xl p-4 shadow-lg border border-gray-100">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-xl bg-[#22C55E]/10 flex items-center justify-center flex-shrink-0">
              <Shield className="w-6 h-6 text-[#22C55E]" />
            </div>
            <div className="flex-1">
              <h3 className="mb-1">Pagamento Seguro</h3>
              <p className="text-[#64748B]">Seus dados estão protegidos</p>
            </div>
          </div>
        </div>
      </div>

      {/* Payment Methods */}
      <div className="px-6 mt-6">
        <h3 className="mb-4">Forma de pagamento</h3>
        <div className="space-y-3">
          <button className="w-full bg-white rounded-2xl p-5 shadow-sm border-2 border-[#3B82F6] hover:shadow-md transition-shadow">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#3B82F6] to-[#2563EB] flex items-center justify-center">
                <CreditCard className="w-6 h-6 text-white" />
              </div>
              <div className="flex-1 text-left">
                <h4 className="mb-1">Cartão de Crédito</h4>
                <p className="text-[#64748B]">Parcelamento em até 12x</p>
              </div>
              <div className="w-6 h-6 rounded-full border-2 border-[#3B82F6] flex items-center justify-center">
                <div className="w-3 h-3 rounded-full bg-[#3B82F6]"></div>
              </div>
            </div>
          </button>

          <button className="w-full bg-white rounded-2xl p-5 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#FB923C] to-[#F97316] flex items-center justify-center">
                <CreditCard className="w-6 h-6 text-white" />
              </div>
              <div className="flex-1 text-left">
                <h4 className="mb-1">Cartão de Débito</h4>
                <p className="text-[#64748B]">Pagamento à vista</p>
              </div>
              <div className="w-6 h-6 rounded-full border-2 border-gray-300"></div>
            </div>
          </button>

          <button className="w-full bg-white rounded-2xl p-5 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#22C55E] to-[#16A34A] flex items-center justify-center">
                <span className="text-white">PIX</span>
              </div>
              <div className="flex-1 text-left">
                <h4 className="mb-1">PIX</h4>
                <p className="text-[#64748B]">Aprovação imediata</p>
              </div>
              <div className="w-6 h-6 rounded-full border-2 border-gray-300"></div>
            </div>
          </button>

          <button className="w-full bg-white rounded-2xl p-5 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#64748B] to-[#475569] flex items-center justify-center">
                <span className="text-white">💰</span>
              </div>
              <div className="flex-1 text-left">
                <h4 className="mb-1">Dinheiro</h4>
                <p className="text-[#64748B]">Pagar na hora do serviço</p>
              </div>
              <div className="w-6 h-6 rounded-full border-2 border-gray-300"></div>
            </div>
          </button>
        </div>
      </div>

      {/* Card Form */}
      <div className="px-6 mt-6">
        <h3 className="mb-4">Dados do cartão</h3>
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 space-y-4">
          <div>
            <label className="block text-[#64748B] mb-2">Número do cartão</label>
            <input
              type="text"
              placeholder="0000 0000 0000 0000"
              className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none"
            />
          </div>
          <div>
            <label className="block text-[#64748B] mb-2">Nome no cartão</label>
            <input
              type="text"
              placeholder="Nome como está no cartão"
              className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[#64748B] mb-2">Validade</label>
              <input
                type="text"
                placeholder="MM/AA"
                className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-[#64748B] mb-2">CVV</label>
              <input
                type="text"
                placeholder="000"
                className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Order Summary */}
      <div className="px-6 mt-6">
        <h3 className="mb-4">Resumo do pedido</h3>
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <div className="space-y-3">
            <div className="flex justify-between">
              <span className="text-[#64748B]">Profissional</span>
              <span>João Santos</span>
            </div>
            <div className="flex justify-between">
              <span className="text-[#64748B]">Serviço</span>
              <span>Troca de disjuntores</span>
            </div>
            <div className="flex justify-between">
              <span className="text-[#64748B]">Data e hora</span>
              <span>05/01/2026 - 14:00</span>
            </div>
            <div className="border-t border-gray-100 pt-3">
              <div className="flex justify-between mb-2">
                <span className="text-[#64748B]">Valor do serviço</span>
                <span>R$ 80,00</span>
              </div>
              <div className="flex justify-between mb-3">
                <span className="text-[#64748B]">Taxa de serviço</span>
                <span>R$ 5,00</span>
              </div>
              <div className="flex justify-between">
                <span>Total</span>
                <span className="text-[#3B82F6]">R$ 85,00</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Button */}
      <div className="px-6 mt-6">
        <button
          onClick={() => onNavigate('clientHome')}
          className="w-full bg-[#3B82F6] text-white py-4 rounded-xl hover:bg-[#2563EB] transition-colors flex items-center justify-center gap-2"
        >
          <CheckCircle2 className="w-5 h-5" />
          <span>Confirmar Pagamento</span>
        </button>
      </div>
    </div>
  );
}
