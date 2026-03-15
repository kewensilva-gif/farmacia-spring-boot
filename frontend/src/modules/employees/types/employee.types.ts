export interface Employee {
  id: number
  hiringDate: string
  terminationDate?: string
  salary: number
  person: {
    id: number
    firstname: string
    lastname: string
    cpf: string
    user: {
      uuid: string
      username: string
      email: string
    }
  }
}

export interface EmployeeDto {
  firstname: string
  lastname: string
  cpf: string
  hiringDate: string
  terminationDate?: string
  salary: number
  roleName: string
}
