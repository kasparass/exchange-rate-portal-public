export const API_BASE_URL = 'http://localhost:8080/api';

export const API_ENDPOINTS = {
    CURRENT_RATES: `${API_BASE_URL}/rates/current`,
    HISTORICAL_RATES: (currencyCode: string, fromDate: string, toDate: string) =>
        `${API_BASE_URL}/rates/${currencyCode}/${fromDate}/${toDate}`,
    CURRENCIES: `${API_BASE_URL}/currencies`
} as const;