export const formatCpf = (value: string) => {
  const digits = value.replace(/\D/g, '').slice(0, 11)
  return digits
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

export const removeCpfMask = (value: string) => {
  return value.replace(/\D/g, '')
}

export const maskCpf = (value: string) => {
  const digits = removeCpfMask(value)
  if (digits.length !== 11) {
    return '***.***.***-**'
  }
  return `***.***.***-${digits.slice(-2)}`
}

export const isValidCpf = (value: string) => {
  const cpf = removeCpfMask(value)

  if (cpf.length !== 11) {
    return false
  }

  if (/^(\d)\1{10}$/.test(cpf)) {
    return false
  }

const calculateDigit = (
    base: string,
    factor: number,
  ) => {
    const total = base
      .split('')
      .reduce(
        (sum, digit) =>
          sum + Number(digit) * factor--,
        0,
      )
    const remainder = (total * 10) % 11
    return remainder === 10 ? 0 : remainder
  }
  const firstDigit = calculateDigit(
    cpf.slice(0, 9),
    10,
  )
  if (firstDigit !== Number(cpf[9])) {
    return false
  }
  const secondDigit = calculateDigit(
    cpf.slice(0, 10),
    11,
  )
  return secondDigit === Number(cpf[10])
}