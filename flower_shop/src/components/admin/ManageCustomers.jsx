import React, { useState, useEffect } from 'react';
import api from '../../api';
import './ManageCustomers.css';

const ManageCustomers = () => {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [selectedCustomer, setSelectedCustomer] = useState(null);
    const [customerOrders, setCustomerOrders] = useState([]);
    const [modalLoading, setModalLoading] = useState(false);

    const [filterName, setFilterName] = useState('');
    const [filterPhone, setFilterPhone] = useState('');


    const statusMap = {
        'PROCESSING': 'В обработке',
        'SHIPPING': 'Доставляется',
        'DELIVERED': 'Доставлен',
        'CANCELLED': 'Отменен',
        'PENDING': 'Ожидает'
    };

    const fetchData = async () => {
        try {
            setLoading(true);
            const response = await api.get('/customers');
            setCustomers(response.data);
        } catch (e) { console.error("Ошибка загрузки:", e); }
        finally { setLoading(false); }
    };

    useEffect(() => { fetchData(); }, []);

    const handleOpenDetails = async (customer) => {
        setSelectedCustomer(customer);
        setModalLoading(true);
        try {
            const response = await api.get('/orders');
            const clientOrders = response.data.filter(order => order.customerId === customer.id);
            setCustomerOrders(clientOrders);
        } catch (e) { console.error("Ошибка загрузки истории:", e); }
        finally { setModalLoading(false); }
    };

    const filteredCustomers = customers.filter(c => {
        const matchesName = c.name.toLowerCase().includes(filterName.toLowerCase());
        const matchesPhone = c.phoneNumber.includes(filterPhone);
        const isNotAdmin = c.id !== 1 && !c.name.toLowerCase().includes('admin');
        return matchesName && matchesPhone && isNotAdmin;
    });

    return (
        <div className="flowers-admin-panel fade-in">
            <div className="operation-content">
                <div className="admin-inner-padding">
                    <div className="luxury-filter-panel">
                        <div className="filter-group">
                            <span className="filter-hint">Поиск по ФИО</span>
                            <input
                                type="text"
                                className="filter-input-medium wide"
                                value={filterName}
                                onChange={(e) => setFilterName(e.target.value)}
                                placeholder="Иван Иванов..."
                            />
                        </div>
                        <div className="filter-group">
                            <span className="filter-hint">Номер телефона</span>
                            <input
                                type="text"
                                className="filter-input-medium"
                                value={filterPhone}
                                onChange={(e) => setFilterPhone(e.target.value)}
                                placeholder="+375..."
                            />
                        </div>

                        <button className="luxury-action-btn reset-btn" onClick={() => { setFilterName(''); setFilterPhone(''); }}>
                            Очистить <div className="btn-line"></div>
                        </button>
                    </div>

                    <div className="table-container-luxury">
                        {loading ? (
                            <div className="loading-text">Синхронизация...</div>
                        ) : (
                            <table className="luxury-table">
                                <thead>
                                <tr>
                                    <th>Покупатель</th>
                                    <th>Контактный номер</th>
                                    <th>Заказов</th>
                                    <th className="text-right">Управление</th>
                                </tr>
                                </thead>
                                <tbody>
                                {filteredCustomers.map(customer => (
                                    <tr key={customer.id}>
                                        <td className="name-cell">{customer.name}</td>
                                        <td className="phone-cell">{customer.phoneNumber}</td>
                                        <td>
                                                <span className="order-count-badge">
                                                    {customer.orderIds?.length || 0} ШТ.
                                                </span>
                                        </td>
                                        <td className="text-right">
                                            <button className="luxury-action-btn" onClick={() => handleOpenDetails(customer)}>
                                                Детали <div className="btn-line"></div>
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                </div>
            </div>

            {selectedCustomer && (
                <div className="modal-overlay" onClick={() => setSelectedCustomer(null)}>
                    <div className="modal-content" onClick={e => e.stopPropagation()}>
                        <button className="close-btn" onClick={() => setSelectedCustomer(null)}>&times;</button>

                        <div className="modal-header-luxury">
                            <h2 className="form-title-luxury">{selectedCustomer.name}</h2>
                            <div className="luxury-line-short"></div>
                        </div>

                        <div className="modal-body-luxury">
                            <div className="info-row-luxury">
                                <span className="info-label">Телефон</span>
                                <span className="info-value">{selectedCustomer.phoneNumber}</span>
                            </div>

                            <h3 className="section-title-mini">История покупок</h3>

                            <div className="modal-order-history-scroll">
                                {modalLoading ? (
                                    <p className="center-text">Загрузка...</p>
                                ) : customerOrders.length > 0 ? (
                                    customerOrders.map(order => (
                                        (order.bouquets || []).map((bq, idx) => (
                                            <div key={`${order.id}-${idx}`} className="bouquet-order-card-luxury">
                                                <div className="bq-text-info">
                                                    <div className="order-date-label">ЗАКАЗ #{order.id}</div>
                                                    <div className="bq-name-mini">{bq.name}</div>
                                                    <div className="bq-price-mini">{order.finalPrice} BYN</div>
                                                </div>

                                                <div className={`mini-status-label ${order.status}`}>
                                                    {statusMap[order.status] || order.status}
                                                </div>
                                            </div>
                                        ))
                                    ))
                                ) : (
                                    <p className="no-data-text">Заказов пока нет</p>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ManageCustomers;