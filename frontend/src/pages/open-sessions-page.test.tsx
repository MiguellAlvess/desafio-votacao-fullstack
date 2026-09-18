import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import {
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import OpenSessionsPage from './open-sessions-page'

const navigateMock = vi.fn()
const refetchMock = vi.fn()
const useOpenSessionsMock = vi.fn()

vi.mock('@/voting-session/hooks/use-open-sessions', () => ({
  useOpenSessions: () => useOpenSessionsMock(),
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
    <MemoryRouter>
      <OpenSessionsPage />
    </MemoryRouter>,
  )
}

describe('OpenSessionsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    sessionStorage.clear()

    sessionStorage.setItem('associateId', '7')
    sessionStorage.setItem(
      'associateCpf',
      '52998224725',
    )
  })

  it('should render open voting sessions', () => {
    useOpenSessionsMock.mockReturnValue({
      data: [
        {
          id: 1,
          proposalId: 10,
          proposalTitle:
            'Aquisição de novos equipamentos',
          startsAt: '2026-09-18T10:00:00',
          endsAt: '2099-09-18T15:30:00',
        },
        {
          id: 2,
          proposalId: 11,
          proposalTitle:
            'Reforma da sede administrativa',
          startsAt: '2026-09-18T10:00:00',
          endsAt: '2099-09-18T16:00:00',
        },
      ],
      isLoading: false,
      isError: false,
      refetch: refetchMock,
    })

    renderPage()

    expect(
      screen.getByRole('heading', {
        name: /votações disponíveis/i,
      }),
    ).toBeInTheDocument()

    expect(
      screen.getByText(
        /aquisição de novos equipamentos/i,
      ),
    ).toBeInTheDocument()

    expect(
      screen.getByText(
        /reforma da sede administrativa/i,
      ),
    ).toBeInTheDocument()

    expect(
      screen.getByText('***.***.***-25'),
    ).toBeInTheDocument()

    expect(
      screen.getAllByRole('link', {
        name: /votar/i,
      }),
    ).toHaveLength(2)
  })

  it('should render loading state while sessions are loading', () => {
    useOpenSessionsMock.mockReturnValue({
      data: [],
      isLoading: true,
      isError: false,
      refetch: refetchMock,
    })

    renderPage()

    expect(
      screen.getByRole('status'),
    ).toHaveTextContent(/carregando votações/i)
  })

  it('should render empty state when there are no open sessions', () => {
    useOpenSessionsMock.mockReturnValue({
      data: [],
      isLoading: false,
      isError: false,
      refetch: refetchMock,
    })

    renderPage()

    expect(
      screen.getByText(/nenhuma votação disponível/i),
    ).toBeInTheDocument()

    expect(
      screen.getByText(
        /não há sessões abertas neste momento/i,
      ),
    ).toBeInTheDocument()
  })

  it('should render error state when sessions cannot be loaded', () => {
    useOpenSessionsMock.mockReturnValue({
      data: [],
      isLoading: false,
      isError: true,
      refetch: refetchMock,
    })

    renderPage()

    expect(
      screen.getByText(
        /não foi possível carregar as votações/i,
      ),
    ).toBeInTheDocument()
  })

  it('should refetch sessions when retry button is clicked', async () => {
    const user = userEvent.setup()
    useOpenSessionsMock.mockReturnValue({
      data: [],
      isLoading: false,
      isError: true,
      refetch: refetchMock,
    })

    renderPage()
    await user.click(
      screen.getByRole('button', {
        name: /tentar novamente/i,
      }),
    )

    expect(refetchMock).toHaveBeenCalledOnce()
  })

  it('should clear associate and navigate home when changing associate', async () => {
    const user = userEvent.setup()
    useOpenSessionsMock.mockReturnValue({
      data: [],
      isLoading: false,
      isError: false,
      refetch: refetchMock,
    })

    renderPage()
    await user.click(
      screen.getByRole('button', {
        name: /trocar associado/i,
      }),
    )

    expect(
      sessionStorage.getItem('associateId'),
    ).toBeNull()
    expect(
      sessionStorage.getItem('associateCpf'),
    ).toBeNull()
    expect(navigateMock).toHaveBeenCalledWith('/')
  })
})