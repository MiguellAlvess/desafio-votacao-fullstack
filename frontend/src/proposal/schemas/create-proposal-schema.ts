import { z } from 'zod'

export const createProposalSchema = z.object({
  title: z
    .string()
    .trim()
    .min(1, 'Informe o título da pauta'),
  description: z.string().trim().optional(),
})

export type CreateProposalSchema = z.infer<
  typeof createProposalSchema
>