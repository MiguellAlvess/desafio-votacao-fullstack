export type VoteChoice = 'YES' | 'NO'

export type CreateVoteRequest = {
  associateId: number
  choice: VoteChoice
}

export type VoteResponse = {
  id: number
  votingSessionId: number
  associateId: number
  choice: VoteChoice
  createdAt: string
}

export type VotingResultResponse = {
  votingSessionId: number
  yesVotes: number
  noVotes: number
  totalVotes: number
}