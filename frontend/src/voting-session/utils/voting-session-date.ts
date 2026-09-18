import { differenceInMinutes, format } from 'date-fns'

export const formatSessionEndTime = (endsAt: string) => {
  return format(new Date(endsAt), 'HH:mm')
}

export const getRemainingMinutes = (endsAt: string) => {
  const remainingMinutes = differenceInMinutes(
    new Date(endsAt),
    new Date(),
  )
  return Math.max(0, remainingMinutes)
}