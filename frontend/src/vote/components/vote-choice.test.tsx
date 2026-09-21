import {
  render,
  screen,
} from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import {
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import VoteChoice from './vote-choice'

describe('VoteChoice', () => {
  it('should select yes vote', async () => {
    const user = userEvent.setup()
    const onChange = vi.fn()

    render(
      <VoteChoice
        value={null}
        onChange={onChange}
      />,
    )
    await user.click(
      screen.getByRole('button', {
        name: /sim/i,
      }),
    )

    expect(onChange).toHaveBeenCalledWith(
      'YES',
    )
  })

  it('should select no vote', async () => {
    const user = userEvent.setup()
    const onChange = vi.fn()

    render(
      <VoteChoice
        value={null}
        onChange={onChange}
      />,
    )
    await user.click(
      screen.getByRole('button', {
        name: /não/i,
      }),
    )

    expect(onChange).toHaveBeenCalledWith(
      'NO',
    )
  })

  it('should indicate selected vote', () => {
    render(
      <VoteChoice
        value="YES"
        onChange={vi.fn()}
      />,
    )

    expect(
      screen.getByRole('button', {
        name: /sim/i,
      }),
    ).toHaveAttribute(
      'aria-pressed',
      'true',
    )
  })
})