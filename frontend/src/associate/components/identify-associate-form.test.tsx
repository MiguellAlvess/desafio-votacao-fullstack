import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import IdentifyAssociateForm from './identify-associate-form'

const navigateMock = vi.fn()
const identifyAssociateMock = vi.fn()

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>(
    'react-router-dom',
  )
  return {
    ...actual,
    useNavigate: () => navigateMock,
  }
})

vi.mock('../hooks/use-identify-associate', () => ({
  useIdentifyAssociate: () => ({
    mutateAsync: identifyAssociateMock,
    isPending: false,
  }),
}))

describe('IdentifyAssociateForm', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    sessionStorage.clear()
  })

  it('should render the identification form', () => {
    render(<IdentifyAssociateForm />)

    expect(
      screen.getByLabelText(/cpf/i),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('button', {
        name: /continuar/i,
      }),
    ).toBeInTheDocument()
  })

  it('should show a validation error when cpf is invalid', async () => {
    const user = userEvent.setup()
    render(<IdentifyAssociateForm />)

    await user.type(
      screen.getByLabelText(/cpf/i),
      '123',
    )
    await user.click(
      screen.getByRole('button', {
        name: /continuar/i,
      }),
    )

    expect(
      await screen.findByText(/informe um cpf válido/i),
    ).toBeInTheDocument()

    expect(identifyAssociateMock).not.toHaveBeenCalled()
  })

  it('should format cpf while typing', async () => {
    const user = userEvent.setup()
    render(<IdentifyAssociateForm />)

    const cpfInput = screen.getByLabelText(/cpf/i)
    await user.type(cpfInput, '52998224725')

    expect(cpfInput).toHaveValue('529.982.247-25')
  })

  it('should identify associate and navigate to open sessions', async () => {
    const user = userEvent.setup()
    identifyAssociateMock.mockResolvedValue({
      id: 7,
      cpf: '52998224725',
    })
    render(<IdentifyAssociateForm />)

    await user.type(
      screen.getByLabelText(/cpf/i),
      '52998224725',
    )
    await user.click(
      screen.getByRole('button', {
        name: /continuar/i,
      }),
    )

    expect(identifyAssociateMock).toHaveBeenCalledWith({
      cpf: '52998224725',
    })
    expect(sessionStorage.getItem('associateId')).toBe('7')
    expect(sessionStorage.getItem('associateCpf')).toBe(
      '52998224725',
    )
    expect(navigateMock).toHaveBeenCalledWith('/votacoes')
  })
})
