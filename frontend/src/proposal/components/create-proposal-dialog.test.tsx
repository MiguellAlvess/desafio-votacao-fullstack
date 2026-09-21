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

import CreateProposalDialog from './create-proposal-dialog'

const createProposalMock = vi.fn()

vi.mock('../hooks/use-create-proposal', () => ({
  useCreateProposal: () => ({
    mutateAsync: createProposalMock,
    isPending: false,
  }),
}))

describe('CreateProposalDialog', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should open create proposal dialog', async () => {
    const user = userEvent.setup()

    render(<CreateProposalDialog />)
    await user.click(
      screen.getByRole('button', {
        name: /nova pauta/i,
      }),
    )

    expect(
      screen.getByRole('heading', {
        name: /nova pauta/i,
      }),
    ).toBeInTheDocument()
    expect(
      screen.getByLabelText(/título da pauta/i),
    ).toBeInTheDocument()
  })

  it('should show validation error when title is empty', async () => {
    const user = userEvent.setup()

    render(<CreateProposalDialog />)
    await user.click(
      screen.getByRole('button', {
        name: /nova pauta/i,
      }),
    )
    await user.click(
      screen.getByRole('button', {
        name: /cadastrar pauta/i,
      }),
    )

    expect(
      await screen.findByText(
        /informe o título da pauta/i,
      ),
    ).toBeInTheDocument()

    expect(
      createProposalMock,
    ).not.toHaveBeenCalled()
  })

  it('should create a proposal with valid data', async () => {
    const user = userEvent.setup()
    createProposalMock.mockResolvedValue({
      id: 1,
      title: 'Aquisição de equipamentos',
      description:
        'Compra de novos equipamentos.',
      createdAt:
        '2026-09-21T10:00:00',
    })

    render(<CreateProposalDialog />)
    await user.click(
      screen.getByRole('button', {
        name: /nova pauta/i,
      }),
    )
    await user.type(
      screen.getByLabelText(/título da pauta/i),
      'Aquisição de equipamentos',
    )
    await user.type(
      screen.getByLabelText(/descrição/i),
      'Compra de novos equipamentos.',
    )
    await user.click(
      screen.getByRole('button', {
        name: /cadastrar pauta/i,
      }),
    )

    expect(
      createProposalMock,
    ).toHaveBeenCalledWith({
      title: 'Aquisição de equipamentos',
      description:
        'Compra de novos equipamentos.',
    })
  })
})