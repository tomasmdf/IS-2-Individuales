import React, { createContext, useContext, useState } from 'react'

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext)


export const AuthProvider = ({children}) => {

    const [isAuthenticated, setIsAuthenticated] = useState(() => {
        return localStorage.getItem('isAuthenticated') === 'true'
    })

    const login = (username, password) => {
        setIsAuthenticated(true)
        localStorage.setItem('isAuthenticated', true)
        localStorage.setItem('apiCredentials', JSON.stringify({ username, password }))
    }

    const logout = () => {
        setIsAuthenticated(false)
        localStorage.removeItem('isAuthenticated')
        localStorage.removeItem('apiCredentials')
    }

    return (
        <AuthContext.Provider value={{isAuthenticated, login, logout}}>
            {children}
        </AuthContext.Provider>
    )

}
