<template>
  <div class="training-container">
    <div class="header">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="培训标题">
          <el-input v-model="queryForm.title" placeholder="请输入培训标题关键词" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div>
        <el-button type="primary" @click="handleAdd">新增培训</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
      </div>
    </div>
    
    <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="培训标题" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column prop="location" label="地点" />
      <el-table-column prop="trainer" label="讲师" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
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
        <el-form-item label="培训标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="培训内容">
          <el-input type="textarea" v-model="form.content" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="讲师">
          <el-input v-model="form.trainer" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="待开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
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
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)
const dialogVisible = ref(false)
const dialogTitle = ref('新增培训')
const selectedRows = ref([])

const queryForm = ref({
  title: ''
})

const form = ref({
  id: null,
  title: '',
  content: '',
  startTime: '',
  endTime: '',
  location: '',
  trainer: '',
  status: 0
})

// 加载培训分页列表
const fetchData = async () => {
  try {
    const res = await request.get('/training', {
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
  queryForm.value = { title: '' }
  handleQuery()
}

const handlePageChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增培训'
  form.value = { id: null, title: '', content: '', startTime: '', endTime: '', location: '', trainer: '', status: 0 }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑培训'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该培训吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/training/${row.id}`)
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
  ElMessageBox.confirm('确认批量删除选中的培训吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await request.delete('/training/batch', { data: { ids } })
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
      await request.put('/training', form.value)
    } else {
      await request.post('/training', form.value)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'info'
    case 1: return 'primary'
    case 2: return 'success'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '待开始'
    case 1: return '进行中'
    case 2: return '已完成'
    default: return '未知'
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.training-container {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}
</style>
