import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'

import {
  type IdentifyAssociateSchema,
  identifyAssociateSchema,
} from '../schemas/identify-associate-schema'
import { useIdentifyAssociate } from './use-identify-associate'

type UseIdentifyAssociateFormParams = {
  onSuccess?: (associateId: number, cpf: string) => void
  onError?: () => void
}

export const useIdentifyAssociateForm = ({
  onSuccess,
  onError,
}: UseIdentifyAssociateFormParams) => {
  const { mutateAsync: identifyAssociate, isPending } =
    useIdentifyAssociate()

  const form = useForm<IdentifyAssociateSchema>({
    resolver: zodResolver(identifyAssociateSchema),
    defaultValues: {
      cpf: '',
    },
  })

  const onSubmit = async (data: IdentifyAssociateSchema) => {
    try {
      const associate = await identifyAssociate({
        cpf: data.cpf.replace(/\D/g, ''),
      })
      onSuccess?.(associate.id, associate.cpf)
    } catch {
      onError?.()
    }
  }

  return {
    form,
    onSubmit,
    isPending,
  }
}