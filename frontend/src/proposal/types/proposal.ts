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