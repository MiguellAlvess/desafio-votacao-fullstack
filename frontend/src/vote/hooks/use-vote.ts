import {
  useMutation,
  useQueryClient,
} from '@tanstack/react-query'

import { VoteService } from '../services/vote-service'
import type {
  CreateVoteRequest,
} from '../types/vote'

type CastVoteVariables = {
  votingSessionId: number
  data: CreateVoteRequest
}

export const useVote = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationKey: ['votes', 'cast'],
    mutationFn: ({
      votingSessionId,
      data,
    }: CastVoteVariables) =>
      VoteService.vote(
        votingSessionId,
        data,
      ),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: [
          'voting-sessions',
          variables.votingSessionId,
        ],
      })
      queryClient.invalidateQueries({
        queryKey: [
          'voting-sessions',
          'open',
        ],
      })
    },
  })
}