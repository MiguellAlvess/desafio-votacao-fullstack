import {
  render,
  screen,
} from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import {
  describe,
  expect,
  it,
} from 'vitest'

import OngoingSessionsSection from './ongoing-sessions-section'

describe('OngoingSessionsSection', () => {
  it('should render open voting sessions', () => {
    const futureDate = new Date(
      Date.now() + 10 * 60 * 1000,
    ).toISOString()

    render(
      <MemoryRouter>
        <OngoingSessionsSection
          proposals={[
            {
              id: 1,
              title:
                'Aquisição de novos equipamentos',
              description:
                'Compra de equipamentos.',
              createdAt:
                '2026-09-22T10:00:00',
              session: {
                id: 10,
                status: 'OPEN',
                startsAt:
                  '2026-09-22T10:00:00',
                endsAt: futureDate,
              },
            },
          ]}
        />
      </MemoryRouter>,
    )

    expect(
      screen.getByText(
        /sessões em andamento/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.getByText(
        /aquisição de novos equipamentos/i,
      ),
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

  it('should not render closed sessions', () => {
    render(
      <MemoryRouter>
        <OngoingSessionsSection
          proposals={[
            {
              id: 1,
              title:
                'Revisão do fundo de reserva',
              description: null,
              createdAt:
                '2026-09-22T10:00:00',
              session: {
                id: 10,
                status: 'CLOSED',
                startsAt:
                  '2026-09-22T10:00:00',
                endsAt:
                  '2026-09-22T10:01:00',
              },
            },
          ]}
        />
      </MemoryRouter>,
    )

    expect(
      screen.queryByText(
        /sessões em andamento/i,
      ),
    ).not.toBeInTheDocument()
    expect(
      screen.queryByText(
        /revisão do fundo de reserva/i,
      ),
    ).not.toBeInTheDocument()
  })

  it('should render multiple open sessions', () => {
    const futureDate = new Date(
      Date.now() + 15 * 60 * 1000,
    ).toISOString()

    render(
      <MemoryRouter>
        <OngoingSessionsSection
          proposals={[
            {
              id: 1,
              title: 'Pauta 1',
              description: null,
              createdAt:
                '2026-09-22T10:00:00',
              session: {
                id: 10,
                status: 'OPEN',
                startsAt:
                  '2026-09-22T10:00:00',
                endsAt: futureDate,
              },
            },
            {
              id: 2,
              title: 'Pauta 2',
              description: null,
              createdAt:
                '2026-09-22T10:00:00',
              session: {
                id: 11,
                status: 'OPEN',
                startsAt:
                  '2026-09-22T10:00:00',
                endsAt: futureDate,
              },
            },
          ]}
        />
      </MemoryRouter>,
    )

    expect(
      screen.getByText('Pauta 1'),
    ).toBeInTheDocument()
    expect(
      screen.getByText('Pauta 2'),
    ).toBeInTheDocument()
    expect(
      screen.getAllByRole('link', {
        name: /ver resultado/i,
      }),
    ).toHaveLength(2)
  })
})