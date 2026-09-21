import {
  CheckCircle2,
} from 'lucide-react'
import {
  Link,
  Navigate,
  useParams,
} from 'react-router-dom'

import {
  Page,
  PageContent,
} from '@/components/page/page'
import { buttonVariants } from '@/components/ui/button'
import {
  Card,
  CardContent,
} from '@/components/ui/card'
import { associateStorage } from '@/lib/storage'
import { cn } from '@/lib/utils'

const VoteSuccessPage = () => {
  const { votingSessionId } = useParams()
  const associateId =
    associateStorage.getId()
  const sessionId = Number(votingSessionId)
  if (!associateId) {
    return <Navigate to="/" replace />
  }
  if (
    !votingSessionId ||
    !Number.isInteger(sessionId) ||
    sessionId <= 0
  ) {
    return <Navigate to="/votacoes" replace />
  }

  return (
    <Page className="flex min-h-[calc(100vh-4rem)] items-center justify-center py-12">
      <PageContent className="w-full">
        <Card className="mx-auto max-w-lg">
          <CardContent className="flex flex-col items-center px-6 py-10 text-center">
            <div className="mb-5 flex size-14 items-center justify-center rounded-full bg-primary/10 text-primary">
              <CheckCircle2 className="size-7" />
            </div>

            <h1 className="text-2xl font-semibold">
              Voto registrado
            </h1>

            <p className="mt-2 max-w-sm text-sm text-muted-foreground">
              Seu voto foi registrado com sucesso.
            </p>
            <Link
              to="/votacoes"
              className={cn(
                buttonVariants(),
                'mt-6 w-full',
              )}
            >
              Ver outras votações
            </Link>
          </CardContent>
        </Card>
      </PageContent>
    </Page>
  )
}

export default VoteSuccessPage