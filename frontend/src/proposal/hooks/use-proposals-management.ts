import { useQuery } from '@tanstack/react-query'

import { ProposalService } from '../services/proposal-service'

export const useProposalsManagement = () => {
  return useQuery({
    queryKey: ['proposals', 'management'],
    queryFn: ProposalService.getAllForManagement,
  })
}