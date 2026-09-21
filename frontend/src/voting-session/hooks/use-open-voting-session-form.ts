import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'

import {
  type OpenVotingSessionSchema,
  openVotingSessionSchema,
} from '../schemas/open-voting-session-schema'
import { useOpenVotingSession } from './use-open-voting-session'

type UseOpenVotingSessionFormParams = {
  proposalId: number
  onSuccess?: () => void
  onError?: () => void
}

export const useOpenVotingSessionForm = ({
  proposalId,
  onSuccess,
  onError,
}: UseOpenVotingSessionFormParams) => {
  const {
    mutateAsync: openVotingSession,
    isPending,
  } = useOpenVotingSession()
  const form = useForm<OpenVotingSessionSchema>({
    resolver: zodResolver(openVotingSessionSchema),
    defaultValues: {
      durationInMinutes: undefined,
    },
  })

  const onSubmit = async (
    data: OpenVotingSessionSchema,
  ) => {
    try {
      await openVotingSession({
        proposalId,
        data: {
          durationInMinutes:
            data.durationInMinutes,
        },
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