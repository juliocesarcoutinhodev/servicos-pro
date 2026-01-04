import { User, Briefcase } from 'lucide-react';

interface LoginScreenProps {
  onNavigate: (screen: string) => void;
}

export function LoginScreen({ onNavigate }: LoginScreenProps) {
  return (
    <div className="min-h-screen bg-gradient-to-b from-[#1E40AF] to-[#3B82F6] flex flex-col">
      {/* Header */}
      <div className="px-6 pt-16 pb-8">
        <h1 className="text-white text-center mb-2">ServiçoPro</h1>
        <p className="text-blue-100 text-center">Profissionais qualificados na sua região</p>
      </div>

      {/* Content */}
      <div className="flex-1 bg-[#F8FAFC] rounded-t-[32px] px-6 pt-10">
        <h2 className="text-center mb-2">Bem-vindo!</h2>
        <p className="text-[#64748B] text-center mb-8">Escolha como deseja continuar</p>

        {/* User Type Cards */}
        <div className="space-y-4 mb-8">
          <button
            onClick={() => onNavigate('clientHome')}
            className="w-full bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:border-[#3B82F6] transition-all"
          >
            <div className="flex items-center gap-4">
              <div className="w-14 h-14 rounded-full bg-gradient-to-br from-[#3B82F6] to-[#2563EB] flex items-center justify-center">
                <User className="w-7 h-7 text-white" />
              </div>
              <div className="flex-1 text-left">
                <h3 className="mb-1">Sou Cliente</h3>
                <p className="text-[#64748B]">Preciso contratar um serviço</p>
              </div>
            </div>
          </button>

          <button
            onClick={() => onNavigate('providerHome')}
            className="w-full bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:border-[#3B82F6] transition-all"
          >
            <div className="flex items-center gap-4">
              <div className="w-14 h-14 rounded-full bg-gradient-to-br from-[#FB923C] to-[#F97316] flex items-center justify-center">
                <Briefcase className="w-7 h-7 text-white" />
              </div>
              <div className="flex-1 text-left">
                <h3 className="mb-1">Sou Prestador</h3>
                <p className="text-[#64748B]">Quero oferecer meus serviços</p>
              </div>
            </div>
          </button>
        </div>

        {/* Input Fields */}
        <div className="space-y-4 mb-6">
          <div>
            <label className="block text-[#64748B] mb-2">Email ou telefone</label>
            <input
              type="text"
              placeholder="Digite seu email ou telefone"
              className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none transition-colors"
            />
          </div>
          <div>
            <label className="block text-[#64748B] mb-2">Senha</label>
            <input
              type="password"
              placeholder="Digite sua senha"
              className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B82F6] focus:outline-none transition-colors"
            />
          </div>
        </div>

        <button className="w-full bg-[#3B82F6] text-white py-4 rounded-xl hover:bg-[#2563EB] transition-colors mb-4">
          Entrar
        </button>

        <button
          onClick={() => onNavigate('signup')}
          className="w-full text-[#3B82F6] text-center"
        >
          Não tem conta? <span className="font-medium">Cadastre-se</span>
        </button>
      </div>
    </div>
  );
}
