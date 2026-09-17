import IdentifyAssociateForm from '@/associate/components/identify-associate-form'
import { Page } from '@/components/page/page'

const IdentifyPage = () => {
  return (
    <Page className="flex min-h-[calc(100vh-4rem)] items-center justify-center py-12">
      <IdentifyAssociateForm />
    </Page>
  )
}

export default IdentifyPage