import { ChevronLeft } from 'lucide-react';

interface SignupScreenProps {
  onNavigate: (screen: string) => void;
}

export function SignupScreen({ onNavigate }: SignupScreenProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC]">
      {/* Header */}
      <div className="bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] px-6 pt-12 pb-8">
        <button
          onClick={() => onNavigate('login')}
          className="text-white mb-6 flex items-center"
        >
          <ChevronLeft className="w-6 h-6" />
        </button>
        <h1 className="text-white mb-2">Criar Conta</h1>
        <p className="text-blue-100">Preencha seus dados para começar</p>
      </div>

      {/* Form */}
      <div className="px-6 pt-8 space-y-4">
        <div>
          <label className="block text-[#64748B] mb-2">Nome completo</label>
          <input
            type="text"
            placeholder="Digite seu nome completo"
            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white focus:border-[#3B82F6] focus:outline-none transition-colors"
          />
        </div>

        <div>
          <label className="block text-[#64748B] mb-2">Email</label>
          <input
            type="email"
            placeholder="seu@email.com"
            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white focus:border-[#3B82F6] focus:outline-none transition-colors"
          />
        </div>

        <div>
          <label className="block text-[#64748B] mb-2">Telefone</label>
          <input
            type="tel"
            placeholder="(00) 00000-0000"
            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white focus:border-[#3B82F6] focus:outline-none transition-colors"
          />
        </div>

        <div>
          <label className="block text-[#64748B] mb-2">Senha</label>
          <input
            type="password"
            placeholder="Mínimo 6 caracteres"
            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white focus:border-[#3B82F6] focus:outline-none transition-colors"
          />
        </div>

        <div>
          <label className="block text-[#64748B] mb-2">Confirmar senha</label>
          <input
            type="password"
            placeholder="Digite a senha novamente"
            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white focus:border-[#3B82F6] focus:outline-none transition-colors"
          />
        </div>

        <div className="pt-2">
          <label className="flex items-start gap-3">
            <input type="checkbox" className="mt-1 w-4 h-4 accent-[#3B82F6]" />
            <span className="text-[#64748B] text-sm">
              Aceito os <span className="text-[#3B82F6]">termos de uso</span> e{' '}
              <span className="text-[#3B82F6]">política de privacidade</span>
            </span>
          </label>
        </div>

        <button
          onClick={() => onNavigate('clientHome')}
          className="w-full bg-[#3B82F6] text-white py-4 rounded-xl hover:bg-[#2563EB] transition-colors mt-6"
        >
          Criar Conta
        </button>

        <button
          onClick={() => onNavigate('login')}
          className="w-full text-[#3B82F6] text-center py-2"
        >
          Já tem conta? <span className="font-medium">Entrar</span>
        </button>
      </div>
    </div>
  );
}
