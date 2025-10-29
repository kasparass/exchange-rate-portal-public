import { API_ENDPOINTS } from './api-config';

export interface Currency {
    code: string;
    name: string;
}

export interface ExchangeRate {
    currencyCode: string;
    currencyName: string;
    rate: number;
    date: string;
}

const fetchOptions: RequestInit = {
    credentials: 'include',
    headers: {
        'Content-Type': 'application/json'
    }
};

export const fetchCurrentRates = async (): Promise<ExchangeRate[]> => {
    const response = await fetch(API_ENDPOINTS.CURRENT_RATES, fetchOptions);
    if (!response.ok) {
        throw new Error('Failed to fetch current rates');
    }
    return response.json();
};

export const fetchHistoricalRates = async (
    currencyCode: string,
    fromDate: string,
    toDate: string
): Promise<ExchangeRate[]> => {
    const response = await fetch(API_ENDPOINTS.HISTORICAL_RATES(currencyCode, fromDate, toDate), fetchOptions);
    if (!response.ok) {
        throw new Error('Failed to fetch historical rates');
    }
    return response.json();
};

export const fetchCurrencies = async (): Promise<Currency[]> => {
    const response = await fetch(API_ENDPOINTS.CURRENCIES, fetchOptions);
    if (!response.ok) {
        throw new Error('Failed to fetch currencies');
    }
    return response.json();
};