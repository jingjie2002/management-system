<template>
  <div class="performance-container">
    <!-- 顶部查询表单和操作按钮 -->
    <div class="header">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="员工ID">
          <el-input v-model="queryForm.employeeId" placeholder="请输入员工ID" />
        </el-form-item>
        <el-form-item label="绩效月份">
          <el-date-picker
            v-model="queryForm.perfMonth"
            type="month"
            placeholder="请选择月份"
            value-format="YYYY-MM"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 操作按钮区域，仅管理员可见 -->
      <div class="header-actions" v-if="isAdmin">
        <!-- <el-button type="success" @click="handleRecalculate">按公式重算</el-button> -->
        <el-button type="success" @click="handleAdd">新增绩效</el-button>
        <el-button type="primary" @click="handleExport">导出Excel</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
      </div>
    </div>
    
    <!-- 绩效数据表格 -->
    <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="employeeId" label="员工ID" />
      <el-table-column prop="perfMonth" label="绩效月份" />
      <el-table-column prop="baseSalary" label="基本工资" />
      <el-table-column prop="attendanceDays" label="出勤天数" />
      <el-table-column prop="comments" label="评语" />
      <!-- 操作列，根据角色显示不同按钮 -->
      <el-table-column label="操作">
        <template #default="scope">
          <!-- 编辑按钮，仅管理员可见 -->
          <el-button size="small" @click="handleEdit(scope.row)" v-if="isAdmin">编辑</el-button>
          <!-- 删除按钮，仅管理员和HR可见 -->
          <el-button size="small" type="danger" @click="handleDelete(scope.row)" v-if="isAdmin || isHr">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" label-width="120px">
        <el-form-item label="员工ID">
          <el-input v-model="form.employeeId" />
        </el-form-item>
        <el-form-item label="绩效月份">
          <el-date-picker
            v-model="form.perfMonth"
            type="month"
            placeholder="请选择月份"
            value-format="YYYY-MM"
          />
        </el-form-item>
        <el-form-item label="基本工资">
          <el-input-number v-model="form.baseSalary" :precision="2" :step="100" />
        </el-form-item>
        <el-form-item label="出勤天数">
          <el-input-number v-model="form.attendanceDays" :min="0" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input type="textarea" v-model="form.comments" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import request from '../../api/http'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- 权限控制 ---
// 从localStorage获取当前用户的角色键
const roleKeys = ref(JSON.parse(localStorage.getItem('roleKeys') || '[]'))
// 计算属性，判断当前用户是否为管理员
const isAdmin = computed(() => roleKeys.value.includes('admin'))
// 计算属性，判断当前用户是否为HR
const isHr = computed(() => roleKeys.value.includes('hr'))

// --- 数据状态定义 ---
const tableData = ref([]) // 表格数据
const total = ref(0) // 数据总数
const pageSize = ref(10) // 每页条数
const currentPage = ref(1) // 当前页码
const dialogVisible = ref(false) // 新增/编辑弹窗的显示状态
const dialogTitle = ref('新增绩效') // 弹窗标题
const selectedRows = ref([]) // 选中的行

const queryForm = ref({
  employeeId: '',
  perfMonth: ''
})

const form = ref({
  id: null,
  employeeId: '',
  perfMonth: '',
  baseSalary: 5000,
  attendanceDays: 22,
  otherDeductions: 0,
  comments: ''
})

// 拉取绩效分页列表
const fetchData = async () => {
  try {
    const res = await request.get('/performance', {
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
  queryForm.value = { employeeId: '', perfMonth: '' }
  handleQuery()
}

const handlePageChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增绩效'
  form.value = { 
    id: null, 
    employeeId: '', 
    perfMonth: '', 
    baseSalary: 5000, 
    attendanceDays: 22, 
    otherDeductions: 0, 
    comments: '' 
  }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑绩效'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该绩效记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/performance/${row.id}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSelectionChange = (val) => {
  selectedRows.value = val
}

const handleBatchDelete = () => {
  ElMessageBox.confirm('确认批量删除选中的绩效记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await request.delete('/performance/batch', { data: { ids } })
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/performance', form.value)
    } else {
      await request.post('/performance', form.value)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleRecalculate = async () => {
  const month = queryForm.value.perfMonth || form.value.perfMonth
  if (!month) {
    ElMessage.warning('请先选择绩效月份')
    return
  }
  const res = await request.post('/performance/recalculate', null, {
    params: { perfMonth: month }
  })
  ElMessage.success(`已完成重算，共处理 ${res.data} 人`)
  fetchData()
}

const handleExport = async () => {
  try {
    const response = await request.get('/performance/export', {
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'Performance_List.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error(error)
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.performance-container {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}

.header-actions {
  display: flex;
  gap: 8px;
}
</style>
