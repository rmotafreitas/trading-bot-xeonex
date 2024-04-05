import { Route, BrowserRouter as Router, Routes } from "react-router-dom";

import { HomePage } from "./pages";
import { LoginPage } from "./pages/Login";
import { ProfilePage } from "./pages/me/Profile";
import { ThemeProvider } from "./components/theme-provider";
import { NotFoundPage } from "./pages/NotFound";
import { AuthProvider } from "./lib/hooks/useAuth";

export function App() {
  return (
    <AuthProvider>
      <ThemeProvider defaultTheme="white" storageKey="vite-ui-theme">
        <Router>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/auth" element={<LoginPage />} />
            <Route path="/me" element={<ProfilePage />} />
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </Router>
      </ThemeProvider>
    </AuthProvider>
  );
}
