import { Outlet } from 'react-router-dom'

import Header from '@/components/header';




const AppLayout = () => {
      return (
    <div className="min-h-screen bg-background">
      <Header />
      <main>
        <Outlet />
      </main>
    </div>
  )
}
 
export default AppLayout;