import type { PropsWithChildren } from 'react'
import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'sonner'

import { QueryProvider } from './query-provider'

export function AppProviders({ children }: PropsWithChildren) {
  return (
    <QueryProvider>
      <BrowserRouter>
        {children}
        <Toaster />
      </BrowserRouter>
    </QueryProvider>
  )
}

