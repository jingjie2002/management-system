<template>
  <div class="page">
    <el-card shadow="hover">
      <template #header>
        <div class="header-row">
          <span>员工异动中心</span>
          <div class="toolbar">
            <el-button @click="loadPage">刷新</el-button>
            <el-button type="primary" @click="openDirectDialog">直接办理</el-button>
          </div>
        </div>
      </template>

      <div class="filter-row">
        <el-select v-model="filters.changeType" clearable placeholder="按异动类型筛选">
          <el-option
            v-for="item in changeTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select v-model="filters.status" clearable placeholder="按状态筛选">
          <el-option
            v-for="item in changeStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select v-model="filters.employeeId" clearable filterable placeholder="按员工筛选">
          <el-option
            v-for="item in employeeOptions"
            :key="item.id"
            :label="`${item.name} (${item.jobNumber})`"
            :value="item.id"
          />
        </el-select>
      </div>

      <el-table :data="filteredChanges" border stripe v-loading="loading">
        <el-table-column label="员工" min-width="180">
          <template #default="{ row }">
            {{ employeeLabel(row.employeeId) }}
          </template>
        </el-table-column>
        <el-table-column label="异动类型" width="120">
          <template #default="{ row }">
            {{ changeTypeText(row.changeType) }}
          </template>
        </el-table-column>
        <el-table-column label="申请方式" width="120">
          <template #default="{ row }">
            {{ applyModeText(row.applyMode) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="170">
          <template #default="{ row }">
            <el-tag :type="changeStatusTag(row.status)">
              {{ changeStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="effectiveDate" label="生效日期" width="120" />
        <el-table-column label="目标部门" min-width="160">
          <template #default="{ row }">
            {{ departmentLabel(departmentOptions, row.afterDeptId) }}
          </template>
        </el-table-column>
        <el-table-column prop="afterPosition" label="目标岗位" min-width="160" />
        <el-table-column label="目标职级" min-width="160">
          <template #default="{ row }">
            {{ jobLevelLabel(jobLevels, row.afterJobLevelId) }}
          </template>
        </el-table-column>
        <el-table-column label="调整薪资" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.afterSalary) }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="异动原因" min-width="220" show-overflow-tooltip />
        <el-table-column prop="approveRemark" label="审批意见" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <div class="action-row">
              <el-button
                v-if="canApprove(row.status)"
                size="small"
                type="success"
                @click="openApproveDialog(row, true)"
              >
                通过
              </el-button>
              <el-button
                v-if="canApprove(row.status)"
                size="small"
                type="danger"
                @click="openApproveDialog(row, false)"
              >
                驳回
              </el-button>
              <el-button
                v-if="canExecute(row.status)"
                size="small"
                type="primary"
                @click="openExecuteDialog(row)"
              >
                执行生效
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="directDialogVisible" title="直接办理异动" width="720px">
      <el-form label-width="110px" class="dialog-form">
        <el-form-item label="员工">
          <el-select v-model="directForm.employeeId" filterable placeholder="请选择员工">
            <el-option
              v-for="item in employeeOptions"
              :key="item.id"
              :label="`${item.name} (${item.jobNumber})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="异动类型">
          <el-select v-model="directForm.changeType">
            <el-option
              v-for="item in changeTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="生效日期">
          <el-date-picker
            v-model="directForm.effectiveDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择生效日期"
          />
        </el-form-item>
        <el-form-item label="目标部门">
          <el-select v-model="directForm.afterDeptId" clearable filterable placeholder="请选择目标部门">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标岗位">
          <el-input v-model="directForm.afterPosition" placeholder="请输入目标岗位" />
        </el-form-item>
        <el-form-item label="目标职级">
          <el-select v-model="directForm.afterJobLevelId" clearable filterable placeholder="请选择目标职级">
            <el-option
              v-for="item in jobLevels"
              :key="item.id"
              :label="`${item.levelCode} / ${item.levelName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="调整薪资">
          <el-input-number
            v-model="directForm.afterSalary"
            :min="0"
            :precision="2"
            :step="500"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="办理原因" class="form-span-2">
          <el-input
            v-model="directForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入岗位调动、职级调整或薪资联动原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="directDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDirectCreate">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="approveDialogVisible" :title="approveDialogTitle" width="480px">
      <el-form label-width="90px">
        <el-form-item label="审批意见">
          <el-input
            v-model="approveRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApprove">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="executeDialogVisible" title="执行异动生效" width="480px">
      <el-form label-width="90px">
        <el-form-item label="执行说明">
          <el-input
            v-model="executeRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入执行说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="executeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitExecute">确认执行</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../api/http'
import {
  approveEmployeeChange,
  createDirectEmployeeChange,
  executeEmployeeChange,
  listAllEmployeeChanges
} from '../../api/employee-change'
import {
  applyModeText,
  changeStatusOptions,
  changeStatusTag,
  changeStatusText,
  changeTypeOptions,
  changeTypeText,
  departmentLabel,
  EmployeeChangeRecord,
  flattenDepartments,
  formatMoney,
  JobLevelOption,
  jobLevelLabel,
  type DepartmentNode,
  type DepartmentOption
} from '../../utils/employee-change'
import { readRoleKeys } from '../../utils/access'

interface EmployeeOption {
  id: number
  name: string
  jobNumber: string
}

const loading = ref(false)
const changes = ref<EmployeeChangeRecord[]>([])
const departmentOptions = ref<DepartmentOption[]>([])
const jobLevels = ref<JobLevelOption[]>([])
const employeeOptions = ref<EmployeeOption[]>([])
const roleKeys = ref<string[]>([])

const filters = reactive({
  changeType: '',
  status: '',
  employeeId: undefined as number | undefined
})

const directDialogVisible = ref(false)
const approveDialogVisible = ref(false)
const executeDialogVisible = ref(false)
const approveDialogTitle = ref('审批异动')
const pendingApproveId = ref<number>()
const pendingApproveValue = ref(true)
const approveRemark = ref('')
const pendingExecuteId = ref<number>()
const executeRemark = ref('')

const createEmptyDirectForm = () => ({
  employeeId: undefined as number | undefined,
  changeType: 'TRANSFER',
  reason: '',
  effectiveDate: '',
  afterDeptId: undefined as number | undefined,
  afterPosition: '',
  afterJobLevelId: undefined as number | undefined,
  afterSalary: undefined as number | undefined
})

const directForm = reactive(createEmptyDirectForm())

const isAdmin = computed(() => roleKeys.value.includes('admin'))

const filteredChanges = computed(() =>
  changes.value.filter(item => {
    if (filters.changeType && item.changeType !== filters.changeType) {
      return false
    }
    if (filters.status && item.status !== filters.status) {
      return false
    }
    if (filters.employeeId && item.employeeId !== filters.employeeId) {
      return false
    }
    return true
  })
)

const loadMetadata = async () => {
  const [departmentRes, jobLevelRes, employeeRes] = await Promise.all([
    request.get('/departments/tree'),
    request.get('/job-levels'),
    request.get('/employees', {
      params: {
        page: 1,
        size: 200
      }
    })
  ])

  departmentOptions.value = flattenDepartments((departmentRes.data || []) as DepartmentNode[])
  jobLevels.value = (jobLevelRes.data || []) as JobLevelOption[]
  employeeOptions.value = ((employeeRes.data?.records || []) as any[]).map(item => ({
    id: item.id,
    name: item.name,
    jobNumber: item.jobNumber
  }))
}

const loadPage = async () => {
  loading.value = true
  try {
    const res = await listAllEmployeeChanges()
    changes.value = (res.data || []) as EmployeeChangeRecord[]
  } finally {
    loading.value = false
  }
}

const employeeLabel = (employeeId: number) => {
  const item = employeeOptions.value.find(option => option.id === employeeId)
  return item ? `${item.name} (${item.jobNumber})` : `员工 #${employeeId}`
}

const canApprove = (status: string) => status === 'PENDING_APPROVAL'
const canExecute = (status: string) =>
  isAdmin.value && status === 'APPROVED_PENDING_EFFECTIVE'

const resetDirectForm = () => {
  Object.assign(directForm, createEmptyDirectForm())
}

const openDirectDialog = () => {
  resetDirectForm()
  directDialogVisible.value = true
}

const openApproveDialog = (row: EmployeeChangeRecord, approved: boolean) => {
  pendingApproveId.value = row.id
  pendingApproveValue.value = approved
  approveRemark.value = ''
  approveDialogTitle.value = approved ? '审批通过异动' : '驳回异动申请'
  approveDialogVisible.value = true
}

const openExecuteDialog = (row: EmployeeChangeRecord) => {
  pendingExecuteId.value = row.id
  executeRemark.value = ''
  executeDialogVisible.value = true
}

const submitDirectCreate = async () => {
  if (!directForm.employeeId) {
    ElMessage.warning('请选择员工')
    return
  }
  if (!directForm.reason.trim()) {
    ElMessage.warning('请填写办理原因')
    return
  }
  if (!directForm.effectiveDate) {
    ElMessage.warning('请选择生效日期')
    return
  }

  await createDirectEmployeeChange({
    employeeId: directForm.employeeId,
    changeType: directForm.changeType,
    reason: directForm.reason.trim(),
    effectiveDate: directForm.effectiveDate,
    afterDeptId: directForm.afterDeptId ?? null,
    afterPosition: directForm.afterPosition || '',
    afterJobLevelId: directForm.afterJobLevelId ?? null,
    afterSalary: directForm.afterSalary ?? null
  })
  ElMessage.success('异动单已创建，状态为已批准待生效')
  directDialogVisible.value = false
  await loadPage()
}

const submitApprove = async () => {
  if (!pendingApproveId.value) {
    return
  }

  await approveEmployeeChange(pendingApproveId.value, {
    approved: pendingApproveValue.value,
    remark: approveRemark.value.trim()
  })
  ElMessage.success(pendingApproveValue.value ? '异动已审批通过' : '异动已驳回')
  approveDialogVisible.value = false
  await loadPage()
}

const submitExecute = async () => {
  if (!pendingExecuteId.value) {
    return
  }

  await executeEmployeeChange(pendingExecuteId.value, {
    remark: executeRemark.value.trim()
  })
  ElMessage.success('异动已执行生效')
  executeDialogVisible.value = false
  await loadPage()
}

onMounted(async () => {
  roleKeys.value = readRoleKeys()
  await loadMetadata()
  await loadPage()
})
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.toolbar {
  display: flex;
  gap: 8px;
}

.filter-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 240px));
  gap: 12px;
  margin-bottom: 16px;
}

.dialog-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 16px;
}

.form-span-2 {
  grid-column: 1 / -1;
}

.action-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

@media (max-width: 1080px) {
  .filter-row,
  .dialog-form {
    grid-template-columns: 1fr;
  }

  .form-span-2 {
    grid-column: auto;
  }
}
</style>
