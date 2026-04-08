import React, { useState, useEffect } from 'react';
import api from '../../api';
import './ManageOrders.css';

const AdminOrders = () => {
    const [orders, setOrders] = useState([]);
    const [customers, setCustomers] = useState({});
    const [loading, setLoading] = useState(false);
    const [filterStatus, setFilterStatus] = useState('');
    const [searchDate, setSearchDate] = useState('');

    const statusLabels = {
        "PROCESSING": "Обработка",
        "SHIPPING": "Принят",
        "DELIVERED": "Доставлен",
        "CANCELLED": "Отменен"
    };

    const fetchData = async () => {
        try {
            setLoading(true);

            const [ordersRes, customersRes] = await Promise.all([
                api.get('/orders'),
                api.get('/customers')
            ]);


            const customerMap = {};
            customersRes.data.forEach(c => {
                customerMap[c.id] = c.name;
            });
            setCustomers(customerMap);

            const normalizedData = ordersRes.data.map(order => ({
                ...order,
                status: Object.keys(statusLabels).find(key => statusLabels[key] === order.status) || order.status
            }));
            setOrders(normalizedData);
        } catch (e) {
            console.error("Ошибка загрузки:", e);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleStatusChange = async (orderId, newStatus) => {
        try {
            await api.patch(`/orders/${orderId}/status`, null, {
                params: { status: newStatus }
            });

            setOrders(prev => prev.map(o =>
                o.id === orderId ? { ...o, status: newStatus } : o
            ));
        } catch (e) {
            console.error(e);
            alert("Ошибка при сохранении статуса");
        }
    };

    const filteredOrders = orders.filter(o => {
        const matchesStatus = !filterStatus || o.status === filterStatus;
        const matchesDate = !searchDate || o.deliveryDate === searchDate;
        return matchesStatus && matchesDate;
    });

    return (
        <div className="flowers-admin-panel fade-in">
            <div className="operation-content" style={{ padding: '20px 40px' }}>



                <div className="luxury-filter-panel">
                    <div className="filter-group">
                        <label className="filter-hint">ДАТА ДОСТАВКИ</label>
                        <input type="date" className="filter-input-medium" value={searchDate} onChange={e => setSearchDate(e.target.value)} />
                    </div>

                    <div className="filter-group">
                        <label className="filter-hint">СТАТУС ЗАКАЗА</label>
                        <select
                            className="filter-input-medium"
                            value={filterStatus}
                            onChange={e => setFilterStatus(e.target.value)}
                        >
                            <option value="">Все статусы</option>
                            {Object.entries(statusLabels).map(([key, label]) => (
                                <option key={key} value={key}>{label}</option>
                            ))}
                        </select>
                    </div>

                    <button className="luxury-action-btn reset-btn" onClick={() => {setSearchDate(''); setFilterStatus('');}}>
                        Сбросить <div className="btn-line"></div>
                    </button>
                </div>

                <div className="table-container-luxury">
                    <table className="luxury-table">
                        <thead>
                        <tr>
                            <th>ЗАКАЗ</th>
                            <th>ДАТА</th>
                            <th>ПОКУПАТЕЛЬ</th>
                            <th>СУММА</th>
                            <th className="text-right">СТАТУС</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredOrders.length > 0 ? (
                            filteredOrders.map(order => (
                                <tr key={order.id}>
                                    <td className="id-cell">#{order.id}</td>
                                    <td className="date-cell">{order.deliveryDate}</td>

                                    <td className="name-cell">
                                        {customers[order.customerId] || `Клиент #${order.customerId}`}
                                    </td>
                                    <td className="price-cell"><strong>{order.finalPrice} BYN</strong></td>
                                    <td className="text-right">
                                        <select
                                            className={`status-select-luxury ${order.status}`}
                                            value={order.status}
                                            onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                        >
                                            {Object.entries(statusLabels).map(([key, label]) => (
                                                <option key={key} value={key}>{label}</option>
                                            ))}
                                        </select>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr><td colSpan="5" className="no-data-text">Заказов не найдено</td></tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default AdminOrders;