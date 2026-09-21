import { api } from '@/lib/api'

import type {
  CreateProposalRequest,
  ProposalResponse,
} from '../types/proposal'

export const ProposalService = {
  getAll: async (): Promise<ProposalResponse[]> => {
    const response = await api.get<ProposalResponse[]>('/proposals')
    return response.data
  },

  create: async (
    input: CreateProposalRequest,
  ): Promise<ProposalResponse> => {
    const response = await api.post<ProposalResponse>(
      '/proposals',
      input,
    )
    return response.data
  },
}