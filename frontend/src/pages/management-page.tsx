import {
  Page,
  PageContent,
  PageDescription,
  PageHeader,
  PageHeaderContent,
  PageTitle,
} from '@/components/page/page'
import ProposalSection from '@/proposal/components/proposal-section'

const ManagementPage = () => {
  return (
    <Page>
      <PageHeader>
        <PageHeaderContent>
          <span className="text-sm font-medium text-primary">
            Gestão
          </span>
          <PageTitle>Gestão de pautas</PageTitle>
          <PageDescription>
            Cadastre pautas e abra sessões de votação.
          </PageDescription>
        </PageHeaderContent>
      </PageHeader>
      <PageContent>
        <ProposalSection />
      </PageContent>
    </Page>
  )
}

export default ManagementPage