import { Navigate, useNavigate } from 'react-router-dom'

import { maskCpf } from '@/associate/utils/cpf'
import {
  Page,
  PageActions,
  PageContent,
  PageDescription,
  PageHeader,
  PageHeaderContent,
  PageTitle,
} from '@/components/page/page'
import { Button } from '@/components/ui/button'
import { associateStorage } from '@/lib/storage'
import VotingSessionCard from '@/voting-session/components/voting-session-card'
import { useOpenSessions } from '@/voting-session/hooks/use-open-sessions'

const OpenSessionsPage = () => {
  const navigate = useNavigate()
  const associateId = associateStorage.getId()
  const associateCpf = associateStorage.getCpf()
  const {
    data: sessions = [],
    isLoading,
    isError,
    refetch,
  } = useOpenSessions()
  if (!associateId || !associateCpf) {
    return <Navigate to="/" replace />
  }

  const handleChangeAssociate = () => {
    associateStorage.clear()
    navigate('/')
  }

  return (
    <Page>
      <PageHeader>
        <PageHeaderContent>
          <span className="text-sm font-medium text-primary">
            Participar
          </span>

          <PageTitle>Votações disponíveis</PageTitle>

          <PageDescription>
            Escolha uma pauta para participar.
          </PageDescription>
        </PageHeaderContent>
        <PageActions>
          <div className="text-right">
            <p className="text-xs text-muted-foreground">
              Associado identificado
            </p>

            <p className="text-sm font-medium">
              {maskCpf(associateCpf)}
            </p>
          </div>

          <Button
            variant="ghost"
            onClick={handleChangeAssociate}
          >
            Trocar associado
          </Button>
        </PageActions>
      </PageHeader>
      <PageContent>
        <section className="border-t pt-8">
          <h2 className="mb-5 text-lg font-semibold">
            Sessões abertas
          </h2>
          {isLoading && (
            <p
              role="status"
              className="text-sm text-muted-foreground"
            >
              Carregando votações...
            </p>
          )}
          {isError && (
            <div className="rounded-lg border border-dashed p-8 text-center">
              <h3 className="font-medium">
                Não foi possível carregar as votações
              </h3>

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
          {!isLoading &&
            !isError &&
            sessions.length === 0 && (
              <div className="rounded-lg border border-dashed p-8 text-center">
                <h3 className="font-medium">
                  Nenhuma votação disponível
                </h3>

                <p className="mt-1 text-sm text-muted-foreground">
                  Não há sessões abertas neste momento.
                </p>
              </div>
            )}
          {!isLoading &&
            !isError &&
            sessions.length > 0 && (
              <div className="grid gap-4 md:grid-cols-2">
                {sessions.map((session) => (
                  <VotingSessionCard
                    key={session.id}
                    session={session}
                  />
                ))}
              </div>
            )}
        </section>
      </PageContent>
    </Page>
  )
}

export default OpenSessionsPage