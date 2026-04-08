import React, { useState, useEffect } from 'react';
import api from '../../api';
import BouquetGallery from '../BouquetGallery';
import './ManageFlowers.css';

const ManageBouquets = () => {
    const [bouquets, setBouquets] = useState([]);
    const [activeOperation, setActiveOperation] = useState('findAll');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingBouquet, setEditingBouquet] = useState(null);
    const [availableFlowers, setAvailableFlowers] = useState([]);

    const [newBouquet, setNewBouquet] = useState({
        name: '', active: true, price: '', pathPhoto: '', countFlowers: '',
        flowers: [], wrappingPaper: true, ribbon: true
    });

    useEffect(() => {
        fetchFlowers();
        fetchBouquets();
    }, []);

    const fetchFlowers = async () => {
        try {
            const response = await api.get('/flowers');
            setAvailableFlowers(response.data);
        } catch (err) { console.error("Ошибка загрузки цветов", err); }
    };

    const fetchBouquets = async () => {
        try {
            const response = await api.get('/bouquets');
            setBouquets(response.data);
        } catch (err) { console.error("Ошибка загрузки букетов", err); }
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setEditingBouquet(null);
        setNewBouquet({
            name: '', active: true, price: '', pathPhoto: '',
            countFlowers: '', flowers: [], wrappingPaper: true, ribbon: true
        });
    };

    const handleEditClick = (bouquet) => {
        setEditingBouquet({ ...bouquet });
        setIsModalOpen(true);
    };


    const handleUpdateSubmit = async (e) => {
        if (e) e.preventDefault();
        try {
            const updateData = {
                active: editingBouquet.active,
                price: Number(editingBouquet.price),
                pathPhoto: editingBouquet.pathPhoto
            };

            const response = await api.patch(`/bouquets/${editingBouquet.id}`, updateData);


            setBouquets(prevBouquets =>
                prevBouquets.map(b => (b.id === editingBouquet.id ? response.data : b))
            );

            closeModal();
        } catch (error) {
            console.error("Ошибка обновления:", error);
            alert("Не удалось сохранить изменения.");
        }
    };

    const handleCreateSubmit = async (e) => {
        e.preventDefault();
        try {
            const payload = {
                ...newBouquet,
                price: Number(newBouquet.price),
                countFlowers: Number(newBouquet.countFlowers),
                flowers: newBouquet.flowers.map(f => ({
                    id: f.id, name: f.name, color: f.color, price: f.price, active: f.active
                }))
            };
            const response = await api.post('/bouquets', payload);


            setBouquets(prevBouquets => [response.data, ...prevBouquets]);

            closeModal();
        } catch (err) {
            alert("Ошибка при создании.");
        }
    };

    const handleAddFlowerToBouquet = (flowerId) => {
        const flower = availableFlowers.find(f => f.id === Number(flowerId));
        if (flower && !newBouquet.flowers.some(f => f.id === flower.id)) {
            setNewBouquet({ ...newBouquet, flowers: [...newBouquet.flowers, flower] });
        }
    };

    const removeFlowerFromBouquet = (id) => {
        setNewBouquet({ ...newBouquet, flowers: newBouquet.flowers.filter(f => f.id !== id) });
    };

    return (
        <div className="flowers-admin-panel">
            <div className="operations-grid">
                <div className={`op-card ${activeOperation === 'findAll' ? 'active' : ''}`}
                     onClick={() => setActiveOperation('findAll')}>
                    <span className="op-label">Ассортимент</span>
                    <div className="op-indicator"></div>
                </div>
                <div className="op-card" onClick={() => { setEditingBouquet(null); setIsModalOpen(true); }}>
                    <span className="op-label">Создать новый</span>
                    <div className="op-indicator"></div>
                </div>
            </div>

            <div className="operation-content">

                <BouquetGallery bouquets={bouquets} isAdmin={true} onEdit={handleEditClick} />
            </div>

            {isModalOpen && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <button className="close-btn" onClick={closeModal}>&times;</button>

                        <div className="flower-form-clean">
                            {editingBouquet ? (
                                <div className="edit-mode-container">
                                    <h2 className="form-title-luxury">Редактирование</h2>
                                    <p className="edit-subtitle" style={{textAlign:'center', color:'#bcaaa4', fontSize:'10px', letterSpacing:'3px', marginBottom:'20px'}}>
                                        {editingBouquet.name.toUpperCase()}
                                    </p>

                                    <div className="form-grid">
                                        <div className="input-field-luxury full-width">
                                            <label>Стоимость (BYN)</label>
                                            <input
                                                type="number"
                                                step="0.01"
                                                value={editingBouquet.price}
                                                onChange={(e) => setEditingBouquet({...editingBouquet, price: e.target.value})}
                                            />
                                        </div>

                                        <div className="input-field-luxury full-width">
                                            <label>Путь к фото</label>
                                            <input
                                                type="text"
                                                value={editingBouquet.pathPhoto}
                                                onChange={(e) => setEditingBouquet({...editingBouquet, pathPhoto: e.target.value})}
                                            />
                                        </div>

                                        <div className="input-field-luxury full-width center-text">
                                            <label style={{marginBottom: '15px', display: 'block'}}>Доступность</label>
                                            <button
                                                type="button"
                                                className={`status-toggle-btn ${!editingBouquet.active ? 'archived' : ''}`}
                                                onClick={() => setEditingBouquet({...editingBouquet, active: !editingBouquet.active})}
                                            >
                                                {editingBouquet.active ? 'АКТИВЕН' : 'СКРЫТ ИЗ КАТАЛОГА'}
                                            </button>
                                        </div>
                                    </div>

                                    <div className="center-text" style={{marginTop:'40px'}}>
                                        <button className="submit-btn-luxury" onClick={handleUpdateSubmit}>
                                            Применить изменения <div className="btn-line"></div>
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                <form onSubmit={handleCreateSubmit}>

                                    <h2 className="form-title-luxury">Новая композиция</h2>
                                    <div className="form-grid">
                                        <div className="input-field-luxury full-width">
                                            <label>Название</label>
                                            <input type="text" value={newBouquet.name} onChange={(e) => setNewBouquet({...newBouquet, name: e.target.value})} required />
                                        </div>

                                        <div className="input-field-luxury full-width">
                                            <label>Состав</label>
                                            <select
                                                className="flower-select-luxury"
                                                onChange={(e) => handleAddFlowerToBouquet(e.target.value)}
                                                value=""
                                            >
                                                <option value="" disabled>Добавить цветок...</option>
                                                {availableFlowers.map(f => (
                                                    <option key={f.id} value={f.id}>
                                                        {f.name} ({f.color})
                                                    </option>
                                                ))}
                                            </select>
                                            <div className="selected-flowers-list">
                                                {newBouquet.flowers.map(f => (
                                                    <span key={f.id} className="flower-tag">
                                                        {f.name}
                                                        <span className="remove-flower" onClick={() => removeFlowerFromBouquet(f.id)}>&times;</span>
                                                    </span>
                                                ))}
                                            </div>
                                        </div>

                                        <div className="input-field-luxury full-width">
                                            <label>URL фотографии</label>
                                            <input
                                                type="text"
                                                value={newBouquet.pathPhoto}
                                                onChange={(e) => setNewBouquet({...newBouquet, pathPhoto: e.target.value})}
                                            />
                                        </div>

                                        <div className="input-field-luxury">
                                            <label>Цена (BYN)</label>
                                            <input type="number" step="0.01" value={newBouquet.price} onChange={(e) => setNewBouquet({...newBouquet, price: e.target.value})} required />
                                        </div>
                                        <div className="input-field-luxury">
                                            <label>Кол-во (шт)</label>
                                            <input type="number" value={newBouquet.countFlowers} onChange={(e) => setNewBouquet({...newBouquet, countFlowers: e.target.value})} required />
                                        </div>
                                    </div>

                                    <div className="center-text" style={{marginTop:'30px'}}>
                                        <button type="submit" className="submit-btn-luxury">Сохранить букет</button>
                                    </div>
                                </form>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ManageBouquets;