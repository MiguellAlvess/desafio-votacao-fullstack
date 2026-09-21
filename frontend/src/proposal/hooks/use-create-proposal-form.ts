import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'

import {
  type CreateProposalSchema,
  createProposalSchema,
} from '../schemas/create-proposal-schema'
import { useCreateProposal } from './use-create-proposal'

type UseCreateProposalFormParams = {
  onSuccess?: () => void
  onError?: () => void
}

export const useCreateProposalForm = ({
  onSuccess,
  onError,
}: UseCreateProposalFormParams = {}) => {
  const {
    mutateAsync: createProposal,
    isPending,
  } = useCreateProposal()

  const form = useForm<CreateProposalSchema>({
    resolver: zodResolver(createProposalSchema),
    defaultValues: {
      title: '',
      description: '',
    },
  })

  const onSubmit = async (
    data: CreateProposalSchema,
  ) => {
    try {
      await createProposal({
        title: data.title.trim(),
        description:
          data.description?.trim() || undefined,
      })
      form.reset()
      onSuccess?.()
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