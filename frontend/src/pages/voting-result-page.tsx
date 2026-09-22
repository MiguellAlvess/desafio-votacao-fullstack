import {
  ArrowLeft,
  Clock,
} from 'lucide-react'
import {
  Link,
  Navigate,
  useParams,
} from 'react-router-dom'

import {
  Page,
  PageContent,
  PageDescription,
  PageHeader,
  PageHeaderContent,
  PageTitle,
} from '@/components/page/page'
import { Badge } from '@/components/ui/badge'
import {
  Button,
  buttonVariants,
} from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'
import { cn } from '@/lib/utils'
import { useVotingResult } from '@/vote/hooks/use-voting-result'
import { useVotingSession } from '@/voting-session/hooks/use-voting-session'
import { formatSessionEndTime } from '@/voting-session/utils/voting-session-date'

const calculatePercentage = (
  value: number,
  total: number,
) => {
  if (total === 0) {
    return 0
  }
  return Math.round((value / total) * 100)
}

const VotingResultPage = () => {
  const { votingSessionId } = useParams()
  const sessionId = Number(votingSessionId)
  const {
    data: session,
    isLoading: isSessionLoading,
    isError: isSessionError,
    refetch: refetchSession,
  } = useVotingSession(sessionId)
  const {
    data: result,
    isLoading: isResultLoading,
    isError: isResultError,
    refetch: refetchResult,
  } = useVotingResult(sessionId)
  if (
    !votingSessionId ||
    !Number.isInteger(sessionId) ||
    sessionId <= 0
  ) {
    return <Navigate to="/votacoes" replace />
  }

  const isLoading =
    isSessionLoading || isResultLoading
  const isError =
    isSessionError || isResultError

  const handleRetry = () => {
    refetchSession()
    refetchResult()
  }

  const yesPercentage = result
    ? calculatePercentage(
        result.yesVotes,
        result.totalVotes,
      )
    : 0

  const noPercentage = result
    ? calculatePercentage(
        result.noVotes,
        result.totalVotes,
      )
    : 0

  return (
    <Page>
      <PageHeader>
        <PageHeaderContent>
          <Link
            to="/votacoes"
            className={cn(
              buttonVariants({
                variant: 'ghost',
                size: 'sm',
              }),
              'mb-2 w-fit px-0',
            )}
          >
            <ArrowLeft />
            Voltar para votações
          </Link>

          <PageTitle>
            Resultado da votação
          </PageTitle>

          <PageDescription>
            Consulte a contabilização dos votos desta
            sessão.
          </PageDescription>
        </PageHeaderContent>
      </PageHeader>
      <PageContent>
        {isLoading && (
          <p
            role="status"
            className="text-sm text-muted-foreground"
          >
            Carregando resultado...
          </p>
        )}
        {isError && (
          <div className="rounded-lg border border-dashed p-8 text-center">
            <h2 className="font-medium">
              Não foi possível carregar o resultado
            </h2>

            <p className="mt-1 text-sm text-muted-foreground">
              Tente novamente em alguns instantes.
            </p>

            <Button
              variant="outline"
              className="mt-4"
              onClick={handleRetry}
            >
              Tentar novamente
            </Button>
          </div>
        )}
        {!isLoading &&
          !isError &&
          session &&
          result && (
            <Card className="mx-auto max-w-2xl">
              <CardHeader className="space-y-4">
                <div className="flex flex-wrap items-center justify-between gap-3">
                  <Badge
                    variant={
                      session.status === 'OPEN'
                        ? 'secondary'
                        : 'outline'
                    }
                  >
                    {session.status === 'OPEN'
                      ? 'Sessão em andamento'
                      : 'Sessão encerrada'}
                  </Badge>

                  <div className="flex items-center gap-1.5 text-sm text-muted-foreground">
                    <Clock className="size-4" />
                    Encerra às{' '}
                    {formatSessionEndTime(
                      session.endsAt,
                    )}
                  </div>
                </div>
                <div>
                  <CardTitle className="text-xl">
                    {session.proposalTitle}
                  </CardTitle>

                  {session.proposalDescription && (
                    <p className="mt-2 text-sm text-muted-foreground">
                      {session.proposalDescription}
                    </p>
                  )}
                </div>
              </CardHeader>
              <CardContent className="space-y-6">
                <Separator />
                <div className="flex items-center gap-2">
                  <h2 className="font-semibold">
                    Contabilização dos votos
                  </h2>
                </div>

                <div className="space-y-6">
                  <div className="space-y-2">
                    <div className="flex items-center justify-between gap-4">
                      <div>
                        <p className="font-medium">
                          SIM
                        </p>
                        <p className="text-sm text-muted-foreground">
                          {result.yesVotes}{' '}
                          {result.yesVotes === 1
                            ? 'voto'
                            : 'votos'}
                        </p>
                      </div>
                      <span className="text-lg font-semibold">
                        {yesPercentage}%
                      </span>
                    </div>
                    <div className="h-2 overflow-hidden rounded-full bg-muted">
                      <div
                        className="h-full rounded-full bg-primary transition-all"
                        style={{
                          width: `${yesPercentage}%`,
                        }}
                      />
                    </div>
                  </div>
                  <div className="space-y-2">
                    <div className="flex items-center justify-between gap-4">
                      <div>
                        <p className="font-medium">
                          NÃO
                        </p>

                        <p className="text-sm text-muted-foreground">
                          {result.noVotes}{' '}
                          {result.noVotes === 1
                            ? 'voto'
                            : 'votos'}
                        </p>
                      </div>
                      <span className="text-lg font-semibold">
                        {noPercentage}%
                      </span>
                    </div>
                    <div className="h-2 overflow-hidden rounded-full bg-muted">
                      <div
                        className="h-full rounded-full bg-foreground/60 transition-all"
                        style={{
                          width: `${noPercentage}%`,
                        }}
                      />
                    </div>
                  </div>
                </div>
                <Separator />
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">
                    Total de votos
                  </span>

                  <span className="text-xl font-semibold">
                    {result.totalVotes}
                  </span>
                </div>
                {result.totalVotes === 0 && (
                  <div className="rounded-lg border border-dashed p-5 text-center">
                    <p className="text-sm text-muted-foreground">
                      Nenhum voto foi registrado nesta
                      sessão até o momento.
                    </p>
                  </div>
                )}
              </CardContent>
            </Card>
          )}
      </PageContent>
    </Page>
  )
}

export default VotingResultPage