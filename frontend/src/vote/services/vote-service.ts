import { api } from '@/lib/api'

import type {
  CreateVoteRequest,
  VoteResponse,
} from '../types/vote'

export const VoteService = {
  vote: async (
    votingSessionId: number,
    input: CreateVoteRequest,
  ): Promise<VoteResponse> => {
    const response = await api.post<VoteResponse>(
      `/voting-sessions/${votingSessionId}/votes`,
      input,
    )
    return response.data
  },
}