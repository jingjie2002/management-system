<template>
  <div class="attendance-container">
    <div class="header">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="员工ID">
          <el-input v-model="queryForm.employeeId" placeholder="请输入员工ID" />
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="queryForm.date"
            type="date"
            placeholder="请选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div>
        <el-button type="success" @click="handleCheckIn">上班打卡</el-button>
        <el-button type="warning" @click="handleCheckOut">下班打卡</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
      </div>
    </div>
    
    <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="employeeId" label="员工ID" />
      <el-table-column prop="date" label="日期" />
      <el-table-column prop="checkInTime" label="上班时间" />
      <el-table-column prop="checkOutTime" label="下班时间" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
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
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import request from '../../api/http'
import { ElMessage } from 'element-plus'

const tableData = ref<any[]>([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)
const selectedRows = ref<any[]>([])
const currentEmployeeId = ref<number | null>(null)

const queryForm = ref({
  employeeId: '',
  date: ''
})

const fetchCurrentProfile = async () => {
  const res = await request.get('/employees/profile')
  currentEmployeeId.value = res.data?.employeeId ?? null
  if (!queryForm.value.employeeId && currentEmployeeId.value) {
    queryForm.value.employeeId = String(currentEmployeeId.value)
  }
}

// 加载考勤列表数据
const fetchData = async () => {
  try {
    const res = await request.get('/attendance', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        ...queryForm.value
      }
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

const handleQuery = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  queryForm.value = { employeeId: '', date: '' }
  handleQuery()
}

const handlePageChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

const handleCheckIn = async () => {
  try {
    if (!currentEmployeeId.value) {
      ElMessage.error('当前账号未关联员工档案，无法打卡')
      return
    }
    await request.post('/attendance/checkin', { employeeId: currentEmployeeId.value })
    ElMessage.success('上班打卡成功')
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleCheckOut = async () => {
  try {
    const pendingRecord = tableData.value.find(row =>
      row.employeeId === currentEmployeeId.value && !row.checkOutTime
    )
    if (!pendingRecord?.id) {
      ElMessage.warning('未找到待签退的考勤记录')
      return
    }
    await request.put('/attendance/checkout', { id: pendingRecord.id })
    ElMessage.success('下班打卡成功')
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleSelectionChange = (val: any[]) => {
  selectedRows.value = val
}

const handleBatchDelete = async () => {
  try {
    const ids = selectedRows.value.map(row => row.id)
    await request.delete('/attendance/batch', { data: { ids } })
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const getStatusType = (status: number) => {
  switch (status) {
    case 1: return 'success'
    case 2: return 'warning'
    case 3: return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 1: return '正常'
    case 2: return '迟到'
    case 3: return '早退'
    case 4: return '缺勤'
    default: return '未知'
  }
}

onMounted(async () => {
  await fetchCurrentProfile()
  fetchData()
})
</script>

<style scoped>
.attendance-container {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}
</style>

