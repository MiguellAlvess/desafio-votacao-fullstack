import { useQuery } from '@tanstack/react-query'

import { VoteService } from '../services/vote-service'

export const useVotingResult = (
  votingSessionId: number,
) => {
  return useQuery({
    queryKey: [
      'voting-sessions',
      votingSessionId,
      'result',
    ],
    queryFn: () => VoteService.getResult(votingSessionId)
  })
}