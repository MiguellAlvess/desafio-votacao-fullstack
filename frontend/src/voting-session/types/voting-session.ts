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

export type VotingSessionStatus = 'OPEN' | 'CLOSED'

export type VotingSessionDetails = {
  id: number
  proposalId: number
  proposalTitle: string
  proposalDescription: string | null
  startsAt: string
  endsAt: string
  status: VotingSessionStatus
}