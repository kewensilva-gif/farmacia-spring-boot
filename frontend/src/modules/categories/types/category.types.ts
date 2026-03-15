export interface Category {
  id: number
  name: string
  enabled: boolean
}

export interface CreateCategoryRequest {
  name: string
}
