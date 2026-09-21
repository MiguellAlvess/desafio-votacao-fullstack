export type CreateProposalRequest = {
  title: string
  description?: string
}

export type ProposalResponse = {
  id: number
  title: string
  description: string | null
  createdAt: string
}

export type VotingSessionStatus = 'OPEN' | 'CLOSED'

export type ProposalSessionSummary = {
  id: number
  status: VotingSessionStatus
  startsAt: string
  endsAt: string
}

export type ProposalManagementResponse = {
  id: number
  title: string
  description: string | null
  createdAt: string
  session: ProposalSessionSummary | null
}