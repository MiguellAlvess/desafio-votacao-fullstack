import { Search } from 'lucide-react'
import {
  useMemo,
  useState,
} from 'react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import OngoingSessionsSection from '@/voting-session/components/ongoing-sessions-section'

import { useProposalsManagement } from '../hooks/use-proposals-management'
import CreateProposalDialog from './create-proposal-dialog'
import ProposalTable from './proposal-table'

const ProposalSection = () => {
  const [search, setSearch] = useState('')
  const {
    data: proposals = [],
    isLoading,
    isError,
    refetch,
  } = useProposalsManagement()
  const filteredProposals = useMemo(() => {
    const normalizedSearch = search
      .trim()
      .toLowerCase()
    if (!normalizedSearch) {
      return proposals
    }
    return proposals.filter((proposal) =>
      proposal.title
        .toLowerCase()
        .includes(normalizedSearch),
    )
  }, [proposals, search])

  return (
    <div className="space-y-8">
      <section className="space-y-4">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <h2 className="text-lg font-semibold">
              Pautas
            </h2>

            {!isLoading && !isError && (
              <p className="text-sm text-muted-foreground">
                {proposals.length}{' '}
                {proposals.length === 1
                  ? 'pauta cadastrada'
                  : 'pautas cadastradas'}
              </p>
            )}
          </div>
          <CreateProposalDialog />
        </div>
        {!isLoading &&
          !isError &&
          proposals.length > 0 && (
            <div className="relative max-w-sm">
              <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                type="search"
                value={search}
                placeholder="Buscar pauta por título"
                className="pl-9"
                aria-label="Buscar pauta por título"
                onChange={(event) =>
                  setSearch(event.target.value)
                }
              />
            </div>
          )}
        {isLoading && (
          <p
            role="status"
            className="text-sm text-muted-foreground"
          >
            Carregando pautas...
          </p>
        )}
        {isError && (
          <div className="rounded-lg border border-dashed p-8 text-center">
            <h3 className="font-medium">
              Não foi possível carregar as pautas
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
          proposals.length === 0 && (
            <div className="rounded-lg border border-dashed p-8 text-center">
              <h3 className="font-medium">
                Nenhuma pauta cadastrada
              </h3>
              <p className="mt-1 text-sm text-muted-foreground">
                Cadastre uma pauta para iniciar uma
                votação.
              </p>
            </div>
          )}
        {!isLoading &&
          !isError &&
          proposals.length > 0 &&
          filteredProposals.length === 0 && (
            <div className="rounded-lg border border-dashed p-8 text-center">
              <h3 className="font-medium">
                Nenhuma pauta encontrada
              </h3>

              <p className="mt-1 text-sm text-muted-foreground">
                Tente buscar por outro título.
              </p>
            </div>
          )}
        {!isLoading &&
          !isError &&
          filteredProposals.length > 0 && (
            <ProposalTable
              proposals={filteredProposals}
            />
          )}
      </section>
      {!isLoading &&
        !isError &&
        proposals.length > 0 && (
          <OngoingSessionsSection
            proposals={proposals}
          />
        )}
    </div>
  )
}

export default ProposalSection