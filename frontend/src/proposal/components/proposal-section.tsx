import { Button } from '@/components/ui/button'

import { useProposalsManagement } from '../hooks/use-proposals-management'
import CreateProposalDialog from './create-proposal-dialog'
import ProposalTable from './proposal-table'

const ProposalSection = () => {
  const {
    data: proposals = [],
    isLoading,
    isError,
    refetch,
  } = useProposalsManagement()

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