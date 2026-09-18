import { Clock } from 'lucide-react'
import { Link } from 'react-router-dom'

import { Badge } from '@/components/ui/badge'
import { buttonVariants } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { cn } from '@/lib/utils'

import type { OpenVotingSession } from '../types/voting-session'
import {
  formatSessionEndTime,
  getRemainingMinutes,
} from '../utils/voting-session-date'

type VotingSessionCardProps = {
  session: OpenVotingSession
}

const VotingSessionCard = ({
  session,
}: VotingSessionCardProps) => {
  const endTime = formatSessionEndTime(session.endsAt)
  const remainingMinutes = getRemainingMinutes(session.endsAt)

  return (
    <Card className="flex h-full flex-col">
      <CardHeader>
        <div className="flex items-start justify-between gap-4">
          <CardTitle className="text-base">
            {session.proposalTitle}
          </CardTitle>

          <Badge
            variant="secondary"
            className="shrink-0 text-primary"
          >
            Aberta
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="flex-1">
        <div className="flex flex-wrap items-center gap-2 text-sm text-muted-foreground">
          <Clock className="size-4" />

          <span>Encerra às {endTime}</span>

          <span aria-hidden="true">•</span>

          <span className="font-medium text-foreground">
            {remainingMinutes} min restantes
          </span>
        </div>
      </CardContent>
      <CardFooter className="border-t pt-4">
        <Link
          to={`/votacoes/${session.id}`}
          className={cn(
            buttonVariants({
              variant: 'default',
              size: 'sm',
            }),
          )}
        >
          Votar
        </Link>
      </CardFooter>
    </Card>
  )
}

export default VotingSessionCard