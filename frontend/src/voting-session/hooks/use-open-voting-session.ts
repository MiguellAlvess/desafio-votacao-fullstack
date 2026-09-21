import {
  useMutation,
  useQueryClient,
} from '@tanstack/react-query'

import { VotingSessionService } from '../services/voting-session-service'
import type { OpenVotingSessionRequest } from '../types/voting-session'

type OpenVotingSessionVariables = {
  proposalId: number
  data: OpenVotingSessionRequest
}

export const useOpenVotingSession = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationKey: ['voting-sessions', 'open'],
    mutationFn: ({
      proposalId,
      data,
    }: OpenVotingSessionVariables) =>
      VotingSessionService.open(proposalId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['voting-sessions', 'open'],
      })

      queryClient.invalidateQueries({
        queryKey: ['proposals'],
      })
    },
  })
}