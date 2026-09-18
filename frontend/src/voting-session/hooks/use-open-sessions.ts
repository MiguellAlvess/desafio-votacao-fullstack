import { useQuery } from '@tanstack/react-query'

import { VotingSessionService } from '../services/voting-session-service'

export const useOpenSessions = () => {
  return useQuery({
    queryKey: ['voting-sessions', 'open'],
    queryFn: VotingSessionService.getOpenSessions,
  })
}