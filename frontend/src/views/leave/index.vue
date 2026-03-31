<template>
  <div class="leave-container">
    <div class="header">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="员工ID">
          <el-input v-model="queryForm.employeeId" placeholder="请输入员工ID" />
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryForm.status" clearable>
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div>
        <el-button type="primary" @click="dialogVisible = true">发起请假</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
      </div>
    </div>

    <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="employeeId" label="员工ID" />
      <el-table-column prop="type" label="请假类型">
        <template #default="scope">
          {{ typeText(scope.row.type) }}
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column prop="reason" label="请假原因" />
      <el-table-column prop="status" label="审批状态">
        <template #default="scope">
          <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="approver" label="审批人" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <template v-if="hasApprovalPermission">
            <el-button size="small" type="success" @click="handleApprove(scope.row, true)" :disabled="scope.row.status !== 0">通过</el-button>
            <el-button size="small" type="danger" @click="handleApprove(scope.row, false)" :disabled="scope.row.status !== 0">驳回</el-button>
          </template>
          <template v-else>
            <span>无权限</span>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      @current-change="handlePageChange"
    />

    <el-dialog v-model="dialogVisible" title="请假申请">
      <el-form :model="form" label-width="120px">
        <el-form-item label="员工ID">
          <el-input-number v-model="form.employeeId" :min="1" />
        </el-form-item>
        <el-form-item label="请假类型">
          <el-select v-model="form.type">
            <el-option label="病假" :value="1" />
            <el-option label="事假" :value="2" />
            <el-option label="年假" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="请假原因">
          <el-input type="textarea" v-model="form.reason" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import request from '../../api/http'
import { ElMessage } from 'element-plus'

// --- 权限控制 ---
// 从localStorage获取当前用户的角色键
const roleKeys = ref(JSON.parse(localStorage.getItem('roleKeys') || '[]'))
// 计算属性，判断当前用户是否为管理员
const isAdmin = computed(() => roleKeys.value.includes('admin'))
// 计算属性，判断当前用户是否为HR
const isHr = computed(() => roleKeys.value.includes('hr'))
// 计算属性，判断当前用户是否有审批权限
const hasApprovalPermission = computed(() => isAdmin.value || isHr.value)

const tableData = ref<any[]>([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)
const dialogVisible = ref(false)
const selectedRows = ref<any[]>([])
const currentEmployeeId = ref<number | null>(null)
const currentUsername = ref(localStorage.getItem('username') || '')

const queryForm = ref({
  employeeId: '',
  status: null as number | null
})

const form = ref({
  employeeId: undefined as number | undefined,
  type: 1,
  startTime: '',
  endTime: '',
  reason: ''
})

const fetchCurrentProfile = async () => {
  const res = await request.get('/employees/profile')
  currentEmployeeId.value = res.data?.employeeId ?? null
  if (currentEmployeeId.value) {
    form.value.employeeId = currentEmployeeId.value
    if (!hasApprovalPermission.value) {
      queryForm.value.employeeId = String(currentEmployeeId.value)
    }
  }
}

// 加载请假分页数据
const fetchData = async () => {
  const res = await request.get('/leave', {
    params: {
      page: currentPage.value,
      size: pageSize.value,
      employeeId: queryForm.value.employeeId || undefined,
      status: queryForm.value.status
    }
  })
  tableData.value = res.data.records
  total.value = res.data.total
}

const submitApply = async () => {
  if (!form.value.employeeId) {
    ElMessage.error('当前账号未关联员工档案，无法发起请假')
    return
  }
  await request.post('/leave/apply', form.value)
  ElMessage.success('请假申请已提交')
  dialogVisible.value = false
  form.value = {
    employeeId: currentEmployeeId.value ?? undefined,
    type: 1,
    startTime: '',
    endTime: '',
    reason: ''
  }
  fetchData()
}

const handleApprove = async (row: any, approved: boolean) => {
  await request.put(`/leave/approve/${row.id}`, null, {
    params: {
      approver: currentUsername.value || 'system',
      approved
    }
  })
  ElMessage.success(approved ? '审批通过' : '审批驳回')
  fetchData()
}

const handleSelectionChange = (val: any[]) => {
  selectedRows.value = val
}

const handleBatchDelete = async () => {
  try {
    const ids = selectedRows.value.map(row => row.id)
    await request.delete('/leave/batch', { data: { ids } })
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleQuery = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  queryForm.value = { employeeId: '', status: null }
  handleQuery()
}

const handlePageChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

const typeText = (type: number) => {
  if (type === 1) return '病假'
  if (type === 2) return '事假'
  if (type === 3) return '年假'
  return '未知'
}

const statusText = (status: number) => {
  if (status === 0) return '待审批'
  if (status === 1) return '已通过'
  if (status === 2) return '已驳回'
  return '未知'
}

const statusType = (status: number) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  return 'info'
}

onMounted(async () => {
  await fetchCurrentProfile()
  fetchData()
})
</script>

<style scoped>
.leave-container {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}
</style>
