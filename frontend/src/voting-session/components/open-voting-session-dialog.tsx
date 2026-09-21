import { Loader2 } from 'lucide-react'
import { useState } from 'react'
import { Controller } from 'react-hook-form'
import { toast } from 'sonner'

import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Field,
  FieldDescription,
  FieldError,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'

import { useOpenVotingSessionForm } from '../hooks/use-open-voting-session-form'

type OpenVotingSessionDialogProps = {
  proposalId: number
  proposalTitle: string
}

const OpenVotingSessionDialog = ({
  proposalId,
  proposalTitle,
}: OpenVotingSessionDialogProps) => {
  const [isOpen, setIsOpen] = useState(false)

  const {
    form,
    onSubmit,
    isPending,
  } = useOpenVotingSessionForm({
    proposalId,
    onSuccess: () => {
      setIsOpen(false)
      toast.success('Sessão aberta com sucesso')
    },
    onError: () => {
      toast.error(
        'Não foi possível abrir a sessão de votação',
      )
    },
  })

  const handleOpenChange = (open: boolean) => {
    setIsOpen(open)
    if (!open) {
      form.reset()
    }
  }

  return (
    <>
      <Button
        variant="outline"
        size="sm"
        onClick={() => setIsOpen(true)}
      >
        Abrir sessão
      </Button>
      <Dialog
        open={isOpen}
        onOpenChange={handleOpenChange}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              Abrir sessão de votação
            </DialogTitle>
            <DialogDescription>
              Defina por quanto tempo a votação ficará
              disponível.
            </DialogDescription>
          </DialogHeader>
          <div className="rounded-md bg-muted p-4">
            <p className="text-xs text-muted-foreground">
              Pauta
            </p>
            <p className="mt-1 font-medium">
              {proposalTitle}
            </p>
          </div>
          <form
            className="space-y-5"
            onSubmit={form.handleSubmit(onSubmit)}
          >
            <Controller
              control={form.control}
              name="durationInMinutes"
              render={({ field, fieldState }) => (
                <Field
                  data-invalid={fieldState.invalid}
                >
                  <FieldLabel htmlFor={field.name}>
                    Duração em minutos
                  </FieldLabel>
                  <Input
                    id={field.name}
                    type="number"
                    min={1}
                    placeholder="1"
                    value={field.value ?? ''}
                    aria-invalid={fieldState.invalid}
                    onChange={(event) => {
                      const value = event.target.value
                      field.onChange(
                        value === ''
                          ? undefined
                          : Number(value),
                      )
                    }}
                  />
                  <FieldDescription>
                    Se nenhuma duração for informada, a
                    sessão ficará aberta por 1 minuto.
                  </FieldDescription>
                  {fieldState.invalid && (
                    <FieldError
                      errors={[fieldState.error]}
                    />
                  )}
                </Field>
              )}
            />
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                disabled={isPending}
                onClick={() =>
                  handleOpenChange(false)
                }
              >
                Cancelar
              </Button>
              <Button
                type="submit"
                disabled={isPending}
              >
                {isPending && (
                  <Loader2 className="animate-spin" />
                )}
                {isPending
                  ? 'Abrindo...'
                  : 'Abrir sessão'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </>
  )
}

export default OpenVotingSessionDialog