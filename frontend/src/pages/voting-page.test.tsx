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

import VotingPage from './voting-page'

const useVotingSessionMock = vi.fn()
const voteMock = vi.fn()
const navigateMock = vi.fn()
const refetchMock = vi.fn()

vi.mock(
  '@/voting-session/hooks/use-voting-session',
  () => ({
    useVotingSession: () =>
      useVotingSessionMock(),
  }),
)

vi.mock('@/vote/hooks/use-cast-vote', () => ({
  useCastVote: () => ({
    mutateAsync: voteMock,
    isPending: false,
  }),
}))

vi.mock('react-router-dom', async () => {
  const actual =
    await vi.importActual<
      typeof import('react-router-dom')
    >('react-router-dom')

  return {
    ...actual,
    useNavigate: () => navigateMock,
  }
})

const renderPage = () => {
  return render(
    <MemoryRouter
      initialEntries={['/votacoes/10']}
    >
      <Routes>
        <Route
          path="/votacoes/:votingSessionId"
          element={<VotingPage />}
        />
      </Routes>
    </MemoryRouter>,
  )
}

describe('VotingPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()

    sessionStorage.setItem(
      'associateId',
      '5',
    )

    sessionStorage.setItem(
      'associateCpf',
      '12345678901',
    )
  })

  it('should render voting session details', () => {
    useVotingSessionMock.mockReturnValue({
      data: {
        id: 10,
        proposalId: 1,
        proposalTitle:
          'Aquisição de novos equipamentos',
        proposalDescription:
          'Deliberação sobre novos equipamentos.',
        startsAt:
          '2026-09-21T10:00:00',
        endsAt:
          '2026-09-21T10:30:00',
        status: 'OPEN',
      },
      isLoading: false,
      isError: false,
      refetch: refetchMock,
    })

    renderPage()

    expect(
      screen.getByText(
        /aquisição de novos equipamentos/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.getByText(/sessão aberta/i),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('button', {
        name: /sim/i,
      }),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('button', {
        name: /não/i,
      }),
    ).toBeInTheDocument()
  })

  it('should render closed session state', () => {
    useVotingSessionMock.mockReturnValue({
      data: {
        id: 10,
        proposalId: 1,
        proposalTitle:
          'Aquisição de novos equipamentos',
        proposalDescription: null,
        startsAt:
          '2026-09-21T10:00:00',
        endsAt:
          '2026-09-21T10:01:00',
        status: 'CLOSED',
      },
      isLoading: false,
      isError: false,
      refetch: refetchMock,
    })

    renderPage()

    expect(
      screen.getByText(
        /esta votação foi encerrada/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.queryByRole('button', {
        name: /^confirmar voto$/i,
      }),
    ).not.toBeInTheDocument()
  })

  it('should render loading state while voting session is loading', () => {
    useVotingSessionMock.mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
      refetch: refetchMock,
    })

    renderPage()

    expect(
      screen.getByRole('status'),
    ).toHaveTextContent(
      /carregando votação/i,
    )
  })

  it('should render error state when voting session cannot be loaded', () => {
    useVotingSessionMock.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
      refetch: refetchMock,
    })

    renderPage()

    expect(
      screen.getByText(
        /não foi possível carregar a votação/i,
      ),
    ).toBeInTheDocument()
  })

  it('should confirm and cast selected vote', async () => {
    const user = userEvent.setup()

    voteMock.mockResolvedValue({
      id: 20,
      votingSessionId: 10,
      associateId: 5,
      choice: 'YES',
      createdAt:
        '2026-09-21T10:05:00',
    })

    useVotingSessionMock.mockReturnValue({
      data: {
        id: 10,
        proposalId: 1,
        proposalTitle:
          'Aquisição de novos equipamentos',
        proposalDescription: null,
        startsAt:
          '2026-09-21T10:00:00',
        endsAt:
          '2026-09-21T10:30:00',
        status: 'OPEN',
      },
      isLoading: false,
      isError: false,
      refetch: refetchMock,
    })

    renderPage()
    await user.click(
      screen.getByRole('button', {
        name: /sim/i,
      }),
    )
    await user.click(
      screen.getByRole('button', {
        name: /^confirmar voto$/i,
      }),
    )

    expect(
      screen.getByText(
        /você está prestes a votar/i,
      ),
    ).toBeInTheDocument()

    await user.click(
      screen.getByRole('button', {
        name: /^confirmar voto$/i,
      }),
    )
    expect(
      voteMock,
    ).toHaveBeenCalledWith({
      votingSessionId: 10,
      data: {
        associateId: 5,
        choice: 'YES',
      },
    })
  })
})