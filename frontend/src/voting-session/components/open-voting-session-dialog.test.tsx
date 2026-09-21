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

import OpenVotingSessionDialog from './open-voting-session-dialog'

const openVotingSessionMock = vi.fn()

vi.mock('../hooks/use-open-voting-session', () => ({
  useOpenVotingSession: () => ({
    mutateAsync: openVotingSessionMock,
    isPending: false,
  }),
}))

describe('OpenVotingSessionDialog', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should open voting session dialog', async () => {
    const user = userEvent.setup()

    render(
      <OpenVotingSessionDialog
        proposalId={1}
        proposalTitle="Aquisição de equipamentos"
      />,
    )
    await user.click(
      screen.getByRole('button', {
        name: /abrir sessão/i,
      }),
    )

    expect(
      screen.getByRole('heading', {
        name: /abrir sessão de votação/i,
      }),
    ).toBeInTheDocument()

    expect(
      screen.getByText(
        /aquisição de equipamentos/i,
      ),
    ).toBeInTheDocument()
  })

  it('should open session with informed duration', async () => {
    const user = userEvent.setup()
    openVotingSessionMock.mockResolvedValue({
      id: 1,
      proposalId: 1,
      startsAt: '2026-09-21T10:00:00',
      endsAt: '2026-09-21T10:05:00',
    })

    render(
      <OpenVotingSessionDialog
        proposalId={1}
        proposalTitle="Aquisição de equipamentos"
      />,
    )
    await user.click(
      screen.getByRole('button', {
        name: /abrir sessão/i,
      }),
    )
    await user.type(
      screen.getByLabelText(/duração em minutos/i),
      '5',
    )
    await user.click(
      screen.getByRole('button', {
        name: /^abrir sessão$/i,
      }),
    )

    expect(
      openVotingSessionMock,
    ).toHaveBeenCalledWith({
      proposalId: 1,
      data: {
        durationInMinutes: 5,
      },
    })
  })

  it('should use default duration when duration is not informed', async () => {
    const user = userEvent.setup()
    openVotingSessionMock.mockResolvedValue({
      id: 1,
      proposalId: 1,
      startsAt: '2026-09-21T10:00:00',
      endsAt: '2026-09-21T10:01:00',
    })

    render(
      <OpenVotingSessionDialog
        proposalId={1}
        proposalTitle="Aquisição de equipamentos"
      />,
    )
    await user.click(
      screen.getByRole('button', {
        name: /abrir sessão/i,
      }),
    )
    await user.click(
      screen.getByRole('button', {
        name: /^abrir sessão$/i,
      }),
    )

    expect(
      openVotingSessionMock,
    ).toHaveBeenCalledWith({
      proposalId: 1,
      data: {
        durationInMinutes: undefined,
      },
    })
  })

  it('should show validation error when duration is invalid', async () => {
    const user = userEvent.setup()

    render(
      <OpenVotingSessionDialog
        proposalId={1}
        proposalTitle="Aquisição de equipamentos"
      />,
    )
    await user.click(
      screen.getByRole('button', {
        name: /abrir sessão/i,
      }),
    )
    await user.type(
      screen.getByLabelText(/duração em minutos/i),
      '0',
    )
    await user.click(
      screen.getByRole('button', {
        name: /^abrir sessão$/i,
      }),
    )

    expect(
      await screen.findByText(
        /a duração deve ser maior que zero/i,
      ),
    ).toBeInTheDocument()
    expect(
      openVotingSessionMock,
    ).not.toHaveBeenCalled()
  })
})