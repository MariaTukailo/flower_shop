import React, { useState, useEffect } from 'react';
import api from '../../api';
import FlowerGallery from '../FlowerGallery';
import './ManageFlowers.css';

const ManageFlowers = () => {
    const [flowers, setFlowers] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingFlower, setEditingFlower] = useState(null);
    const [flowerData, setFlowerData] = useState({
        name: '', price: '', active: true, pathPhoto: '', color: 'белый'
    });

    const colorOptions = ['белый', 'желтый', 'розовый', 'красный', 'зеленый', 'черный'];

    useEffect(() => {
        const fetchAll = async () => {
            try {
                const response = await api.get('/flowers');
                setFlowers(response.data);
            } catch (err) {
                console.error(err);
            }
        };
        fetchAll();
    }, []);

    const handleEditClick = (flower) => {
        setEditingFlower(flower);
        setFlowerData({ ...flower });
        setIsModalOpen(true);
    };

    const handleAddClick = () => {
        setEditingFlower(null);
        setFlowerData({ name: '', price: '', active: true, pathPhoto: '', color: 'белый' });
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setEditingFlower(null);
    };

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        const val = type === 'checkbox' ? checked : value;
        setFlowerData(prev => ({ ...prev, [name]: val }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const payload = { ...flowerData, price: Number(flowerData.price) };
            if (editingFlower) {
                const response = await api.put(`/flowers/${editingFlower.id}`, payload);
                setFlowers(prev => prev.map(f => f.id === editingFlower.id ? response.data : f));
            } else {
                const response = await api.post('/flowers', payload);
                setFlowers(prev => [...prev, response.data]);
            }
            closeModal();
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="flowers-admin-panel">
            {/* Плавающая кнопка (FAB) */}
            <button className="fab-add-flower" onClick={handleAddClick} title="Добавить новый товар">
                <span className="fab-plus">+</span>
                <span className="fab-text">Добавить цветок</span>
            </button>

            <div className="operation-content">
                <FlowerGallery
                    flowers={flowers}
                    isAdmin={true}
                    onEdit={handleEditClick}
                />
            </div>

            {isModalOpen && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <button className="close-btn" onClick={closeModal}>&times;</button>
                        <form className="flower-form-clean" onSubmit={handleSubmit}>
                            <h2 className="form-title-luxury">
                                {editingFlower ? 'Редактирование' : 'Новый цветок'}
                            </h2>
                            <div className="form-grid">
                                <div className="input-field-luxury full-width">
                                    <label>Название</label>
                                    <input
                                        name="name"
                                        type="text"
                                        value={flowerData.name}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <div className="input-field-luxury">
                                    <label>Цена (BYN)</label>
                                    <input
                                        name="price"
                                        type="number" step="0.01"
                                        value={flowerData.price}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <div className="input-field-luxury">
                                    <label>Оттенок</label>
                                    <select
                                        name="color"
                                        className="flower-select-luxury"
                                        value={flowerData.color}
                                        onChange={handleInputChange}
                                    >
                                        {colorOptions.map(opt => <option key={opt} value={opt}>{opt}</option>)}
                                    </select>
                                </div>
                                <div className="input-field-luxury full-width">
                                    <label>URL фотографии</label>
                                    <input
                                        name="pathPhoto"
                                        type="text"
                                        value={flowerData.pathPhoto || ''}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                <div className="input-field-luxury full-width">
                                    <label className="status-toggle-wrapper">
                                        <input
                                            name="active"
                                            type="checkbox"
                                            checked={flowerData.active}
                                            onChange={handleInputChange}
                                        />
                                        <span>Доступен для продажи</span>
                                    </label>
                                </div>
                            </div>
                            <div className="center-text" style={{marginTop:'30px'}}>
                                <button type="submit" className="submit-btn-luxury">
                                    {editingFlower ? 'Сохранить изменения' : 'Добавить цветок'}
                                    <div className="btn-line"></div>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ManageFlowers;