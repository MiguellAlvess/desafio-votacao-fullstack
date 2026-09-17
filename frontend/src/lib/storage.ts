const ASSOCIATE_ID_KEY = 'associateId'
const ASSOCIATE_CPF_KEY = 'associateCpf'

export const associateStorage = {
  set: (id: number, cpf: string) => {
    sessionStorage.setItem(ASSOCIATE_ID_KEY, String(id))
    sessionStorage.setItem(ASSOCIATE_CPF_KEY, cpf)
  },

  getId: () => {
    const id = sessionStorage.getItem(ASSOCIATE_ID_KEY)
    return id ? Number(id) : null
  },

  getCpf: () => {
    return sessionStorage.getItem(ASSOCIATE_CPF_KEY)
  },

  clear: () => {
    sessionStorage.removeItem(ASSOCIATE_ID_KEY)
    sessionStorage.removeItem(ASSOCIATE_CPF_KEY)
  },
}