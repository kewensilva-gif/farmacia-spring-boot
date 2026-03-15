export interface Customer {
  id: number
  registrationDate: string
  person: {
    id: number
    firstname: string
    lastname: string
    cpf: string
    user: {
      uuid: string
      username: string
      email: string
      role: { name: string }
    }
  }
}

export interface CustomerDto {
  firstname: string
  lastname: string
  cpf: string
  registrationDate: string
  roleName: string
}
