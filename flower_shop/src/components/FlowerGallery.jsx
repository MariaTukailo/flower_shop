import React, { useState, useEffect } from 'react';
import api from '../api';
import './FlowerGallery.css';


const FlowerGallery = ({ isAdmin = false, onEdit, flowers: externalFlowers }) => {

    const [flowers, setFlowers] = useState(externalFlowers || []);
    const [filterName, setFilterName] = useState('');
    const [filterColor, setFilterColor] = useState('все цвета');
    const [filterPrice, setFilterPrice] = useState('');
    const [showOnlyActive, setShowOnlyActive] = useState(!isAdmin);

    const colorOptions = ['белый', 'желтый', 'розовый', 'красный', 'зеленый', 'черный'];

    const getRussianColor = (color) => {
        if (!color) return '';
        const translations = {
            'white': 'белый', 'yellow': 'желтый', 'pink': 'розовый',
            'red': 'красный', 'green': 'зеленый', 'black': 'черный'
        };
        return translations[color.toLowerCase()] || color;
    };


    useEffect(() => {
        if (externalFlowers) {
            setFlowers(externalFlowers);
        }
    }, [externalFlowers]);

    const fetchFlowers = async () => {
        try {

            if (!externalFlowers) {
                const endpoint = showOnlyActive ? '/active' : '';
                const response = await api.get(`/flowers${endpoint}`);
                setFlowers(Array.isArray(response.data) ? response.data : []);
            }
        } catch (error) {
            console.error("Ошибка загрузки:", error);
            setFlowers([]);
        }
    };

    useEffect(() => {
        fetchFlowers();
    }, [showOnlyActive, isAdmin, externalFlowers]);

    const filteredFlowers = flowers.filter(f => {
        if (!f) return false;
        const matchesName = !filterName || f.name.toLowerCase().includes(filterName.toLowerCase());
        const matchesPrice = !filterPrice || f.price <= Number(filterPrice);

        const currentColor = getRussianColor(f.color);
        const matchesColor = (filterColor === 'все цвета') || (currentColor === filterColor);

        return matchesName && matchesColor && matchesPrice;
    });

    return (
        <div className="flower-gallery-container fade-in">

            <div className="luxury-filter-panel">
                <div className="filter-group">
                    <span className="filter-hint">Название</span>
                    <input
                        type="text"
                        className="filter-input-medium"
                        placeholder="Поиск..."
                        value={filterName}
                        onChange={(e) => setFilterName(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <span className="filter-hint">Цена до</span>
                    <input
                        type="number"
                        className="filter-input-short"
                        placeholder="BYN"
                        value={filterPrice}
                        onChange={(e) => setFilterPrice(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <span className="filter-hint">Цвет</span>
                    <select
                        className="filter-select-luxury"
                        value={filterColor}
                        onChange={(e) => setFilterColor(e.target.value)}
                    >
                        <option value="все цвета">все цвета</option>
                        {colorOptions.map(c => (
                            <option key={c} value={c}>{c}</option>
                        ))}
                    </select>
                </div>

                {isAdmin && (
                    <div className="filter-group mode-toggle-group">
                        <span className="filter-hint">Режим</span>
                        <button
                            className="luxury-action-btn"
                            onClick={() => setShowOnlyActive(!showOnlyActive)}
                        >
                            {showOnlyActive ? 'В продаже' : 'Все'}
                            <div className="btn-line"></div>
                        </button>
                    </div>
                )}
            </div>

            <div className="flowers-grid">
                {filteredFlowers.map(f => (
                    <div key={f.id} className="flower-card">
                        <div className="card-image-wrapper">
                            <img
                                src={f.pathPhoto || 'https://via.placeholder.com/300'}
                                alt={f.name}
                                className="card-img"
                            />
                            {isAdmin && (
                                <>
                                    <span className={`card-status-badge ${f.active ? 'active' : 'hidden'}`}>
                                        {f.active ? 'В продаже' : 'Скрыт'}
                                    </span>
                                    <button
                                        className="edit-icon-btn"
                                        onClick={() => onEdit(f)}
                                        title="Редактировать"
                                    >
                                        ✎
                                    </button>
                                </>
                            )}
                        </div>
                        <div className="card-content">
                            <div className="card-info-top">
                                <span className="flower-color-tag">{getRussianColor(f.color)}</span>
                            </div>
                            <h3 className="card-title">{f.name}</h3>
                            <div className="card-footer">
                                <p className="card-price">{f.price} BYN</p>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default FlowerGallery;