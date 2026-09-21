import { api } from '@/lib/api'

import type {
  OpenVotingSession,
  OpenVotingSessionRequest,
  VotingSessionResponse,
} from '../types/voting-session'

export const VotingSessionService = {
  getOpenSessions: async (): Promise<OpenVotingSession[]> => {
    const response = await api.get<OpenVotingSession[]>(
      '/voting-sessions/open',
    )
    return response.data
  },

  open: async (
    proposalId: number,
    input: OpenVotingSessionRequest,
  ): Promise<VotingSessionResponse> => {
    const response = await api.post<VotingSessionResponse>(
      `/proposals/${proposalId}/sessions`,
      input,
    )
    return response.data
  },
}