import { z } from 'zod'

export const identifyAssociateSchema = z.object({
  cpf: z
    .string()
    .min(1, 'Informe seu CPF')
    .refine(
      (cpf) => cpf.replace(/\D/g, '').length === 11,
      'Informe um CPF com 11 dígitos',
    ),
})

export type IdentifyAssociateSchema = z.infer<
  typeof identifyAssociateSchema
>