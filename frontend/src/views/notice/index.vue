<template>
  <div class="notice-container">
    <div class="header">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="标题">
          <el-input v-model="queryForm.title" placeholder="请输入公告标题关键词" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="header-actions" v-if="hasManagePermission">
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
        <el-button type="primary" @click="handleAdd">新增公告</el-button>
      </div>
    </div>
    
    <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="标题" />
      <el-table-column label="置顶" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isTop === 1 ? 'danger' : 'info'">
            {{ scope.row.isTop === 1 ? '已置顶' : '普通' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型">
        <template #default="scope">
          <el-tag :type="scope.row.type === 1 ? 'primary' : 'warning'">
            {{ scope.row.type === 1 ? '通知' : '公告' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '启用' : '关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <template v-if="hasManagePermission">
            <el-button size="small" type="warning" @click="handleTop(scope.row)">
              {{ scope.row.isTop === 1 ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" label-width="120px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="form.content" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option label="通知" :value="1" />
            <el-option label="公告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="启用" :value="1" />
            <el-option label="关闭" :value="0" />
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
// 计算属性，判断当前用户是否有管理权限
const hasManagePermission = computed(() => isAdmin.value || isHr.value)

const tableData = ref([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)
const dialogVisible = ref(false)
const dialogTitle = ref('新增公告')
const selectedIds = ref<number[]>([])

const queryForm = ref({
  title: ''
})

const form = ref({
  id: null,
  title: '',
  content: '',
  type: 1,
  status: 1,
  isTop: 0
})

// 加载公告列表
const fetchData = async () => {
  try {
    const res = await request.get('/notices', {
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
  dialogTitle.value = '新增公告'
  form.value = { id: null, title: '', content: '', type: 1, status: 1, isTop: 0 }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑公告'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/notices/${row.id}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

// 表格多选变更，用于批量操作
const handleSelectionChange = (rows: any[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

// 批量删除公告
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确认删除已选中的 ${selectedIds.value.length} 条公告吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await request.delete('/notices/batch', { data: selectedIds.value })
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchData()
  })
}

// 切换公告置顶状态
const handleTop = async (row: any) => {
  await request.put(`/notices/top/${row.id}`, null, {
    params: { top: row.isTop !== 1 }
  })
  ElMessage.success(row.isTop === 1 ? '已取消置顶' : '已置顶')
  fetchData()
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await request.put('/notices', form.value)
    } else {
      await request.post('/notices', form.value)
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
.notice-container {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 8px;
}
</style>
