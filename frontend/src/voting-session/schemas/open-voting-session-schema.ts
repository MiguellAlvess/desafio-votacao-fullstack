import { z } from 'zod'

export const openVotingSessionSchema = z.object({
  durationInMinutes: z
    .number()
    .int('A duração deve ser um número inteiro')
    .positive('A duração deve ser maior que zero')
    .optional(),
})

export type OpenVotingSessionSchema = z.infer<
  typeof openVotingSessionSchema
>