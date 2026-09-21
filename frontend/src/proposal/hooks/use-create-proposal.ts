import {
  useMutation,
  useQueryClient,
} from '@tanstack/react-query'

import { ProposalService } from '../services/proposal-service'
import type { CreateProposalRequest } from '../types/proposal'

export const useCreateProposal = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationKey: ['proposals', 'create'],
    mutationFn: (data: CreateProposalRequest) =>
      ProposalService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['proposals'],
      })
    },
  })
}