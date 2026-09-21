export type OpenVotingSession = {
  id: number
  proposalId: number
  proposalTitle: string
  startsAt: string
  endsAt: string
}
export type OpenVotingSessionRequest = {
  durationInMinutes?: number
}

export type VotingSessionResponse = {
  id: number
  proposalId: number
  startsAt: string
  endsAt: string
}