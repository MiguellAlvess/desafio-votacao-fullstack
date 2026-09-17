import { NavLink } from 'react-router-dom'

import { cn } from '@/lib/utils'

const Header = () => {
  const linkClassName = ({ isActive }: { isActive: boolean }) =>
    cn(
      'rounded-md px-3 py-2 text-sm font-medium transition-colors',
      isActive
        ? 'bg-accent text-accent-foreground'
        : 'text-muted-foreground hover:text-foreground',
    )

  return (
    <header className="border-b bg-background">
      <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <NavLink to="/" className="flex items-center">
          <img
            src="/assets/images/logo-voting.svg"
            alt="Voting"
            className="h-9 w-auto"
          />
        </NavLink>
        <nav className="flex items-center gap-1">
          <NavLink to="/votacoes" className={linkClassName}>
            Participar
          </NavLink>
          <NavLink to="/gestao" className={linkClassName}>
            Gestão
          </NavLink>
        </nav>
      </div>
    </header>
  )
}

export default Header