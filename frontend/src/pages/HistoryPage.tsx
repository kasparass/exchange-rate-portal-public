import { useState, useEffect } from 'react'
import { useParams } from 'react-router'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from '@/components/ui/chart'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, ResponsiveContainer } from 'recharts'
import { fetchHistoricalRates } from '@/lib/api-client'
import type { ExchangeRate } from '@/lib/api-client'

export default function HistoryPage() {
  const { currency } = useParams()
  const [viewMode, setViewMode] = useState<'chart' | 'table'>('chart')
  const [historyData, setHistoryData] = useState<ExchangeRate[]>([])
  const [currencyName, setCurrencyName] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchData = async () => {
      if (!currency) return

      try {
        // Get data for last 90 days
        const toDate = new Date().toISOString().split('T')[0]
        const fromDate = new Date(Date.now() - 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]

        const [ratesData] = await Promise.all([
          fetchHistoricalRates(currency, fromDate, toDate),
        ])

        setHistoryData(ratesData)
        const currencyInfo = ratesData.find(c => c.currencyCode === currency)
        setCurrencyName(currencyInfo?.currencyName || 'Unknown')
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to fetch data')
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [currency])

  // Format data for chart
  const chartData = historyData.map(rate => ({
    date: rate.date,
    rate: rate.rate,
  })).reverse()

  if (!currency) return null

  return (
    <div className="container mx-auto py-8 px-4">
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">
            Historical Exchange Rates
          </h1>
          <p className="text-muted-foreground mt-2">
            {currency} ({currencyName}) - Last 90 Days
          </p>
        </div>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle>EUR to {currency}</CardTitle>
                <CardDescription>
                  Historical exchange rate data
                </CardDescription>
              </div>
              <div className="flex gap-2">
                <Button
                  variant={viewMode === 'chart' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setViewMode('chart')}
                >
                  Chart
                </Button>
                <Button
                  variant={viewMode === 'table' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setViewMode('table')}
                >
                  Table
                </Button>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            {error && (
              <div className="mb-4 p-4 text-red-600 bg-red-50 rounded-md">
                {error}
              </div>
            )}
            {loading ? (
              <div className="h-[400px] flex items-center justify-center">
                Loading...
              </div>
            ) : viewMode === 'chart' ? (
              <div className="h-[400px]">
                <ChartContainer
                  config={{
                    rate: {
                      label: 'Rate',
                      color: 'var(--foreground)',
                    },
                  }}
                  className='w-full h-full min-h-[300px]'
                >
                  <LineChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis
                      dataKey="date"
                      tick={{ fontSize: 12 }}
                      angle={-45}
                      textAnchor="end"
                      height={80}
                    />
                    <YAxis
                      tick={{ fontSize: 12 }}
                      domain={['auto', 'auto']}
                    />
                    <ChartTooltip content={<ChartTooltipContent />} />
                    <Line
                      type="monotone"
                      dataKey="rate"
                      stroke="var(--foreground)"
                      strokeWidth={2}
                      dot={false}
                      activeDot={{ r: 4 }}
                    />
                  </LineChart>
                </ChartContainer>
              </div>
            ) : (
              <div className="rounded-md border">
                <table className="w-full">
                  <thead>
                    <tr className="border-b">
                      <th className="h-12 px-4 text-left align-middle font-medium">Date</th>
                      <th className="h-12 px-4 text-left align-middle font-medium">Rate</th>
                    </tr>
                  </thead>
                  <tbody>
                    {historyData.map((rate, index) => (
                      <tr key={index} className="border-b">
                        <td className="p-4">{rate.date}</td>
                        <td className="p-4">{rate.rate}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Summary</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-muted-foreground">Highest Rate</p>
                <p className="text-2xl font-bold">
                  {Math.max(...chartData.map(d => d.rate)).toFixed(4)}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Lowest Rate</p>
                <p className="text-2xl font-bold">
                  {Math.min(...chartData.map(d => d.rate)).toFixed(4)}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Average Rate</p>
                <p className="text-2xl font-bold">
                  {(chartData.reduce((sum, d) => sum + d.rate, 0) / chartData.length).toFixed(4)}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Latest Rate</p>
                <p className="text-2xl font-bold">
                  {chartData[chartData.length - 1]?.rate.toFixed(4) || 'N/A'}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div >
  )
}

