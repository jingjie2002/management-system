import request from './http'

export interface EmployeeChangePayload {
  employeeId?: number
  changeType: string
  reason: string
  effectiveDate: string
  afterDeptId?: number | null
  afterPosition?: string
  afterJobLevelId?: number | null
  afterSalary?: number | null
}

export interface EmployeeChangeApprovePayload {
  approved: boolean
  remark?: string
}

export interface EmployeeChangeExecutePayload {
  remark?: string
}

export const listMyEmployeeChanges = () => request.get('/employee-changes/my')

export const listAllEmployeeChanges = () => request.get('/employee-changes')

export const applyEmployeeChange = (payload: EmployeeChangePayload) =>
  request.post('/employee-changes/apply', payload)

export const createDirectEmployeeChange = (payload: EmployeeChangePayload) =>
  request.post('/employee-changes/direct', payload)

export const approveEmployeeChange = (id: number, payload: EmployeeChangeApprovePayload) =>
  request.post(`/employee-changes/${id}/approve`, payload)

export const executeEmployeeChange = (id: number, payload: EmployeeChangeExecutePayload) =>
  request.post(`/employee-changes/${id}/execute`, payload)
