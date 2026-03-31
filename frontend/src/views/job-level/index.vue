<template>
  <div class="job-level-container">
    <div class="header">
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">新增职级</el-button>
      </div>
      <el-button @click="fetchData">刷新</el-button>
    </div>

    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="levelCode" label="职级编码" width="140" />
      <el-table-column prop="levelName" label="职级名称" min-width="160" />
      <el-table-column prop="levelRank" label="排序" width="100" />
      <el-table-column prop="salaryMin" label="最低薪资" width="130">
        <template #default="scope">
          {{ formatMoney(scope.row.salaryMin) }}
        </template>
      </el-table-column>
      <el-table-column prop="salaryMax" label="最高薪资" width="130">
        <template #default="scope">
          {{ formatMoney(scope.row.salaryMax) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">
            {{ statusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="职级编码">
          <el-input v-model="form.levelCode" placeholder="例如 P1" />
        </el-form-item>
        <el-form-item label="职级名称">
          <el-input v-model="form.levelName" placeholder="例如 初级工程师" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.levelRank" :min="1" :step="1" />
        </el-form-item>
        <el-form-item label="最低薪资">
          <el-input-number v-model="form.salaryMin" :min="0" :precision="2" :step="100" />
        </el-form-item>
        <el-form-item label="最高薪资">
          <el-input-number v-model="form.salaryMax" :min="0" :precision="2" :step="100" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可填写说明" />
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
import { onMounted, ref } from 'vue'
import request from '../../api/http'
import { ElMessage, ElMessageBox } from 'element-plus'

type JobLevelForm = {
  id: number | null
  levelCode: string
  levelName: string
  levelRank: number
  salaryMin: number
  salaryMax: number
  status: number
  remark: string
}

const tableData = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增职级')

const createEmptyForm = (): JobLevelForm => ({
  id: null,
  levelCode: '',
  levelName: '',
  levelRank: 1,
  salaryMin: 0,
  salaryMax: 0,
  status: 1,
  remark: ''
})

const form = ref<JobLevelForm>(createEmptyForm())

const fetchData = async () => {
  try {
    const res = await request.get('/job-levels')
    tableData.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增职级'
  form.value = createEmptyForm()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑职级'
  form.value = {
    id: row.id ?? null,
    levelCode: row.levelCode ?? '',
    levelName: row.levelName ?? '',
    levelRank: row.levelRank ?? 1,
    salaryMin: row.salaryMin ?? 0,
    salaryMax: row.salaryMax ?? 0,
    status: row.status ?? 1,
    remark: row.remark ?? ''
  }
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除职级「${row.levelName || row.levelCode}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/job-levels/${row.id}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  try {
    const payload = {
      ...form.value,
      salaryMin: form.value.salaryMin ?? 0,
      salaryMax: form.value.salaryMax ?? 0
    }

    if (payload.id) {
      await request.put('/job-levels', payload)
    } else {
      await request.post('/job-levels', payload)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const formatMoney = (value: unknown) => {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  const num = Number(value)
  if (Number.isNaN(num)) {
    return String(value)
  }
  return num.toFixed(2)
}

const statusText = (status: number) => (status === 1 ? '启用' : '停用')

const statusTagType = (status: number) => (status === 1 ? 'success' : 'info')

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.job-level-container {
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
  align-items: center;
  gap: 8px;
}
</style>
