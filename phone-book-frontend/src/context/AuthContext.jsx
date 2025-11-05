import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuth = () => {
      try {
        const userData = localStorage.getItem('userData');
        if (userData) {
          const parsedData = JSON.parse(userData);
          setUser(parsedData);
        }
      } catch (error) {
        console.error('Erreur lors de la récupération des données utilisateur:', error);
        localStorage.removeItem('userData');
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  const login = async (username, password) => {
    try {
      const response = await authAPI.login({ username, password });

      if (response.data?.token) {
        const userData = {
          username: response.data.username || username,
          token: response.data.token,
          refreshToken: response.data.refreshToken,
          expiresIn: response.data.expiresIn,
        };

        localStorage.setItem('userData', JSON.stringify(userData));
        setUser(userData);
        return { success: true, user: userData };
      } else {
        throw new Error('Structure de réponse invalide');
      }
    } catch (error) {
      // Gestion d'erreur détaillée
      let errorMessage = 'Erreur de connexion';
      
      if (error.code === 'ERR_NETWORK') {
        errorMessage = 'Impossible de joindre le serveur';
      } else if (error.response?.status === 401) {
        errorMessage = 'Identifiants incorrects';
      } else if (error.response?.status === 403) {
        errorMessage = 'Accès refusé';
      } else if (error.response?.status === 429) {
        errorMessage = 'Trop de tentatives. Réessayez plus tard';
      } else if (error.response?.status >= 500) {
        errorMessage = 'Erreur serveur, veuillez réessayer';
      } else if (error.message === 'Structure de réponse invalide') {
        errorMessage = 'Réponse du serveur invalide';
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }

      if (process.env.NODE_ENV === 'development') {
        console.error('Erreur de login:', error);
      }
      return { success: false, error: errorMessage };
    }
  };



  const register = async (userData) => {
    try {
      const response = await authAPI.register(userData);
      return { success: true, data: response.data };
    } catch (error) {
      let errorMessage = 'Erreur lors de l\'inscription';
      
      if (error.response) {
        const status = error.response.status;
        if (status === 409) {
          errorMessage = 'Ce nom d\'utilisateur existe déjà';
        } else if (status === 400) {
          errorMessage = error.response.data?.message || 'Données invalides';
        } else if (status >= 500) {
          errorMessage = 'Erreur serveur. Réessayez plus tard';
        } else {
          errorMessage = error.response.data?.error || 
                        error.response.data?.message || 
                        `Erreur ${status}`;
        }
      } else if (error.request) {
        errorMessage = 'Impossible de joindre le serveur. Vérifiez votre connexion';
      } else {
        errorMessage = error.message || 'Erreur inconnue';
      }

      return { success: false, error: errorMessage };
    }
  };

  const refreshToken = async () => {
    try {
      const userData = localStorage.getItem('userData');
      if (!userData) return { success: false };
      
      const user = JSON.parse(userData);
      if (!user.refreshToken) return { success: false };
      
      const response = await authAPI.refresh({ refreshToken: user.refreshToken });
      
      if (response.data && response.data.token) {
        const newUserData = {
          username: response.data.username,
          token: response.data.token,
          refreshToken: response.data.refreshToken,
          expiresIn: response.data.expiresIn,
        };
        
        localStorage.setItem('userData', JSON.stringify(newUserData));
        setUser(newUserData);
        
        return { success: true, user: newUserData };
      }
      
      return { success: false };
    } catch (error) {
      localStorage.removeItem('userData');
      setUser(null);
      return { success: false };
    }
  };

  const logout = () => {
    localStorage.removeItem('userData');
    setUser(null);
  };

  const value = {
    user,
    isAuthenticated: !!user,
    loading,
    login,
    register,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};