<template>
  <div class="employee-container">
    <div class="header">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="姓名">
          <el-input v-model="queryForm.name" placeholder="请输入姓名关键词" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="queryForm.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id' }"
            check-strictly
            placeholder="请选择部门"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div>
        <el-button type="success" @click="handleExport">导出Excel</el-button>
        <el-button @click="handleImportTemplate">下载导入模板</el-button>
        <el-upload
          class="upload-demo"
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          accept=".xlsx,.xls"
          :show-file-list="false"
        >
          <el-button type="warning">导入Excel</el-button>
        </el-upload>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
        <el-button type="primary" @click="handleAdd">新增员工</el-button>
      </div>
    </div>
    
    <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="jobNumber" label="工号" />
      <el-table-column prop="deptName" label="部门" />
      <el-table-column prop="position" label="岗位" />
      <el-table-column prop="baseSalary" label="基本工资" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '在职' : '离职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="form.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id' }"
            check-strictly
          />
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="form.jobNumber" />
        </el-form-item>
        <el-form-item label="岗位">
          <el-input v-model="form.position" />
        </el-form-item>
        <el-form-item label="基本工资">
          <el-input-number v-model="form.baseSalary" :min="0" :step="100" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="2" />
          </el-select>
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
import { ref, onMounted } from 'vue'
import request from '../../api/http'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const deptTree = ref([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)
const dialogVisible = ref(false)
const dialogTitle = ref('新增员工')
const selectedIds = ref<number[]>([])

const queryForm = ref({
  name: '',
  deptId: null
})

const form = ref({
  id: null,
  name: '',
  deptId: null,
  jobNumber: '',
  position: '',
  baseSalary: 5000,
  status: 1
})

// 拉取员工分页与筛选数据
const fetchData = async () => {
  try {
    const res = await request.get('/employees', {
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

const fetchDeptTree = async () => {
  try {
    const res = await request.get('/departments/tree')
    deptTree.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleQuery = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  queryForm.value = { name: '', deptId: null }
  handleQuery()
}

const handlePageChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增员工'
  form.value = { id: null, name: '', deptId: null, jobNumber: '', position: '', baseSalary: 5000, status: 1 }
  dialogVisible.value = true
}

const handleExport = async () => {
  try {
    const response = await request.get('/employees/export', {
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'Employee_List.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error(error)
    ElMessage.error('导出失败')
  }
}

const handleImportTemplate = async () => {
  try {
    const response = await request.get('/employees/import-template', {
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'Employee_Import_Template.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error(error)
    ElMessage.error('下载模板失败')
  }
}

const handleFileChange = async (file: any) => {
  // 实现Excel导入功能
  const formData = new FormData()
  formData.append('file', file.raw)
  
  try {
    await request.post('/employees/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    ElMessage.success('导入成功')
    fetchData()
  } catch (error) {
    console.error(error)
    ElMessage.error('导入失败')
  }
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑员工'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该员工吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/employees/${row.id}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSelectionChange = (rows: any[]) => {
  selectedIds.value = rows.map(row => row.id)
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确认删除已选中的 ${selectedIds.value.length} 位员工吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete('/employees/batch', { data: selectedIds.value })
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/employees', form.value)
    } else {
      await request.post('/employees', form.value)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchData()
  fetchDeptTree()
})
</script>

<style scoped>
.employee-container {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}
</style>
