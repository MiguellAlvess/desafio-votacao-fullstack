import {
  render,
  screen,
} from '@testing-library/react'
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
} from 'vitest'

import VoteSuccessPage from './vote-success-page'

describe('VoteSuccessPage', () => {
  beforeEach(() => {
    sessionStorage.setItem(
      'associateId',
      '5',
    )

    sessionStorage.setItem(
      'associateCpf',
      '52998224725',
    )
  })

  it('should render vote success actions', () => {
    render(
      <MemoryRouter
        initialEntries={[
          '/votacoes/10/sucesso',
        ]}
      >
        <Routes>
          <Route
            path="/votacoes/:votingSessionId/sucesso"
            element={<VoteSuccessPage />}
          />
        </Routes>
      </MemoryRouter>,
    )

    expect(
      screen.getByRole('heading', {
        name: /voto registrado/i,
      }),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('link', {
        name: /ver outras votações/i,
      }),
    ).toHaveAttribute(
      'href',
      '/votacoes',
    )
    expect(
      screen.getByRole('link', {
        name: /consultar resultado/i,
      }),
    ).toHaveAttribute(
      'href',
      '/votacoes/10/resultado',
    )
  })
})