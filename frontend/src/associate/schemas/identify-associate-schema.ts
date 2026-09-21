import { z } from 'zod'

import { isValidCpf } from '../utils/cpf'

export const identifyAssociateSchema = z.object({
  cpf: z
    .string()
    .min(1, 'Informe seu CPF')
    .refine(
      isValidCpf,
      'Informe um CPF válido',
    ),
})

export type IdentifyAssociateSchema = z.infer<
  typeof identifyAssociateSchema
>