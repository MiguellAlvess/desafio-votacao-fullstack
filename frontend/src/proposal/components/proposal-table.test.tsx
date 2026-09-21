import {
  render,
  screen,
} from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import {
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import ProposalTable from './proposal-table'

vi.mock(
  '@/voting-session/components/open-voting-session-dialog',
  () => ({
    default: ({
      proposalTitle,
    }: {
      proposalId: number
      proposalTitle: string
    }) => (
      <button type="button">
        Abrir sessão - {proposalTitle}
      </button>
    ),
  }),
)

describe('ProposalTable', () => {
  it('should render open session action when proposal has no session', () => {
    render(
      <MemoryRouter>
        <ProposalTable
          proposals={[
            {
              id: 1,
              title: 'Aquisição de equipamentos',
              description: 'Compra de equipamentos.',
              createdAt: '2026-09-21T10:00:00',
              session: null,
            },
          ]}
        />
      </MemoryRouter>,
    )

    expect(
      screen.getByText(/sem sessão/i),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('button', {
        name: /abrir sessão - aquisição de equipamentos/i,
      }),
    ).toBeInTheDocument()
  })

  it('should render open session status and view session link', () => {
    render(
      <MemoryRouter>
        <ProposalTable
          proposals={[
            {
              id: 1,
              title: 'Aquisição de equipamentos',
              description: 'Compra de equipamentos.',
              createdAt: '2026-09-21T10:00:00',
              session: {
                id: 10,
                status: 'OPEN',
                startsAt: '2026-09-21T10:00:00',
                endsAt: '2026-09-21T10:10:00',
              },
            },
          ]}
        />
      </MemoryRouter>,
    )

    expect(
      screen.getByText(/sessão aberta/i),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('link', {
        name: /ver sessão/i,
      }),
    ).toHaveAttribute(
      'href',
      '/votacoes/10',
    )
  })

  it('should render closed session status and result link', () => {
    render(
      <MemoryRouter>
        <ProposalTable
          proposals={[
            {
              id: 1,
              title: 'Aquisição de equipamentos',
              description: 'Compra de equipamentos.',
              createdAt: '2026-09-21T10:00:00',
              session: {
                id: 10,
                status: 'CLOSED',
                startsAt: '2026-09-21T10:00:00',
                endsAt: '2026-09-21T10:01:00',
              },
            },
          ]}
        />
      </MemoryRouter>,
    )

    expect(
      screen.getByText(/sessão encerrada/i),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('link', {
        name: /ver resultado/i,
      }),
    ).toHaveAttribute(
      'href',
      '/votacoes/10/resultado',
    )
  })

  it('should render proposal information', () => {
    render(
      <MemoryRouter>
        <ProposalTable
          proposals={[
            {
              id: 1,
              title: 'Revisão do fundo de reserva',
              description:
                'Análise da proposta de revisão.',
              createdAt: '2026-09-21T10:00:00',
              session: null,
            },
          ]}
        />
      </MemoryRouter>,
    )

    expect(
      screen.getByText(
        /revisão do fundo de reserva/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.getByText(
        /análise da proposta de revisão/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.getByText('21/09/2026'),
    ).toBeInTheDocument()
  })
})