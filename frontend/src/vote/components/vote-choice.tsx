import {
  Check,
  X,
} from 'lucide-react'

import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

import type { VoteChoice as VoteChoiceType } from '../types/vote'

type VoteChoiceProps = {
  value: VoteChoiceType | null
  onChange: (value: VoteChoiceType) => void
  disabled?: boolean
}

const VoteChoice = ({
  value,
  onChange,
  disabled = false,
}: VoteChoiceProps) => {
  return (
    <div className="grid gap-4 sm:grid-cols-2">
      <Button
        type="button"
        variant="outline"
        disabled={disabled}
        aria-pressed={value === 'YES'}
        className={cn(
          'h-28 flex-col gap-2 text-base',
          value === 'YES' &&
            'border-primary bg-primary/5 text-primary',
        )}
        onClick={() => onChange('YES')}
      >
        <Check className="size-6" />
        SIM
      </Button>
      <Button
        type="button"
        variant="outline"
        disabled={disabled}
        aria-pressed={value === 'NO'}
        className={cn(
          'h-28 flex-col gap-2 text-base',
          value === 'NO' &&
            'border-primary bg-primary/5 text-primary',
        )}
        onClick={() => onChange('NO')}
      >
        <X className="size-6" />
        NÃO
      </Button>
    </div>
  )
}

export default VoteChoice