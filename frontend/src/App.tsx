import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router'
import { cn } from '@/lib/utils'
import ExchangeRatesPage from '@/pages/ExchangeRatesPage'
import HistoryPage from '@/pages/HistoryPage'
import CalculatorPage from '@/pages/CalculatorPage'
import './App.css'

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-background">
        <Navigation />
        <main>
          <Routes>
            <Route path="/" element={<ExchangeRatesPage />} />
            <Route path="/history/:currency" element={<HistoryPage />} />
            <Route path="/calculator" element={<CalculatorPage />} />
          </Routes>
        </main>
      </div>
    </Router>
  )
}

function Navigation() {
  const location = useLocation()

  const navItems = [
    { path: '/', label: 'Exchange Rates' },
    { path: '/calculator', label: 'Calculator' },
  ]

  return (
    <nav className="border-b">
      <div className="container mx-auto px-4">
        <div className="flex h-16 items-center justify-between">
          <div className="flex items-center space-x-2">
            <h1 className="text-xl font-bold"><Link to="/">Exchange Rate Portal</Link></h1>
          </div>
          <div className="flex space-x-4">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className={cn(
                  'px-3 py-2 rounded-md text-sm font-medium transition-colors',
                  location.pathname === item.path
                    ? 'bg-primary text-primary-foreground'
                    : 'text-muted-foreground hover:bg-muted hover:text-foreground'
                )}
              >
                {item.label}
              </Link>
            ))}
          </div>
        </div>
      </div>
    </nav>
  )
}

export default App
