<template>
  <div class="department-container">
    <div class="header">
      <div class="header-actions">
        <el-button type="success" @click="handleExport">导出Excel</el-button>
        <el-upload
          :show-file-list="false"
          :before-upload="beforeImport"
          accept=".xlsx,.xls"
          action="#"
        >
          <el-button type="warning">导入Excel</el-button>
        </el-upload>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
        <el-button type="primary" @click="handleAdd">新增部门</el-button>
      </div>
    </div>
    <el-table
      :data="tableData"
      style="width: 100%; margin-bottom: 20px;"
      row-key="id"
      border
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="deptName" label="部门名称" sortable />
      <el-table-column prop="leader" label="负责人" />
      <el-table-column prop="phone" label="联系电话" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="orderNum" label="排序" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" label-width="120px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="form.parentId"
            :data="tableData"
            :props="{ label: 'deptName', value: 'id' }"
            check-strictly
            placeholder="请选择上级部门"
          />
        </el-form-item>
        <el-form-item label="部门名称">
          <el-input v-model="form.deptName" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.orderNum" :min="0" />
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
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const selectedIds = ref<number[]>([])
const form = ref({
  id: null,
  parentId: null,
  deptName: '',
  leader: '',
  phone: '',
  email: '',
  orderNum: 0
})

// 加载部门树形数据
const fetchData = async () => {
  try {
    const res = await request.get('/departments/tree')
    tableData.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增部门'
  form.value = {
    id: null,
    parentId: null,
    deptName: '',
    leader: '',
    phone: '',
    email: '',
    orderNum: 0
  }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑部门'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该部门吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/departments/${row.id}`)
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
  ElMessageBox.confirm(`确认删除已选中的 ${selectedIds.value.length} 个部门吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete('/departments/batch', { data: selectedIds.value })
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

// 导出当前部门数据为Excel
const handleExport = () => {
  window.location.href = '/api/departments/export'
}

// 上传Excel后拦截默认行为，改为走自定义导入接口
const beforeImport = async (rawFile: File) => {
  const formData = new FormData()
  formData.append('file', rawFile)
  const res = await request.post('/departments/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  ElMessage.success(`导入完成：成功 ${res.data.success} 条，跳过 ${res.data.skipped} 条`)
  fetchData()
  return false
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/departments', form.value)
    } else {
      await request.post('/departments', form.value)
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
})
</script>

<style scoped>
.department-container {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
