import React, { useState } from 'react';
import './App.css';
import Header from './components/Header';
import UserView from './components/UserView';
import AdminPanel from './components/AdminPanel';
import Login from './components/Login';

function App() {
    const [user, setUser] = useState(null);

    const handleLogout = () => {
        setUser(null);
    };

    return (
        <div className="main-container">

            {user && (
                <div className="top-service-bar">
                    <span className="user-status-text">
                        {user.role === 'ADMIN' ? "Администратор" : "Пользователь"}
                    </span>
                    <button className="logout-top-btn" onClick={handleLogout}>
                        ВЫХОД
                    </button>
                </div>
            )}

            <Header user={user} />

            <main className="content-area">
                {!user ? (
                    <Login onLoginSuccess={(userData) => setUser(userData)} />
                ) : (
                    user.role === 'ADMIN'
                        ? <AdminPanel user={user} onLogout={handleLogout} />
                        : <UserView user={user} onLogout={handleLogout} />
                )}
            </main>
        </div>
    );
}

export default App;