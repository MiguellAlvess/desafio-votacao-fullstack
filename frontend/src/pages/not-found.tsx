import { ArrowLeft } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

import { Button } from '@/components/ui/button'

const NotFoundPage = () => {
  const navigate = useNavigate()

  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <div className="w-full max-w-xl text-center">
        <h1 className="text-8xl font-bold text-muted-foreground">404</h1>
        <div className="my-6 flex justify-center">
          <img
            src="/assets/images/not-found-page.png"
            alt="Página não encontrada"
            className="w-64"
          />
        </div>
        <h2 className="mb-3 text-3xl font-bold">Ops! Página não encontrada</h2>
        <p className="mx-auto mb-6 max-w-md text-muted-foreground">
          A página que você está tentando acessar não existe ou foi removida.
        </p>
        <div className="flex justify-center">
          <Button
            variant="outline"
            onClick={() => navigate(-1)}
            className="gap-2"
          >
            <ArrowLeft className="h-5 w-5" />
            Voltar
          </Button>
        </div>
      </div>
    </div>
  )
}

export default NotFoundPage