import {
  render,
  screen,
} from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import {
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import ProposalSection from './proposal-section'

const useProposalsManagementMock = vi.fn()
const refetchMock = vi.fn()

vi.mock('../hooks/use-proposals-management', () => ({
  useProposalsManagement: () =>
    useProposalsManagementMock(),
}))

vi.mock('./create-proposal-dialog', () => ({
  default: () => (
    <button type="button">
      Nova pauta
    </button>
  ),
}))

vi.mock(
  '@/voting-session/components/ongoing-sessions-section',
  () => ({
    default: () => (
      <div>
        Sessões em andamento
      </div>
    ),
  }),
)

vi.mock(
  '@/voting-session/components/open-voting-session-dialog',
  () => ({
    default: () => (
      <button type="button">
        Abrir sessão
      </button>
    ),
  }),
)

describe('ProposalSection', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render registered proposals', () => {
    useProposalsManagementMock.mockReturnValue({
      data: [
        {
          id: 1,
          title: 'Aquisição de novos equipamentos',
          description:
            'Deliberação sobre novos equipamentos.',
          createdAt: '2026-09-21T10:00:00',
          session: null,
        },
      ],
      isLoading: false,
      isError: false,
      refetch: refetchMock,
    })

    render(<ProposalSection />)

    expect(
      screen.getByText(
        /aquisição de novos equipamentos/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.getByText(/1 pauta cadastrada/i),
    ).toBeInTheDocument()
    expect(
      screen.getByText('21/09/2026'),
    ).toBeInTheDocument()
    expect(
      screen.getByText(/sem sessão/i),
    ).toBeInTheDocument()
  })

  it('should render loading state while proposals are loading', () => {
    useProposalsManagementMock.mockReturnValue({
      data: [],
      isLoading: true,
      isError: false,
      refetch: refetchMock,
    })

    render(<ProposalSection />)

    expect(
      screen.getByRole('status'),
    ).toHaveTextContent(/carregando pautas/i)
  })

  it('should render empty state when there are no proposals', () => {
    useProposalsManagementMock.mockReturnValue({
      data: [],
      isLoading: false,
      isError: false,
      refetch: refetchMock,
    })

    render(<ProposalSection />)

    expect(
      screen.getByText(
        /nenhuma pauta cadastrada/i,
      ),
    ).toBeInTheDocument()
  })

  it('should render error state when proposals cannot be loaded', () => {
    useProposalsManagementMock.mockReturnValue({
      data: [],
      isLoading: false,
      isError: true,
      refetch: refetchMock,
    })

    render(<ProposalSection />)

    expect(
      screen.getByText(
        /não foi possível carregar as pautas/i,
      ),
    ).toBeInTheDocument()
  })

  it('should refetch proposals when retry button is clicked', async () => {
    const user = userEvent.setup()
    useProposalsManagementMock.mockReturnValue({
      data: [],
      isLoading: false,
      isError: true,
      refetch: refetchMock,
    })

    render(<ProposalSection />)
    await user.click(
      screen.getByRole('button', {
        name: /tentar novamente/i,
      }),
    )

    expect(refetchMock).toHaveBeenCalledOnce()
  })

  it('should filter proposals by title', async () => {
    const user = userEvent.setup()
    useProposalsManagementMock.mockReturnValue({
      data: [
        {
          id: 1,
          title: 'Aquisição de equipamentos',
          description: null,
          createdAt: '2026-09-22T10:00:00',
          session: null,
        },
        {
          id: 2,
          title: 'Revisão do fundo de reserva',
          description: null,
          createdAt: '2026-09-22T10:00:00',
          session: null,
        },
      ],
      isLoading: false,
      isError: false,
      refetch: refetchMock,
    })

    render(<ProposalSection />)
    await user.type(
      screen.getByRole('searchbox', {
        name: /buscar pauta por título/i,
      }),
      'fundo',
    )

    expect(
      screen.getByText(
        /revisão do fundo de reserva/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.queryByText(
        /aquisição de equipamentos/i,
      ),
    ).not.toBeInTheDocument()
  })
})
