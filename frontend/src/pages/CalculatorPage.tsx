import { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { fetchCurrentRates, type ExchangeRate as ApiExchangeRate } from '@/lib/api-client'

interface ConversionResult {
  convertedAmount: number
  rate: number
  currencyName: string
}

export default function CalculatorPage() {
  const [amount, setAmount] = useState('')
  const [selectedCurrency, setSelectedCurrency] = useState('USD')
  const [result, setResult] = useState<ConversionResult | null>(null)
  const [exchangeRates, setExchangeRates] = useState<ApiExchangeRate[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [ratesData] = await Promise.all([
          fetchCurrentRates(),
        ])
        setExchangeRates(ratesData)

        // Set initial currency if available
        if (ratesData.length > 0 && !ratesData.some(rate => rate.currencyCode === selectedCurrency)) {
          setSelectedCurrency(ratesData[0].currencyCode)
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to fetch data')
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  const calculate = () => {
    const numAmount = parseFloat(amount)
    if (isNaN(numAmount) || numAmount <= 0) {
      setResult(null)
      return
    }

    const rateData = exchangeRates.find(
      rate => rate.currencyCode === selectedCurrency
    )

    if (rateData) {
      const rate = rateData.rate
      const convertedAmount = numAmount * rate
      setResult({
        convertedAmount,
        rate,
        currencyName: rateData.currencyName
      })
    } else {
      setResult(null)
    }
  }

  const handleAmountChange = (value: string) => {
    setAmount(value)
    if (value === '') {
      setResult(null)
    }
  }

  if (loading) {
    return <div className="container mx-auto max-w-md py-8 px-4">Loading...</div>
  }

  if (error) {
    return (
      <div className="container mx-auto max-w-md py-8 px-4 text-red-500">
        Error: {error}
      </div>
    )
  }

  return (
    <div className="container mx-auto max-w-md py-8 px-4">
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Currency Calculator</h1>
          <p className="text-muted-foreground mt-2">
            Convert EUR to foreign currencies using current exchange rates
          </p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Conversion Calculator</CardTitle>
            <CardDescription>
              Enter an amount and select a currency to convert from EUR
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="grid gap-4">
              <div className="space-y-2">
                <Label htmlFor="amount">Amount (EUR)</Label>
                <Input
                  id="amount"
                  type="number"
                  placeholder="Enter amount"
                  value={amount}
                  onChange={(e) => handleAmountChange(e.target.value)}
                  min="0"
                  step="0.01"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="currency">Target Currency</Label>
                <Select value={selectedCurrency} onValueChange={setSelectedCurrency}>
                  <SelectTrigger id="currency" className="w-full">
                    <SelectValue placeholder="Select a currency" />
                  </SelectTrigger>
                  <SelectContent>
                    {exchangeRates.map((rate) => {
                      return (
                        <SelectItem key={rate.currencyCode} value={rate.currencyCode}>
                          {rate.currencyCode} - {rate.currencyName}
                        </SelectItem>
                      )
                    })}
                  </SelectContent>
                </Select>
              </div>

              <Button onClick={calculate} className="w-full">
                Calculate
              </Button>
            </div>

            {result && (
              <Card className="bg-muted">
                <CardContent className="pt-6">
                  <div className="space-y-2">
                    <div className="flex justify-between items-center">
                      <span className="text-muted-foreground">Converted Amount:</span>
                      <span className="text-2xl font-bold">
                        {result.convertedAmount.toFixed(2)} {selectedCurrency}
                      </span>
                    </div>
                    <div className="flex justify-between items-center text-sm">
                      <span className="text-muted-foreground">Exchange Rate:</span>
                      <span className="font-medium">1 EUR = {result.rate} {selectedCurrency}</span>
                    </div>
                    <div className="flex justify-between items-center text-sm">
                      <span className="text-muted-foreground">Currency:</span>
                      <span className="font-medium">{result.currencyName}</span>
                    </div>
                  </div>
                </CardContent>
              </Card>
            )}

            {result === null && amount !== '' && parseFloat(amount) > 0 && (
              <div className="text-center text-sm text-muted-foreground">
                Click "Calculate" to see the conversion result
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

