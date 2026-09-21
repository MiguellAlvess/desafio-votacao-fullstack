import { Route, Routes } from 'react-router-dom'

import IdentifyPage from '@/pages/identify-page'
import ManagementPage from '@/pages/management-page'
import OpenSessionsPage from '@/pages/open-sessions-page'
import VoteSuccessPage from '@/pages/vote-success-page'
import VotingPage from '@/pages/voting-page'

import AppLayout from '../layouts/app-layout'


export function AppRouter() {
  return (
    <Routes >
      <Route element={<AppLayout />}>
         <Route path="/" element={<IdentifyPage />} />
          <Route
          path="/votacoes"
          element={<OpenSessionsPage />}
        />
           <Route
          path="/gestao"
          element={<ManagementPage />}
        />
                <Route
          path="/votacoes/:votingSessionId"
          element={<VotingPage />}
        />
        <Route
          path="/votacoes/:votingSessionId/sucesso"
          element={<VoteSuccessPage />}
        />
      </Route>
    </Routes>
  )
}