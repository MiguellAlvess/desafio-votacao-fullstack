import { Link } from 'react-router-dom'

import { Badge } from '@/components/ui/badge'
import { buttonVariants } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { cn } from '@/lib/utils'
import OpenVotingSessionDialog from '@/voting-session/components/open-voting-session-dialog'

import type { ProposalManagementResponse } from '../types/proposal'
import { formatProposalDate } from '../utils/proposal-date'

type ProposalTableProps = {
  proposals: ProposalManagementResponse[]
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
            <TableHead className="w-40">
              Sessão
            </TableHead>
            <TableHead className="w-40 text-right">
              Ações
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
              <TableCell>
                {!proposal.session && (
                  <Badge variant="outline">
                    Sem sessão
                  </Badge>
                )}
                {proposal.session?.status ===
                  'OPEN' && (
                  <Badge variant="secondary">
                    Sessão aberta
                  </Badge>
                )}
                {proposal.session?.status ===
                  'CLOSED' && (
                  <Badge variant="outline">
                    Sessão encerrada
                  </Badge>
                )}
              </TableCell>
              <TableCell className="text-right">
                {!proposal.session && (
                  <OpenVotingSessionDialog
                    proposalId={proposal.id}
                    proposalTitle={proposal.title}
                  />
                )}
                {proposal.session?.status ===
                  'OPEN' && (
                  <Link
                    to={`/votacoes/${proposal.session.id}`}
                    className={cn(
                      buttonVariants({
                        variant: 'outline',
                        size: 'sm',
                      }),
                    )}
                  >
                    Ver sessão
                  </Link>
                )}
                {proposal.session?.status ===
                  'CLOSED' && (
                  <Link
                    to={`/votacoes/${proposal.session.id}/resultado`}
                    className={cn(
                      buttonVariants({
                        variant: 'outline',
                        size: 'sm',
                      }),
                    )}
                  >
                    Ver resultado
                  </Link>
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