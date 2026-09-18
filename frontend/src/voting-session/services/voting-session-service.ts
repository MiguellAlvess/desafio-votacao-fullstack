import { api } from '@/lib/api'

import type { OpenVotingSession } from '../types/voting-session'

export const VotingSessionService = {
  getOpenSessions: async (): Promise<OpenVotingSession[]> => {
    const response = await api.get<OpenVotingSession[]>(
      '/voting-sessions/open',
    )
    return response.data
  },
}