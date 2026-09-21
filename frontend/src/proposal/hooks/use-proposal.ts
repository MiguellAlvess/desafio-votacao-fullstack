import { useQuery } from '@tanstack/react-query'

import { ProposalService } from '../services/proposal-service'

export const useProposals = () => {
  return useQuery({
    queryKey: ['proposals'],
    queryFn: ProposalService.getAll,
  })
}