export interface Currency {
  code: string
  names: {
    LT: string
    EN: string
  }
  number: string
  minor_units: string
}

export interface ExchangeRate {
  type?: string
  date: string
  from_currency: {
    currency: string
    amount: string
  }
  to_currency: {
    currency: string
    amount: string
  }
  rate: string
}

export interface CurrencyListData {
  currencies: Currency[]
}

export interface ExchangeRatesData {
  exchange_rates: ExchangeRate[]
  base_currency?: string
  date?: string
  target_currency?: string
  date_range?: {
    start: string
    end: string
  }
  rate_count?: number
}

