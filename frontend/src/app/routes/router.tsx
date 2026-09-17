import { Route, Routes } from 'react-router-dom'

import IdentifyPage from '@/pages/identify-page'

import AppLayout from '../layouts/app-layout'


export function AppRouter() {
  return (
    <Routes >
      <Route element={<AppLayout />}>
         <Route path="/" element={<IdentifyPage />} />
      </Route>
    </Routes>
  )
}