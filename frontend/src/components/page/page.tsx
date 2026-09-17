import type { ComponentProps } from 'react'

import { cn } from '@/lib/utils'

export function Page({
  className,
  ...props
}: ComponentProps<'div'>) {
  return (
    <div
      className={cn(
        'mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 lg:px-8',
        className,
      )}
      {...props}
    />
  )
}

export function PageHeader({
  className,
  ...props
}: ComponentProps<'header'>) {
  return (
    <header
      className={cn(
        'flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between',
        className,
      )}
      {...props}
    />
  )
}

export function PageHeaderContent({
  className,
  ...props
}: ComponentProps<'div'>) {
  return (
    <div
      className={cn('space-y-1', className)}
      {...props}
    />
  )
}

export function PageTitle({
  className,
  ...props
}: ComponentProps<'h1'>) {
  return (
    <h1
      className={cn(
        'text-2xl font-semibold tracking-tight sm:text-3xl',
        className,
      )}
      {...props}
    />
  )
}

export function PageDescription({
  className,
  ...props
}: ComponentProps<'p'>) {
  return (
    <p
      className={cn(
        'text-sm text-muted-foreground',
        className,
      )}
      {...props}
    />
  )
}

export function PageActions({
  className,
  ...props
}: ComponentProps<'div'>) {
  return (
    <div
      className={cn(
        'flex shrink-0 items-center gap-2',
        className,
      )}
      {...props}
    />
  )
}

export function PageContent({
  className,
  ...props
}: ComponentProps<'div'>) {
  return (
    <div
      className={cn('mt-8 space-y-8', className)}
      {...props}
    />
  )
}