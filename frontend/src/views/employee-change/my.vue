<template>
  <div class="page">
    <el-card shadow="hover">
      <template #header>
        <div class="header-row">
          <span>发起异动申请</span>
          <el-button type="primary" @click="submitForm">提交申请</el-button>
        </div>
      </template>

      <el-form label-width="100px" class="form-grid">
        <el-form-item label="异动类型">
          <el-select v-model="form.changeType" placeholder="请选择异动类型">
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
            v-model="form.effectiveDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择生效日期"
          />
        </el-form-item>
        <el-form-item label="目标部门">
          <el-select v-model="form.afterDeptId" clearable filterable placeholder="请选择目标部门">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标岗位">
          <el-input v-model="form.afterPosition" placeholder="例如：销售赋能专员" />
        </el-form-item>
        <el-form-item label="目标职级">
          <el-select v-model="form.afterJobLevelId" clearable filterable placeholder="请选择目标职级">
            <el-option
              v-for="item in jobLevels"
              :key="item.id"
              :label="`${item.levelCode} / ${item.levelName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="调整薪资">
          <el-input-number v-model="form.afterSalary" :min="0" :precision="2" :step="500" controls-position="right" />
        </el-form-item>
        <el-form-item label="申请原因" class="form-span-2">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            placeholder="请说明本次调岗、调级或调薪的原因"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <template #header>
        <div class="header-row">
          <span>我的异动记录</span>
          <el-button @click="loadPage">刷新</el-button>
        </div>
      </template>

      <el-table :data="changes" border stripe v-loading="loading">
        <el-table-column prop="changeType" label="异动类型" width="120">
          <template #default="{ row }">
            {{ changeTypeText(row.changeType) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="170">
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
        <el-table-column prop="afterSalary" label="调整薪资" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.afterSalary) }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请原因" min-width="240" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../api/http'
import { applyEmployeeChange, listMyEmployeeChanges } from '../../api/employee-change'
import {
  changeStatusTag,
  changeStatusText,
  changeTypeOptions,
  changeTypeText,
  departmentLabel,
  EmployeeChangeRecord,
  flattenDepartments,
  formatMoney,
  JobLevelOption,
  type DepartmentNode,
  type DepartmentOption
} from '../../utils/employee-change'

const loading = ref(false)
const changes = ref<EmployeeChangeRecord[]>([])
const jobLevels = ref<JobLevelOption[]>([])
const departmentOptions = ref<DepartmentOption[]>([])

const form = reactive({
  changeType: 'TRANSFER',
  reason: '',
  effectiveDate: '',
  afterDeptId: undefined as number | undefined,
  afterPosition: '',
  afterJobLevelId: undefined as number | undefined,
  afterSalary: undefined as number | undefined
})

const loadMetadata = async () => {
  const [departmentRes, jobLevelRes] = await Promise.all([
    request.get('/departments/tree'),
    request.get('/job-levels')
  ])

  departmentOptions.value = flattenDepartments((departmentRes.data || []) as DepartmentNode[])
  jobLevels.value = (jobLevelRes.data || []) as JobLevelOption[]
}

const loadPage = async () => {
  loading.value = true
  try {
    const res = await listMyEmployeeChanges()
    changes.value = (res.data || []) as EmployeeChangeRecord[]
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.changeType = 'TRANSFER'
  form.reason = ''
  form.effectiveDate = ''
  form.afterDeptId = undefined
  form.afterPosition = ''
  form.afterJobLevelId = undefined
  form.afterSalary = undefined
}

const submitForm = async () => {
  if (!form.reason.trim()) {
    ElMessage.warning('请先填写申请原因')
    return
  }
  if (!form.effectiveDate) {
    ElMessage.warning('请选择生效日期')
    return
  }

  await applyEmployeeChange({
    changeType: form.changeType,
    reason: form.reason.trim(),
    effectiveDate: form.effectiveDate,
    afterDeptId: form.afterDeptId ?? null,
    afterPosition: form.afterPosition || '',
    afterJobLevelId: form.afterJobLevelId ?? null,
    afterSalary: form.afterSalary ?? null
  })
  ElMessage.success('异动申请已提交')
  resetForm()
  await loadPage()
}

onMounted(async () => {
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
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 16px;
}

.form-span-2 {
  grid-column: 1 / -1;
}

@media (max-width: 960px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-span-2 {
    grid-column: auto;
  }
}
</style>
