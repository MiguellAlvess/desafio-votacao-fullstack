import { useQuery } from '@tanstack/react-query'

import { VotingSessionService } from '../services/voting-session-service'

export const useVotingSession = (
  votingSessionId: number,
) => {
  return useQuery({
    queryKey: [
      'voting-sessions',
      votingSessionId,
    ],
    queryFn: () =>
      VotingSessionService.getById(
        votingSessionId,
      ),
  })
}