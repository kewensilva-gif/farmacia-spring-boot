export interface User {
  uuid: string
  username: string
  email: string
  enabled: boolean
  role: { uuid: string; name: string }
}

export interface UserDto {
  username: string
  email: string
  enabled: boolean
  roleName: string
}

export interface CreateUserRequest {
  username: string
  email: string
  password: string
  role: { uuid: string }
}

export interface UserRegistrationRequest {
  firstName: string
  lastName: string
  cpf: string
  username: string
  email: string
  password: string
  roleName: string
  registrationDate?: string
  hiringDate?: string
  salary?: number
}
