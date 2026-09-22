import {
  differenceInSeconds,
  format,
} from 'date-fns'

export const formatSessionEndTime = (
  endsAt: string,
) => format(new Date(endsAt), 'HH:mm')

export const getRemainingMinutes = (
  endsAt: string,
) => {
  const remainingSeconds =
    differenceInSeconds(
      new Date(endsAt),
      new Date(),
    )
  if (remainingSeconds <= 0) {
    return 0
  }
  return Math.ceil(
    remainingSeconds / 60,
  )
}