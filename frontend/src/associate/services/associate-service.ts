import { api } from '@/lib/api'

import type {
  AssociateResponse,
  IdentifyAssociateRequest,
} from '../types/associate'

export const AssociateService = {
  identify: async (
    input: IdentifyAssociateRequest,
  ): Promise<AssociateResponse> => {
    const response = await api.post<AssociateResponse>(
      '/associates/identify',
      input,
    )
    return response.data
  },
}