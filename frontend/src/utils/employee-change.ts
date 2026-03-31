export interface DepartmentNode {
  id: number
  deptName: string
  children?: DepartmentNode[]
}

export interface DepartmentOption {
  id: number
  label: string
}

export interface JobLevelOption {
  id: number
  levelCode: string
  levelName: string
}

export interface EmployeeChangeRecord {
  id: number
  employeeId: number
  changeType: string
  applyMode: string
  status: string
  reason?: string
  effectiveDate?: string
  afterDeptId?: number
  afterPosition?: string
  afterJobLevelId?: number
  afterSalary?: number
  approveRemark?: string
  createTime?: string
  updateTime?: string
}

export const changeTypeOptions = [
  { label: '综合异动', value: 'TRANSFER' },
  { label: '岗位调动', value: 'POSITION_ADJUST' },
  { label: '职级调整', value: 'JOB_LEVEL_ADJUST' },
  { label: '部门调动', value: 'DEPARTMENT_TRANSFER' }
]

export const changeStatusOptions = [
  { label: '待审批', value: 'PENDING_APPROVAL' },
  { label: '已批准待生效', value: 'APPROVED_PENDING_EFFECTIVE' },
  { label: '已驳回', value: 'REJECTED' },
  { label: '已执行', value: 'EXECUTED' }
]

export const changeTypeText = (value?: string) =>
  changeTypeOptions.find(item => item.value === value)?.label || value || '-'

export const changeStatusText = (value?: string) =>
  changeStatusOptions.find(item => item.value === value)?.label || value || '-'

export const changeStatusTag = (value?: string) => {
  const tags: Record<string, string> = {
    PENDING_APPROVAL: 'warning',
    APPROVED_PENDING_EFFECTIVE: 'primary',
    REJECTED: 'danger',
    EXECUTED: 'success'
  }
  return tags[value || ''] || 'info'
}

export const applyModeText = (value?: string) => {
  const labels: Record<string, string> = {
    EMPLOYEE_APPLY: '员工申请',
    DIRECT_HANDLE: 'HR/管理员办理'
  }
  return labels[value || ''] || value || '-'
}

export const flattenDepartments = (nodes: DepartmentNode[], prefix = ''): DepartmentOption[] =>
  nodes.flatMap(node => {
    const current = [{ id: node.id, label: `${prefix}${node.deptName}` }]
    const children = node.children?.length
      ? flattenDepartments(node.children, `${prefix}${node.deptName} / `)
      : []
    return [...current, ...children]
  })

export const departmentLabel = (options: DepartmentOption[], id?: number) =>
  options.find(item => item.id === id)?.label || '-'

export const jobLevelLabel = (options: JobLevelOption[], id?: number) => {
  const item = options.find(level => level.id === id)
  return item ? `${item.levelCode} / ${item.levelName}` : '-'
}

export const formatMoney = (value?: number | string | null) => {
  if (value === null || value === undefined || value === '') {
    return '-'
  }

  const amount = Number(value)
  if (Number.isNaN(amount)) {
    return String(value)
  }

  return amount.toFixed(2)
}
