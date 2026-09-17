import { ArrowRight, Loader2 } from 'lucide-react'
import { Controller } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'

import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Field,
  FieldError,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { associateStorage } from '@/lib/storage'

import { useIdentifyAssociateForm } from '../hooks/use-identify-associate-form'
import { formatCpf } from '../utils/cpf'

const IdentifyAssociateForm = () => {
  const navigate = useNavigate()

  const { form, onSubmit, isPending } =
    useIdentifyAssociateForm({
      onSuccess: (associateId, cpf) => {
        associateStorage.set(associateId, cpf)
        navigate('/votacoes')
      },
      onError: () => {
        toast.error(
          'Não foi possível identificar o associado',
        )
      },
    })

  return (
    <Card className="w-full max-w-md">
      <CardHeader className="space-y-4 justify-center text-center">
        <div className="space-y-3">
          <CardTitle className="text-2xl">
            Participe da votação
          </CardTitle>
          <CardDescription>
            Informe seu CPF para acessar as votações
            disponíveis.
          </CardDescription>
        </div>
      </CardHeader>
      <CardContent>
        <form
          className="space-y-5"
          onSubmit={form.handleSubmit(onSubmit)}
        >
          <Controller
            control={form.control}
            name="cpf"
            render={({ field, fieldState }) => (
              <Field
                data-invalid={fieldState.invalid}
              >
                <FieldLabel htmlFor={field.name}>
                  CPF
                </FieldLabel>
                <Input
                  {...field}
                  id={field.name}
                  inputMode="numeric"
                  autoComplete="off"
                  placeholder="000.000.000-00"
                  maxLength={14}
                  aria-invalid={fieldState.invalid}
                  onChange={(event) => {
                    field.onChange(
                      formatCpf(event.target.value),
                    )
                  }}
                />
                {fieldState.invalid && (
                  <FieldError
                    errors={[fieldState.error]}
                  />
                )}
              </Field>
            )}
          />
          <Button
            type="submit"
            className="w-full"
            disabled={isPending}
          >
            {isPending ? (
              <>
                <Loader2 className="animate-spin" />
                Continuando...
              </>
            ) : (
              <>
                Continuar
                <ArrowRight />
              </>
            )}
          </Button>

          <div className="border-t pt-4">
            <p className="text-center text-xs leading-relaxed text-muted-foreground">
              Seu CPF será utilizado apenas para
              identificar sua participação na votação.
            </p>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}

export default IdentifyAssociateForm