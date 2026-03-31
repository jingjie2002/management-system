export type RoleKey = 'admin' | 'hr' | 'employee'

export const readRoleKeys = (): RoleKey[] => {
  const raw = localStorage.getItem('roleKeys')
  if (!raw) {
    return []
  }

  try {
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch (_error) {
    return []
  }
}

export const hasAnyRole = (roles: string[], expected: RoleKey[]) =>
  expected.some(role => roles.includes(role))

export const canAccessModule = (
  roles: string[],
  module:
    | 'department'
    | 'employee'
    | 'project'
    | 'jobLevel'
    | 'myChange'
    | 'employeeChange'
) => {
  if (roles.includes('admin')) {
    return true
  }

  switch (module) {
    case 'department':
      return false
    case 'employee':
    case 'jobLevel':
    case 'employeeChange':
      return roles.includes('hr')
    case 'project':
    case 'myChange':
      return roles.includes('employee')
    default:
      return false
  }
}
