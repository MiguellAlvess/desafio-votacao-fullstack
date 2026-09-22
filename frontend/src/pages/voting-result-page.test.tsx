import {
  render,
  screen,
} from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import {
  MemoryRouter,
  Route,
  Routes,
} from 'react-router-dom'
import {
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import VotingResultPage from './voting-result-page'

const useVotingSessionMock = vi.fn()
const useVotingResultMock = vi.fn()

const refetchSessionMock = vi.fn()
const refetchResultMock = vi.fn()

vi.mock(
  '@/voting-session/hooks/use-voting-session',
  () => ({
    useVotingSession: () =>
      useVotingSessionMock(),
  }),
)

vi.mock(
  '@/vote/hooks/use-voting-result',
  () => ({
    useVotingResult: () =>
      useVotingResultMock(),
  }),
)

const renderPage = () => {
  return render(
    <MemoryRouter
      initialEntries={[
        '/votacoes/10/resultado',
      ]}
    >
      <Routes>
        <Route
          path="/votacoes/:votingSessionId/resultado"
          element={<VotingResultPage />}
        />
      </Routes>
    </MemoryRouter>,
  )
}

describe('VotingResultPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render voting result', () => {
    useVotingSessionMock.mockReturnValue({
      data: {
        id: 10,
        proposalId: 1,
        proposalTitle:
          'Aquisição de novos equipamentos',
        proposalDescription:
          'Deliberação sobre novos equipamentos.',
        startsAt:
          '2026-09-22T10:00:00',
        endsAt:
          '2026-09-22T10:30:00',
        status: 'CLOSED',
      },
      isLoading: false,
      isError: false,
      refetch: refetchSessionMock,
    })
    useVotingResultMock.mockReturnValue({
      data: {
        votingSessionId: 10,
        yesVotes: 10,
        noVotes: 4,
        totalVotes: 14,
      },
      isLoading: false,
      isError: false,
      refetch: refetchResultMock,
    })

    renderPage()

    expect(
      screen.getByText(
        /aquisição de novos equipamentos/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.getByText(/sessão encerrada/i),
    ).toBeInTheDocument()
    expect(
      screen.getByText('10 votos'),
    ).toBeInTheDocument()
    expect(
      screen.getByText('4 votos'),
    ).toBeInTheDocument()
    expect(
      screen.getByText('71%'),
    ).toBeInTheDocument()
    expect(
      screen.getByText('29%'),
    ).toBeInTheDocument()
    expect(
      screen.getByText('14'),
    ).toBeInTheDocument()
  })

  it('should render zero votes without invalid percentages', () => {
    useVotingSessionMock.mockReturnValue({
      data: {
        id: 10,
        proposalId: 1,
        proposalTitle:
          'Revisão do fundo de reserva',
        proposalDescription: null,
        startsAt:
          '2026-09-22T10:00:00',
        endsAt:
          '2026-09-22T10:30:00',
        status: 'OPEN',
      },
      isLoading: false,
      isError: false,
      refetch: refetchSessionMock,
    })
    useVotingResultMock.mockReturnValue({
      data: {
        votingSessionId: 10,
        yesVotes: 0,
        noVotes: 0,
        totalVotes: 0,
      },
      isLoading: false,
      isError: false,
      refetch: refetchResultMock,
    })

    renderPage()

    expect(
      screen.getByText(
        /nenhum voto foi registrado/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.getAllByText('0%'),
    ).toHaveLength(2)
  })

  it('should render loading state while result is loading', () => {
    useVotingSessionMock.mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
      refetch: refetchSessionMock,
    })
    useVotingResultMock.mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
      refetch: refetchResultMock,
    })

    renderPage()

    expect(
      screen.getByRole('status'),
    ).toHaveTextContent(
      /carregando resultado/i,
    )
  })

  it('should render error state when result cannot be loaded', () => {
    useVotingSessionMock.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
      refetch: refetchSessionMock,
    })
    useVotingResultMock.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: false,
      refetch: refetchResultMock,
    })

    renderPage()

    expect(
      screen.getByText(
        /não foi possível carregar o resultado/i,
      ),
    ).toBeInTheDocument()
  })

  it('should refetch session and result when retry button is clicked', async () => {
    const user = userEvent.setup()
    useVotingSessionMock.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
      refetch: refetchSessionMock,
    })
    useVotingResultMock.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
      refetch: refetchResultMock,
    })

    renderPage()
    await user.click(
      screen.getByRole('button', {
        name: /tentar novamente/i,
      }),
    )

    expect(
      refetchSessionMock,
    ).toHaveBeenCalledOnce()
    expect(
      refetchResultMock,
    ).toHaveBeenCalledOnce()
  })
})