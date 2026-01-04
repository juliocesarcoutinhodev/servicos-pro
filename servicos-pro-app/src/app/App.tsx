import { useState } from 'react';
import { LoginScreen } from './components/LoginScreen';
import { SignupScreen } from './components/SignupScreen';
import { ClientHomeScreen } from './components/ClientHomeScreen';
import { CategoriesScreen } from './components/CategoriesScreen';
import { ProfessionalsListScreen } from './components/ProfessionalsListScreen';
import { ProfessionalProfileScreen } from './components/ProfessionalProfileScreen';
import { ServiceRequestScreen } from './components/ServiceRequestScreen';
import { PaymentScreen } from './components/PaymentScreen';
import { ProviderHomeScreen } from './components/ProviderHomeScreen';
import { ProviderRequestsScreen } from './components/ProviderRequestsScreen';
import { ProviderProfileScreen } from './components/ProviderProfileScreen';

type Screen = 
  | 'login' 
  | 'signup' 
  | 'clientHome' 
  | 'categories' 
  | 'professionalsList'
  | 'professionalProfile'
  | 'serviceRequest'
  | 'payment'
  | 'providerHome'
  | 'providerRequests'
  | 'providerProfile';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<Screen>('login');

  const handleNavigate = (screen: Screen) => {
    setCurrentScreen(screen);
  };

  return (
    <div className="min-h-screen bg-[#F8FAFC] max-w-md mx-auto relative">
      {currentScreen === 'login' && <LoginScreen onNavigate={handleNavigate} />}
      {currentScreen === 'signup' && <SignupScreen onNavigate={handleNavigate} />}
      {currentScreen === 'clientHome' && <ClientHomeScreen onNavigate={handleNavigate} />}
      {currentScreen === 'categories' && <CategoriesScreen onNavigate={handleNavigate} />}
      {currentScreen === 'professionalsList' && <ProfessionalsListScreen onNavigate={handleNavigate} />}
      {currentScreen === 'professionalProfile' && <ProfessionalProfileScreen onNavigate={handleNavigate} />}
      {currentScreen === 'serviceRequest' && <ServiceRequestScreen onNavigate={handleNavigate} />}
      {currentScreen === 'payment' && <PaymentScreen onNavigate={handleNavigate} />}
      {currentScreen === 'providerHome' && <ProviderHomeScreen onNavigate={handleNavigate} />}
      {currentScreen === 'providerRequests' && <ProviderRequestsScreen onNavigate={handleNavigate} />}
      {currentScreen === 'providerProfile' && <ProviderProfileScreen onNavigate={handleNavigate} />}
    </div>
  );
}
