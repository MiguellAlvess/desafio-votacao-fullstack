import { format } from 'date-fns'

export const formatProposalDate = (
  createdAt: string,
) => {
  return format(new Date(createdAt), 'dd/MM/yyyy')
}