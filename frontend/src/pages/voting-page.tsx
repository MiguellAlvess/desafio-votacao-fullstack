import axios from 'axios'
import {
  ArrowLeft,
  Clock,
} from 'lucide-react'
import {
  useState,
} from 'react'
import {
  Link,
  Navigate,
  useNavigate,
  useParams,
} from 'react-router-dom'
import { toast } from 'sonner'

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
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { associateStorage } from '@/lib/storage'
import { cn } from '@/lib/utils'
import VoteChoice from '@/vote/components/vote-choice'
import VoteConfirmationDialog from '@/vote/components/vote-confirmation-dialog'
import { useVote } from '@/vote/hooks/use-vote'
import type { VoteChoice as VoteChoiceType } from '@/vote/types/vote'
import { useVotingSession } from '@/voting-session/hooks/use-voting-session'
import { formatSessionEndTime } from '@/voting-session/utils/voting-session-date'

const VotingPage = () => {
  const { votingSessionId } = useParams()
  const navigate = useNavigate()

  const associateId =
    associateStorage.getId()

  const sessionId = Number(votingSessionId)
  const [choice, setChoice] =
    useState<VoteChoiceType | null>(null)
  const [confirmationIsOpen, setConfirmationIsOpen] =
    useState(false)
  const {
    data: session,
    isLoading,
    isError,
    refetch,
  } = useVotingSession(sessionId)
  const {
    mutateAsync: castVote,
    isPending,
  } =  useVote ()

  if (!associateId) {
    return <Navigate to="/" replace />
  }
  if (
    !votingSessionId ||
    !Number.isInteger(sessionId) ||
    sessionId <= 0
  ) {
    return <Navigate to="/votacoes" replace />
  }

  const handleConfirmVote = async () => {
    if (!choice) {
      return
    }
    try {
      await castVote({
        votingSessionId: sessionId,
        data: {
          associateId,
          choice,
        },
      })

      setConfirmationIsOpen(false)

      navigate(
        `/votacoes/${sessionId}/sucesso`,
        {
          replace: true,
        },
      )
    }catch (error) {
      if (axios.isAxiosError(error)) {
      const message = error.response?.data?.message as string | undefined
      if (message?.includes('not eligible')) {
        toast.error('Você não está elegível para votar')
        return
      }
      if (message?.includes('invalid')) {
        toast.error(
          'O CPF informado não é válido para votação',
        )
        return
      }
  }
  toast.error('Não foi possível registrar o voto')
}
  }

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
            Registrar voto
          </PageTitle>
          <PageDescription>
            Escolha uma das opções abaixo para
            registrar seu voto.
          </PageDescription>
        </PageHeaderContent>
      </PageHeader>
      <PageContent>
        {isLoading && (
          <p
            role="status"
            className="text-sm text-muted-foreground"
          >
            Carregando votação...
          </p>
        )}
        {isError && (
          <div className="rounded-lg border border-dashed p-8 text-center">
            <h2 className="font-medium">
              Não foi possível carregar a votação
            </h2>

            <p className="mt-1 text-sm text-muted-foreground">
              Tente novamente em alguns instantes.
            </p>

            <Button
              variant="outline"
              className="mt-4"
              onClick={() => refetch()}
            >
              Tentar novamente
            </Button>
          </div>
        )}
        {!isLoading && !isError && session && (
          <Card className="mx-auto max-w-2xl">
            <CardHeader>
              <div className="flex flex-wrap items-center justify-between gap-3">
                <Badge
                  variant={
                    session.status === 'OPEN'
                      ? 'secondary'
                      : 'outline'
                  }
                >
                  {session.status === 'OPEN'
                    ? 'Sessão aberta'
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

              <CardTitle className="pt-3 text-xl">
                {session.proposalTitle}
              </CardTitle>

              {session.proposalDescription && (
                <CardDescription>
                  {session.proposalDescription}
                </CardDescription>
              )}
            </CardHeader>
            <CardContent className="space-y-6">
              {session.status === 'CLOSED' ? (
                <div className="rounded-lg border border-dashed p-6 text-center">
                  <h3 className="font-medium">
                    Esta votação foi encerrada
                  </h3>
                  <p className="mt-1 text-sm text-muted-foreground">
                    Não é mais possível registrar
                    votos nesta sessão.
                  </p>
                </div>
              ) : (
                <>
                  <div>
                    <p className="mb-3 text-sm font-medium">
                      Selecione seu voto
                    </p>

                    <VoteChoice
                      value={choice}
                      onChange={setChoice}
                      disabled={isPending}
                    />
                  </div>

                  <Button
                    className="w-full"
                    disabled={
                      !choice || isPending
                    }
                    onClick={() =>
                      setConfirmationIsOpen(true)
                    }
                  >
                    Confirmar voto
                  </Button>
                </>
              )}
            </CardContent>
          </Card>
        )}
        {choice && (
          <VoteConfirmationDialog
            open={confirmationIsOpen}
            choice={choice}
            isPending={isPending}
            onOpenChange={
              setConfirmationIsOpen
            }
            onConfirm={handleConfirmVote}
          />
        )}
      </PageContent>
    </Page>
  )
}

export default VotingPage