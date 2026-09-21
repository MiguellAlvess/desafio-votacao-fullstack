import { Loader2 } from 'lucide-react'

import {
  AlertDialog,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { Button } from '@/components/ui/button'

import type { VoteChoice } from '../types/vote'

type VoteConfirmationDialogProps = {
  open: boolean
  choice: VoteChoice
  isPending: boolean
  onOpenChange: (open: boolean) => void
  onConfirm: () => void
}

const VoteConfirmationDialog = ({
  open,
  choice,
  isPending,
  onOpenChange,
  onConfirm,
}: VoteConfirmationDialogProps) => {
  const choiceLabel =
    choice === 'YES' ? 'SIM' : 'NÃO'

  return (
    <AlertDialog
      open={open}
      onOpenChange={onOpenChange}
    >
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>
            Confirmar voto
          </AlertDialogTitle>

          <AlertDialogDescription>
            Você está prestes a votar{' '}
            <strong>{choiceLabel}</strong> nesta
            pauta. O voto não poderá ser alterado
            após a confirmação.
          </AlertDialogDescription>
        </AlertDialogHeader>

        <AlertDialogFooter>
          <AlertDialogCancel
            disabled={isPending}
          >
            Cancelar
          </AlertDialogCancel>
          <Button
            type="button"
            disabled={isPending}
            onClick={onConfirm}
          >
            {isPending && (
              <Loader2 className="animate-spin" />
            )}
            {isPending
              ? 'Confirmando...'
              : 'Confirmar voto'}
          </Button>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}

export default VoteConfirmationDialog