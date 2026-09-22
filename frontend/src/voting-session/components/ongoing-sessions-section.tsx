import { Clock } from 'lucide-react'
import { Link } from 'react-router-dom'

import { buttonVariants } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import type { ProposalManagementResponse } from '@/proposal/types/proposal'
import {
  formatSessionEndTime,
  getRemainingMinutes,
} from '@/voting-session/utils/voting-session-date'

type OngoingSessionsSectionProps = {
  proposals: ProposalManagementResponse[]
}

const OngoingSessionsSection = ({
  proposals,
}: OngoingSessionsSectionProps) => {
  const ongoingSessions = proposals.filter(
    (proposal) =>
      proposal.session?.status === 'OPEN',
  )
  if (ongoingSessions.length === 0) {
    return null
  }
  return (
    <section className="space-y-4">
      <div>
        <h2 className="text-lg font-semibold">
          Sessões em andamento
        </h2>
        <p className="text-sm text-muted-foreground">
          Acompanhe as sessões que estão abertas no
          momento.
        </p>
      </div>

      <div className="overflow-hidden rounded-lg border">
        {ongoingSessions.map(
          (proposal, index) => {
            const session = proposal.session
            if (!session) {
              return null
            }
            const remainingMinutes =
              getRemainingMinutes(
                session.endsAt,
              )
            return (
              <div
                key={session.id}
                className={cn(
                  'flex flex-col gap-4 p-4 sm:flex-row sm:items-center sm:justify-between',
                  index > 0 &&
                    'border-t',
                )}
              >
                <div className="space-y-1">
                  <p className="font-medium">
                    {proposal.title}
                  </p>
                  <div className="flex flex-wrap items-center gap-2 text-sm text-muted-foreground">
                    <span className="flex items-center gap-1.5">
                      <Clock className="size-4" />
                      Encerra às{' '}
                      {formatSessionEndTime(
                        session.endsAt,
                      )}
                    </span>
                    <span aria-hidden="true">
                      •
                    </span>
                    <span>
                      {remainingMinutes}{' '}
                      {remainingMinutes === 1
                        ? 'min restante'
                        : 'min restantes'}
                    </span>
                  </div>
                </div>
                <Link
                  to={`/votacoes/${session.id}/resultado`}
                  className={cn(
                    buttonVariants({
                      variant: 'outline',
                      size: 'sm',
                    }),
                    'w-full sm:w-auto',
                  )}
                >
                  Ver resultado
                </Link>
              </div>
            )
          },
        )}
      </div>
    </section>
  )
}

export default OngoingSessionsSection