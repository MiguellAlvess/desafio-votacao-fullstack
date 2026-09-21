import { Button } from '@/components/ui/button'

import { useProposals } from '../hooks/use-proposal'
import CreateProposalDialog from './create-proposal-dialog'
import ProposalTable from './proposal-table'

const ProposalSection = () => {
  const {
    data: proposals = [],
    isLoading,
    isError,
    refetch,
  } = useProposals()

  return (
    <section className="space-y-4">
      <div className="flex items-end justify-between gap-4">
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
        proposals.length > 0 && (
          <ProposalTable proposals={proposals} />
        )}
    </section>
  )
}

export default ProposalSection