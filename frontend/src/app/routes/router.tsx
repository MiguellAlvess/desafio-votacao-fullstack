import { Route, Routes } from 'react-router-dom'

import App from '../App'
import AppLayout from '../layouts/app-layout'


export function AppRouter() {
  return (
    <Routes >
      <Route element={<AppLayout />}>
        <Route path="/" element={<App />} />
      </Route>
    </Routes>
  )
}