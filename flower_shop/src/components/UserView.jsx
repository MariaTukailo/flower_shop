import React, { useState } from 'react';
import './UserView.css';
import FlowerGallery from './FlowerGallery';
import BouquetGallery from './BouquetGallery';
import Profile from './user/ManageCustomers.jsx';
import Cart from './user/ManageShoppingCards.jsx';

function UserView({ user, onLogout }) {
    const [activeTab, setActiveTab] = useState('flowers');

    const customerId = user?.customerId || user?.customer?.id;

    return (
        <div className="user-container">
            <nav className="user-nav">
                <button
                    className={activeTab === 'flowers' ? 'nav-btn active' : 'nav-btn'}
                    onClick={() => setActiveTab('flowers')}
                >
                    Цветы
                </button>
                <button
                    className={activeTab === 'bouquets' ? 'nav-btn active' : 'nav-btn'}
                    onClick={() => setActiveTab('bouquets')}
                >
                    Букеты
                </button>
                <button
                    className={activeTab === 'customers' ? 'nav-btn active' : 'nav-btn'}
                    onClick={() => setActiveTab('customers')}
                >
                    Профиль
                </button>


                <button
                    className={`nav-btn ${activeTab === 'cart' ? 'active' : ''}`}
                    onClick={() => setActiveTab('cart')}
                >
                    <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="1.5">
                        <circle cx="9" cy="21" r="1"></circle>
                        <circle cx="20" cy="21" r="1"></circle>
                        <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                    </svg>
                </button>
            </nav>

            <div className="content-display">
                {activeTab === 'flowers' && <FlowerGallery isAdmin={false} />}

                {activeTab === 'bouquets' && (
                    <BouquetGallery
                        isAdmin={false}
                        user={user}
                        onNotifyAuth={() => setActiveTab('customers')}
                    />
                )}

                {activeTab === 'cart' && <Cart customerId={customerId} />}

                {activeTab === 'customers' && (
                    <Profile user={user} onLogout={onLogout} />
                )}
            </div>
        </div>
    );
}

export default UserView;