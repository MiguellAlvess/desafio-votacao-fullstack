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
      '12345678901',
    )
  })

  it('should render vote success message', () => {
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
      screen.getByText(
        /seu voto foi registrado com sucesso/i,
      ),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('link', {
        name: /ver outras votações/i,
      }),
    ).toHaveAttribute(
      'href',
      '/votacoes',
    )
  })
})