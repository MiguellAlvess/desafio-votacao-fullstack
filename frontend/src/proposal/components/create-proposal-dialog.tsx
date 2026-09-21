import {
  Loader2,
  Plus,
} from 'lucide-react'
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
  FieldError,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'

import { useCreateProposalForm } from '../hooks/use-create-proposal-form'

const CreateProposalDialog = () => {
  const [isOpen, setIsOpen] = useState(false)
  const {
    form,
    onSubmit,
    isPending,
  } = useCreateProposalForm({
    onSuccess: () => {
      setIsOpen(false)
      toast.success('Pauta cadastrada com sucesso')
    },
    onError: () => {
      toast.error('Não foi possível cadastrar a pauta')
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
      <Button onClick={() => setIsOpen(true)}>
        <Plus />
        Nova pauta
      </Button>
      <Dialog
        open={isOpen}
        onOpenChange={handleOpenChange}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Nova pauta</DialogTitle>
            <DialogDescription>
              Cadastre o assunto que será levado à
              votação.
            </DialogDescription>
          </DialogHeader>
          <form
            className="space-y-5"
            onSubmit={form.handleSubmit(onSubmit)}
          >
            <Controller
              control={form.control}
              name="title"
              render={({ field, fieldState }) => (
                <Field
                  data-invalid={fieldState.invalid}
                >
                  <FieldLabel htmlFor={field.name}>
                    Título da pauta
                  </FieldLabel>

                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Digite o título da pauta"
                    aria-invalid={fieldState.invalid}
                  />

                  {fieldState.invalid && (
                    <FieldError
                      errors={[fieldState.error]}
                    />
                  )}
                </Field>
              )}
            />
            <Controller
              control={form.control}
              name="description"
              render={({ field, fieldState }) => (
                <Field
                  data-invalid={fieldState.invalid}
                >
                  <FieldLabel htmlFor={field.name}>
                    Descrição
                  </FieldLabel>

                  <Textarea
                    {...field}
                    id={field.name}
                    placeholder="Descreva a pauta"
                    rows={4}
                    aria-invalid={fieldState.invalid}
                  />

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
                onClick={() => handleOpenChange(false)}
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
                Cadastrar pauta
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </>
  )
}

export default CreateProposalDialog