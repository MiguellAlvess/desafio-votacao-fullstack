import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

import type { ProposalResponse } from '../types/proposal'
import { formatProposalDate } from '../utils/proposal-date'

type ProposalTableProps = {
  proposals: ProposalResponse[]
}

const ProposalTable = ({
  proposals,
}: ProposalTableProps) => {
  return (
    <div className="overflow-hidden rounded-lg border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Pauta</TableHead>

            <TableHead className="w-36">
              Criada em
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {proposals.map((proposal) => (
            <TableRow key={proposal.id}>
              <TableCell>
                <div className="space-y-1">
                  <p className="font-medium">
                    {proposal.title}
                  </p>
                  {proposal.description && (
                    <p className="text-sm text-muted-foreground">
                      {proposal.description}
                    </p>
                  )}
                </div>
              </TableCell>
              <TableCell>
                {formatProposalDate(
                  proposal.createdAt,
                )}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}

export default ProposalTable