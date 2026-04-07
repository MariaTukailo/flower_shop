import React, { useState, useEffect, useRef } from 'react';
import api from '../../api';
import BouquetGallery from '../BouquetGallery';
import './ManageFlowers.css';

const ManageBouquets = () => {
    const [activeOperation, setActiveOperation] = useState('findAll');
    const [availableFlowers, setAvailableFlowers] = useState([]);
    const [editingBouquet, setEditingBouquet] = useState(null);
    const [editId, setEditId] = useState('');
    const [newPrice, setNewPrice] = useState('');

    const [newBouquet, setNewBouquet] = useState({
        name: '', active: true, price: '', wrappingPaper: false,
        ribbon: false, pathPhoto: '', countFlowers: '', flowers: []
    });
    const [isCreateFlowersOpen, setIsCreateFlowersOpen] = useState(false);
    const createDropdownRef = useRef(null);

    useEffect(() => {
        const fetchFlowers = async () => {
            try {
                const fRes = await api.get('/flowers');
                setAvailableFlowers(fRes.data);
            } catch (e) { console.error("Ошибка загрузки цветов:", e); }
        };
        fetchFlowers();
    }, []);

    useEffect(() => {
        const handleClick = (e) => {
            if (createDropdownRef.current && !createDropdownRef.current.contains(e.target)) {
                setIsCreateFlowersOpen(false);
            }
        };
        document.addEventListener('mousedown', handleClick);
        return () => document.removeEventListener('mousedown', handleClick);
    }, []);

    const toggleFlowerInCreate = (flower) => {
        const isSelected = newBouquet.flowers.some(f => f.id === flower.id);
        setNewBouquet({
            ...newBouquet,
            flowers: isSelected
                ? newBouquet.flowers.filter(f => f.id !== flower.id)
                : [...newBouquet.flowers, flower]
        });
    };

    const handleCreateSubmit = async (e) => {
        e.preventDefault();
        if (newBouquet.flowers.length === 0) return alert("Выберите хотя бы один цветок для состава!");
        try {
            await api.post('/bouquets', {
                ...newBouquet,
                price: Number(newBouquet.price),
                countFlowers: Number(newBouquet.countFlowers)
            });
            alert("Букет успешно создан!");
            setNewBouquet({ name: '', active: true, price: '', wrappingPaper: false, ribbon: false, pathPhoto: '', countFlowers: '', flowers: [] });
            setActiveOperation('findAll');
        } catch (err) { alert("Ошибка при создании букета"); }
    };

    const findForEdit = async (e) => {
        e.preventDefault();
        try {
            const response = await api.get(`/bouquets/${editId}`);
            setEditingBouquet(response.data);
            setNewPrice(response.data.price);
        } catch (error) { alert("Букет с таким ID не найден"); }
    };

    const handlePriceUpdate = async () => {
        try {
            const response = await api.patch(`/bouquets/${editingBouquet.id}/price?price=${newPrice}`);
            setEditingBouquet(response.data);
            alert("Цена успешно обновлена!");
        } catch (error) { alert("Не удалось обновить цену"); }
    };

    const toggleStatus = async () => {
        try {
            const newStatus = !editingBouquet.active;
            const response = await api.patch(`/bouquets/${editingBouquet.id}/status?active=${newStatus}`);
            setEditingBouquet(response.data);
            alert(newStatus ? "Букет возвращен в продажу" : "Букет отправлен в архив");
        } catch (error) { alert("Не удалось изменить статус"); }
    };

    return (
        <div className="flowers-admin-panel fade-in">
            <div className="operations-grid">
                <button className={`op-card ${activeOperation === 'findAll' ? 'active' : ''}`} onClick={() => setActiveOperation('findAll')}>
                    <span className="op-label">Ассортимент</span>
                    <div className="op-indicator"></div>
                </button>
                <button className={`op-card ${activeOperation === 'create' ? 'active' : ''}`} onClick={() => setActiveOperation('create')}>
                    <span className="op-label">Создать новый</span>
                    <div className="op-indicator"></div>
                </button>
                <button className={`op-card ${activeOperation === 'update' ? 'active' : ''}`} onClick={() => setActiveOperation('update')}>
                    <span className="op-label">Редактировать</span>
                    <div className="op-indicator"></div>
                </button>
            </div>

            <div className="operation-content">
                {activeOperation === 'findAll' && <BouquetGallery isAdmin={true} />}

                {activeOperation === 'create' && (
                    <div className="form-container-luxury fade-in">
                        <form className="flower-form-clean" onSubmit={handleCreateSubmit}>
                            <h2 className="form-title-luxury">Новая композиция</h2>
                            <div className="form-grid">
                                <div className="input-field-luxury full-width">
                                    <label>Название букета</label>
                                    <input type="text" value={newBouquet.name} onChange={(e) => setNewBouquet({...newBouquet, name: e.target.value})} required />
                                </div>
                                <div className="input-field-luxury">
                                    <label>Цена (BYN)</label>
                                    <input type="number" step="0.01" value={newBouquet.price} onChange={(e) => setNewBouquet({...newBouquet, price: e.target.value})} required />
                                </div>
                                <div className="input-field-luxury">
                                    <label>Кол-во цветов (шт)</label>
                                    <input type="number" value={newBouquet.countFlowers} onChange={(e) => setNewBouquet({...newBouquet, countFlowers: e.target.value})} required />
                                </div>
                                <div className="input-field-luxury full-width" ref={createDropdownRef}>
                                    <label>Состав</label>
                                    <div className="custom-select-trigger" onClick={() => setIsCreateFlowersOpen(!isCreateFlowersOpen)}>
                                        {newBouquet.flowers.length > 0 ? `Выбрано цветов: ${newBouquet.flowers.length}` : "Нажмите, чтобы выбрать..."}
                                    </div>
                                    {isCreateFlowersOpen && (
                                        <div className="luxury-dropdown-box create-mode fade-in">
                                            <div className="flowers-list-vertical">
                                                {availableFlowers.map(f => (
                                                    <label key={f.id} className="luxury-checkbox-item">
                                                        <input type="checkbox" checked={newBouquet.flowers.some(sel => sel.id === f.id)} onChange={() => toggleFlowerInCreate(f)} />
                                                        <span>{f.name} <small>({f.color})</small></span>
                                                    </label>
                                                ))}
                                            </div>
                                        </div>
                                    )}
                                </div>
                                <div className="input-field-luxury">
                                    <label>Оберточная бумага</label>
                                    <input type="checkbox" checked={newBouquet.wrappingPaper} onChange={(e) => setNewBouquet({...newBouquet, wrappingPaper: e.target.checked})} />
                                </div>
                                <div className="input-field-luxury">
                                    <label>Декоративная лента</label>
                                    <input type="checkbox" checked={newBouquet.ribbon} onChange={(e) => setNewBouquet({...newBouquet, ribbon: e.target.checked})} />
                                </div>
                                <div className="input-field-luxury full-width">
                                    <label>URL фотографии</label>
                                    <input type="text" value={newBouquet.pathPhoto} onChange={(e) => setNewBouquet({...newBouquet, pathPhoto: e.target.value})} />
                                </div>
                            </div>
                            <button type="submit" className="submit-btn-luxury">Создать букет <div className="btn-line"></div></button>
                        </form>
                    </div>
                )}

                {activeOperation === 'update' && (
                    <div className="form-container-luxury fade-in">
                        {!editingBouquet ? (
                            <form className="search-form-luxury" onSubmit={findForEdit}>
                                <div className="search-container-inner">
                                    <span className="search-hint">Введите ID букета</span>
                                    <div className="search-field-group">
                                        <input type="number" className="search-input-clean" value={editId} onChange={(e) => setEditId(e.target.value)} required />
                                        <button type="submit" className="search-action-btn">Найти <div className="btn-underline"></div></button>
                                    </div>
                                </div>
                            </form>
                        ) : (
                            <div className="flower-form-clean">
                                <h2 className="form-title-luxury">{editingBouquet.name}</h2>
                                <div className="edit-panel-luxury" style={{padding: '20px'}}>

                                    <div className="input-field-luxury" style={{marginBottom: '30px'}}>
                                        <label>Изменить цену (BYN)</label>
                                        <div style={{display: 'flex', gap: '10px'}}>
                                            <input
                                                type="number"
                                                step="0.01"
                                                value={newPrice}
                                                onChange={(e) => setNewPrice(e.target.value)}
                                            />
                                            <button className="status-toggle-btn" onClick={handlePriceUpdate} style={{padding: '10px'}}>Обновить цену</button>
                                        </div>
                                    </div>

                                    <p style={{fontSize: '16px', marginBottom: '20px'}}>
                                        Статус: <strong>{editingBouquet.active ? 'В ПРОДАЖЕ' : 'В АРХИВЕ'}</strong>
                                    </p>
                                    <button
                                        type="button"
                                        className={`status-toggle-btn ${editingBouquet.active ? '' : 'active'}`}
                                        onClick={toggleStatus}
                                    >
                                        {editingBouquet.active ? 'ОТПРАВИТЬ В АРХИВ' : 'ВЕРНУТЬ В ПРОДАЖУ'}
                                    </button>
                                    <br />
                                    <button type="button" className="submit-btn-luxury" onClick={() => setEditingBouquet(null)} style={{marginTop: '40px'}}>
                                        Назад к поиску <div className="btn-line"></div>
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ManageBouquets;