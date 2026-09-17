import { useMutation } from '@tanstack/react-query'

import { AssociateService } from '../services/associate-service'
import type { IdentifyAssociateRequest } from '../types/associate'

export const useIdentifyAssociate = () => {
  return useMutation({
    mutationKey: ['associate', 'identify'],
    mutationFn: (data: IdentifyAssociateRequest) =>
      AssociateService.identify(data),
  })
}