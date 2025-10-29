import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Input } from '@/components/ui/input'
import { fetchCurrentRates } from '@/lib/api-client'
import type { ExchangeRate } from '@/lib/api-client'

export default function ExchangeRatesPage() {
  const navigate = useNavigate()
  const [searchTerm, setSearchTerm] = useState('')
  const [exchangeRates, setExchangeRates] = useState<ExchangeRate[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [ratesData] = await Promise.all([
          fetchCurrentRates(),
        ])
        setExchangeRates(ratesData)
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to fetch data')
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [])


  const filteredRates = exchangeRates.filter(rate => {
    const searchLower = searchTerm.toLowerCase()
    return (
      rate.currencyCode.toLowerCase().includes(searchLower) ||
      rate.currencyName.toLowerCase().includes(searchLower)
    )
  })

  const handleRowClick = (currency: string) => {
    navigate(`/history/${currency}`)
  }

  return (
    <div className="container mx-auto py-8 px-4">
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Exchange Rates provided by Bank of Lithuania</h1>
          <p className="text-muted-foreground mt-2">
            Current exchange rates as of {exchangeRates[0]?.date || 'N/A'}
          </p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Exchange Rates</CardTitle>
            <CardDescription>
              Click on a currency row to view its historical rates
            </CardDescription>
          </CardHeader>
          <CardContent>
            {error && (
              <div className="mb-4 p-4 text-red-600 bg-red-50 rounded-md">
                {error}
              </div>
            )}
            <div className="mb-4">
              <Input
                placeholder="Search by currency code or name..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="max-w-md"
              />
            </div>
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className='text-center'>Currency</TableHead>
                    <TableHead className='text-center'>Currency Name</TableHead>
                    <TableHead className='text-center'>Rate</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {loading ? (
                    <TableRow>
                      <TableCell colSpan={3} className="text-center">
                        Loading...
                      </TableCell>
                    </TableRow>
                  ) : filteredRates.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={3} className="text-center">
                        No rates found
                      </TableCell>
                    </TableRow>
                  ) : (
                    filteredRates.map((rate) => (
                      <TableRow
                        key={rate.currencyCode}
                        onClick={() => handleRowClick(rate.currencyCode)}
                        className="cursor-pointer hover:bg-muted"
                      >
                        <TableCell className="font-medium">
                          {rate.currencyCode}
                        </TableCell>
                        <TableCell>
                          {rate.currencyName}
                        </TableCell>
                        <TableCell>{rate.rate}</TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

